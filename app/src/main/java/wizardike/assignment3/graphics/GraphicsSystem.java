package wizardike.assignment3.graphics;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import wizardike.assignment3.R;

import static java.util.Collections.sort;
import static wizardike.assignment3.geometry.IntersectionTesting.isIntersecting;

public class GraphicsSystem extends GLSurfaceView implements GLSurfaceView.Renderer {
    private class EGLContextCreator implements EGLContextFactory {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        private static final int PREFERRED_GL_VERSION = 3;
        private static final int MIN_GL_VERSION = 2;

        @Override
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            int[] attribute_list = {EGL_CONTEXT_CLIENT_VERSION, PREFERRED_GL_VERSION,
                    EGL10.EGL_NONE };
            EGLContext context;
            if(Build.VERSION.SDK_INT >= 18) {
                // attempt to create a OpenGL ES 3.0 context
                context = egl.eglCreateContext(
                        display, eglConfig, EGL10.EGL_NO_CONTEXT, attribute_list);
                if(context != null) {
                    int[] version = new int[2];
                    GLES30.glGetIntegerv(GLES30.GL_MAJOR_VERSION, version, 0);
                    GLES30.glGetIntegerv(GLES30.GL_MINOR_VERSION, version, 1);
                    if (version[0] > 3 || (version[0] == 3 && version[1] >= 1)) {
                        // We have at least ES 3.1.
                        openGLVersion = 31;
                    } else {
                        openGLVersion = 30;
                    }
                    return context;
                }
            }

            openGLVersion = 20;
            //create a OpenGL ES 2.0 context
            attribute_list[1] = MIN_GL_VERSION;
            context = egl.eglCreateContext(
                    display, eglConfig, EGL10.EGL_NO_CONTEXT, attribute_list);
            return context;
        }

        @Override
        public void destroyContext(EGL10 egl10, EGLDisplay eglDisplay, EGLContext eglContext) {
            egl10.eglDestroyContext(eglDisplay, eglContext);
        }
    }
    private static final float MIN_VIEW_PORT_LENGTH_IN_METERS = 7.0f;
    private static final int SPRITE_BATCH_SIZE = 128;
    private static final int SPRITE_SIZE_IN_BYTES30 = 8 * 4;
    private static final int SPRITE_SIZE_IN_BYTES20 = 30 * 4;
    private final ArrayList<UpdateListener> updateListeners = new ArrayList<>();
    private final ArrayList<Sprite> sprites = new ArrayList<>();
    private final ArrayList<Sprite> transparentSprites = new ArrayList<>();
    private final ArrayList<PointLight> pointLights = new ArrayList<>();
    private final ArrayList<CircleShadowCaster> circleShadowCasters = new ArrayList<>();
    private final ArrayList<LineShadowCaster> lineShadowCasters = new ArrayList<>();
    private Camera camera = new Camera();
    private GeometryBuffer geometryBuffer = null;
    private LightBuffer lightBuffer = null;
    private TextureManager textureManager = new TextureManager(1024);
    private FloatBuffer spriteBuffer;
    private int[] spriteBufferHandles;
    private long[] frameSyncObjects;
    private int currentBufferIndex;
    private int openGLVersion;
    private int depthRenderBufferHandle;
    private int depthTextureHandle = -1;

    private int spriteDepthProgram;
    private int spriteDepthCameraScaleAndOffsetHandle;
    private int spriteDepthPositionHandle;
    private int spriteDepthTexCoordinatesHandle;

    private int opaqueSpriteProgram;
    private int opaqueSpritePositionHandle;
    private int opaqueSpriteTexCoordinatesHandle;
    private int opaqueSpriteCameraScaleAndOffsetHandle;

    private int transparentSpriteProgram;
    private int transparentSpritePositionHandle;
    private int transparentSpriteTexCoordinatesHandle;
    private int transparentSpriteCameraScaleAndOffsetHandle;

    public GraphicsSystem(Context context){
        super(context);
        init();
    }

    public GraphicsSystem(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init() {
        // Create an OpenGL ES 3.1 or 2.0 context
        setEGLContextFactory(new EGLContextCreator());
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClearDepthf(0.0f);

        String sprite_vertex_source;
        if(openGLVersion >= 30 && Build.VERSION.SDK_INT >= 18) {
            spriteBufferHandles = new int[2]; //increasing this to 3 buffers might increase performance at the cost of latency and memory
            frameSyncObjects = new long[2];
            frameSyncObjects[0] = -1;
            frameSyncObjects[1] = -1;
            currentBufferIndex = 0;

            GLES30.glGenBuffers(2, spriteBufferHandles, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, spriteBufferHandles[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES30, null,
                    GLES20.GL_DYNAMIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, spriteBufferHandles[1]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES30, null,
                    GLES20.GL_DYNAMIC_DRAW);

            sprite_vertex_source = ShaderLoader.loadStringFromRawResource(getResources(), R.raw.geometry_v30);
        } else {
            spriteBuffer = ByteBuffer.allocateDirect(SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES20)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            sprite_vertex_source = ShaderLoader.loadStringFromRawResource(getResources(), R.raw.geometry_v20);
        }
        int sprite_vertex_shader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, sprite_vertex_source);
        String sprite_opaque_frag_source = ShaderLoader.loadStringFromRawResource(getResources(), R.raw.sprite_opaque_geometry_f);
        int sprite_opaque_frag_shader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, sprite_opaque_frag_source);
        String sprite_transparent_frag_source = ShaderLoader.loadStringFromRawResource(getResources(), R.raw.sprite_transparent_geometry_f);
        int sprite_transparent_frag_shader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, sprite_transparent_frag_source);

        depthRenderBufferHandle = -2;
        if(openGLVersion <= 20) {
            String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
            if(!extensions.contains("OES_depth_texture")) {
                depthRenderBufferHandle = -1; //set depth textures as not supported

                final int sprite_depth_frag_shader = ShaderLoader.loadShaderFromResource(getResources(),
                        R.raw.sprite_depth_f, GLES20.GL_FRAGMENT_SHADER);
                spriteDepthProgram = GLES20.glCreateProgram();
                GLES20.glAttachShader(spriteDepthProgram, sprite_vertex_shader);
                GLES20.glAttachShader(spriteDepthProgram, sprite_depth_frag_shader);
                GLES20.glLinkProgram(spriteDepthProgram);

                spriteDepthPositionHandle = GLES20.glGetAttribLocation(spriteDepthProgram, "position");
                spriteDepthTexCoordinatesHandle = GLES20.glGetAttribLocation(spriteDepthProgram, "texCoordinates");
                spriteDepthCameraScaleAndOffsetHandle = GLES20.glGetUniformLocation(spriteDepthProgram, "scaleAndOffset");
            }
        }

        opaqueSpriteProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(opaqueSpriteProgram, sprite_vertex_shader);
        GLES20.glAttachShader(opaqueSpriteProgram, sprite_opaque_frag_shader);
        GLES20.glLinkProgram(opaqueSpriteProgram);

        transparentSpriteProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(transparentSpriteProgram, sprite_vertex_shader);
        GLES20.glAttachShader(transparentSpriteProgram, sprite_transparent_frag_shader);
        GLES20.glLinkProgram(transparentSpriteProgram);

        opaqueSpritePositionHandle = GLES20.glGetAttribLocation(opaqueSpriteProgram, "position");
        opaqueSpriteTexCoordinatesHandle = GLES20.glGetAttribLocation(opaqueSpriteProgram, "texCoordinates");
        opaqueSpriteCameraScaleAndOffsetHandle = GLES20.glGetUniformLocation(opaqueSpriteProgram, "scaleAndOffset");

        transparentSpritePositionHandle = GLES20.glGetAttribLocation(transparentSpriteProgram, "position");
        transparentSpriteTexCoordinatesHandle = GLES20.glGetAttribLocation(transparentSpriteProgram, "texCoordinates");
        transparentSpriteCameraScaleAndOffsetHandle = GLES20.glGetUniformLocation(transparentSpriteProgram, "scaleAndOffset");
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if(openGLVersion >= 30 && Build.VERSION.SDK_INT >= 18) {
            startRendering30();

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, spriteBufferHandles[currentBufferIndex]);
            final int flags = GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_FLUSH_EXPLICIT_BIT
                    | GLES30.GL_MAP_UNSYNCHRONIZED_BIT;
            Buffer buffer = GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER, 0,
                    SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES30, flags);
            FloatBuffer floatBuffer = ((ByteBuffer)buffer).asFloatBuffer();

            //create geometry and add it to the buffer
            int spritesOffset = buffer.position() * 4;
            int numberOfSpritesPending = drawSprites30(sprites, floatBuffer);
            sortTransparentSprites();
            int transparentSpritesOffset = buffer.position() * 4;
            int transparentNumberOfSpritesPending = drawSprites30(transparentSprites, floatBuffer);

            GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureManager.getTextureHandle());
            if(depthRenderBufferHandle == -2) { //depth texture is supported
                geometryBuffer.bindColorPass();
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                GLES30.glUseProgram(opaqueSpriteProgram);
                GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                        -camera.positionX, -camera.positionY);
                GLES30.glEnableVertexAttribArray(opaqueSpritePositionHandle);
                GLES30.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
                GLES30.glVertexAttribDivisor(opaqueSpritePositionHandle, 1);
                GLES30.glVertexAttribDivisor(opaqueSpriteTexCoordinatesHandle, 1);

                GLES30.glDepthMask(true);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDisable(GLES20.GL_BLEND);
                GLES30.glDepthFunc(GLES30.GL_GEQUAL);
                submitSprites30(spritesOffset, numberOfSpritesPending, opaqueSpritePositionHandle,
                        opaqueSpriteTexCoordinatesHandle);

                GLES30.glDisableVertexAttribArray(opaqueSpritePositionHandle);
                GLES30.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);

                GLES30.glDepthMask(false);
            } else {
                geometryBuffer.bindDepthPass();
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                GLES30.glUseProgram(spriteDepthProgram);
                GLES20.glUniform4f(spriteDepthCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                        -camera.positionX, -camera.positionY);
                GLES30.glEnableVertexAttribArray(spriteDepthPositionHandle);
                GLES30.glEnableVertexAttribArray(spriteDepthTexCoordinatesHandle);
                GLES30.glVertexAttribDivisor(spriteDepthPositionHandle, 1);
                GLES30.glVertexAttribDivisor(spriteDepthTexCoordinatesHandle, 1);

                GLES30.glDepthMask(true);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDisable(GLES20.GL_BLEND);
                GLES30.glDepthFunc(GLES30.GL_GEQUAL);
                submitSprites30(spritesOffset, numberOfSpritesPending, spriteDepthPositionHandle,
                        spriteDepthTexCoordinatesHandle);

                GLES30.glDisableVertexAttribArray(spriteDepthPositionHandle);
                GLES30.glDisableVertexAttribArray(spriteDepthTexCoordinatesHandle);


                geometryBuffer.bindColorPass();
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES30.glDepthMask(false);
                GLES30.glUseProgram(opaqueSpriteProgram);
                GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                        -camera.positionX, -camera.positionY);
                GLES30.glEnableVertexAttribArray(opaqueSpritePositionHandle);
                GLES30.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
                GLES30.glVertexAttribDivisor(opaqueSpritePositionHandle, 1);
                GLES30.glVertexAttribDivisor(opaqueSpriteTexCoordinatesHandle, 1);

                submitSprites30(spritesOffset, numberOfSpritesPending, opaqueSpritePositionHandle,
                        opaqueSpriteTexCoordinatesHandle);

                GLES30.glDisableVertexAttribArray(opaqueSpritePositionHandle);
                GLES30.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
            }

            GLES30.glUseProgram(transparentSpriteProgram);
            //set camera constants on gpu
            GLES20.glUniform4f(transparentSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                    -camera.positionX, -camera.positionY);
            GLES30.glEnableVertexAttribArray(transparentSpritePositionHandle);
            GLES30.glEnableVertexAttribArray(transparentSpriteTexCoordinatesHandle);
            GLES30.glVertexAttribDivisor(transparentSpritePositionHandle, 1);
            GLES30.glVertexAttribDivisor(transparentSpriteTexCoordinatesHandle, 1);

            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES30.glEnable(GLES30.GL_BLEND);
            submitSprites30(transparentSpritesOffset, transparentNumberOfSpritesPending,
                    transparentSpritePositionHandle, transparentSpriteTexCoordinatesHandle);

            GLES30.glDisableVertexAttribArray(transparentSpritePositionHandle);
            GLES30.glDisableVertexAttribArray(transparentSpriteTexCoordinatesHandle);

            endRendering30();
        } else {
            //create geometry and add it to the buffer
            int spritesOffset = spriteBuffer.position() * 4;
            int numberOfSpritesPending = drawSprites20(sprites, spriteBuffer);
            sortTransparentSprites();
            int transparentSpritesOffset = spriteBuffer.position() * 4;
            int transparentNumberOfSpritesPending = drawSprites20(transparentSprites, spriteBuffer);


            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureManager.getTextureHandle());
            if(depthRenderBufferHandle == -2) { //depth texture is supported
                geometryBuffer.bindColorPass();
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                GLES20.glUseProgram(opaqueSpriteProgram);
                //set camera constants on gpu
                GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                        -camera.positionX, -camera.positionY);
                GLES20.glEnableVertexAttribArray(opaqueSpritePositionHandle);
                GLES20.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);

                GLES20.glDepthMask(true);
                GLES20.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDepthFunc(GLES30.GL_GEQUAL);
                GLES20.glDisable(GLES20.GL_BLEND);
                submitSprites20(spritesOffset, numberOfSpritesPending,
                        opaqueSpritePositionHandle, opaqueSpriteTexCoordinatesHandle);

                GLES20.glDisableVertexAttribArray(opaqueSpritePositionHandle);
                GLES20.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);

                GLES20.glDepthMask(false);
            } else {
                geometryBuffer.bindDepthPass();
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                GLES20.glUseProgram(spriteDepthProgram);
                GLES20.glUniform4f(spriteDepthCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                        -camera.positionX, -camera.positionY);
                GLES20.glEnableVertexAttribArray(spriteDepthPositionHandle);
                GLES20.glEnableVertexAttribArray(spriteDepthTexCoordinatesHandle);

                GLES20.glDepthMask(true);
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                GLES20.glDisable(GLES20.GL_BLEND);
                GLES20.glDepthFunc(GLES20.GL_GEQUAL);
                submitSprites20(spritesOffset, numberOfSpritesPending, spriteDepthPositionHandle,
                        spriteDepthTexCoordinatesHandle);

                GLES20.glDisableVertexAttribArray(spriteDepthPositionHandle);
                GLES20.glDisableVertexAttribArray(spriteDepthTexCoordinatesHandle);


                geometryBuffer.bindColorPass();
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glDepthMask(false);
                GLES20.glUseProgram(opaqueSpriteProgram);
                GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                        -camera.positionX, -camera.positionY);
                GLES20.glEnableVertexAttribArray(opaqueSpritePositionHandle);
                GLES20.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);

                submitSprites20(spritesOffset, numberOfSpritesPending, opaqueSpritePositionHandle,
                        opaqueSpriteTexCoordinatesHandle);

                GLES20.glDisableVertexAttribArray(opaqueSpritePositionHandle);
                GLES20.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
            }

            GLES30.glUseProgram(transparentSpriteProgram);
            //set camera constants on gpu
            GLES20.glUniform4f(transparentSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                    -camera.positionX, -camera.positionY);
            GLES20.glEnableVertexAttribArray(transparentSpritePositionHandle);
            GLES20.glEnableVertexAttribArray(transparentSpriteTexCoordinatesHandle);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glEnable(GLES20.GL_BLEND);
            submitSprites20(transparentSpritesOffset, transparentNumberOfSpritesPending,
                    transparentSpritePositionHandle, transparentSpriteTexCoordinatesHandle);

            GLES20.glDisableVertexAttribArray(transparentSpritePositionHandle);
            GLES20.glDisableVertexAttribArray(transparentSpriteTexCoordinatesHandle);
        }

        final float viewPortHalfWidth = 1 / camera.scaleX;
        final float viewPortHalfHeight = 1 / camera.scaleY;
        lightBuffer.prepareToRenderLights();
        for(PointLight light : pointLights) {
            if(isIntersecting(light.positionX, light.positionY, light.radius,
                    camera.positionX, camera.positionY, viewPortHalfWidth, viewPortHalfHeight)) {
                lightBuffer.renderLight(light, camera);
                for(CircleShadowCaster shadowCaster : circleShadowCasters) {
                    lightBuffer.renderCircleShadow(shadowCaster, light);
                }
                for(LineShadowCaster shadowCaster : lineShadowCasters) {
                    lightBuffer.renderLineShadow(shadowCaster, light);
                }
                lightBuffer.applyLighting(geometryBuffer.getColorTextureHandle(), light, camera);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        if(width > height) {
            camera.scaleX = 2 / MIN_VIEW_PORT_LENGTH_IN_METERS;
            camera.scaleY = height / width * camera.scaleX;
        } else {
            camera.scaleY = 2 / MIN_VIEW_PORT_LENGTH_IN_METERS;
            camera.scaleX = width / height * camera.scaleY;
        }
        GLES20.glViewport(0, 0, width, height);

        if(depthRenderBufferHandle != -2) {
            //resize/create depth buffer
            if(depthRenderBufferHandle == -1) {
                int[] temp = new int[1];
                GLES20.glGenRenderbuffers(1, temp, 0);
                depthRenderBufferHandle = temp[0];
            }
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBufferHandle);
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

            //resize/create 'fake' depth texture
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA4, width, height, 0,
                    GLES20.GL_RGBA4, GLES20.GL_UNSIGNED_BYTE, null);
            //the buffer size is the same as the screen size so GL_NEAREST sampling is good.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //Non-power-of-two textures might only support GL_CLAMP_TO_EDGE.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        } else {
            //resize/create depth texture
            if(depthTextureHandle == -1) {
                int[] temp = new int[1];
                GLES20.glGenTextures(1, temp, 0);
                depthTextureHandle = temp[0];
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureHandle);
            GLES20.glTexImage2D (GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT16, width, height, 0,
                    GLES20.GL_DEPTH_COMPONENT16, GLES20.GL_UNSIGNED_SHORT, null);
            //the buffer size is the same as the screen size so GL_NEAREST sampling is good.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //Non-power-of-two textures might only support GL_CLAMP_TO_EDGE.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        if(geometryBuffer == null) {
            geometryBuffer = new GeometryBuffer(width, height, depthTextureHandle, depthRenderBufferHandle);
        } else {
            geometryBuffer.resize(width, height);
        }
        if(lightBuffer == null) {
            lightBuffer = new LightBuffer(width, height, getResources(), depthTextureHandle,
                    depthRenderBufferHandle == -2);
        } else {
            lightBuffer.resize(width, height);
        }
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public void removeSprite(Sprite sprite) {
        int index = sprites.indexOf(sprite);
        sprites.set(index, sprites.get(sprites.size() - 1));
        sprites.remove(sprites.size() - 1);
    }

    public void addTransparentSprite(Sprite sprite) {
        transparentSprites.add(sprite);
    }

    public void removeTransparentSprite(Sprite sprite) {
        int index = transparentSprites.indexOf(sprite);
        transparentSprites.set(index, transparentSprites.get(transparentSprites.size() - 1));
        transparentSprites.remove(transparentSprites.size() - 1);
    }

    public void addPointLight(PointLight light) {
        pointLights.add(light);
    }

    public void removePointLight(PointLight light) {
        int index = pointLights.indexOf(light);
        pointLights.set(index, pointLights.get(pointLights.size() - 1));
        pointLights.remove(pointLights.size() - 1);
    }

    public void addCircleShadowCaster(CircleShadowCaster circleShadowCaster) {
        circleShadowCasters.add(circleShadowCaster);
    }

    public void removeCircleShadowCaster(CircleShadowCaster circleShadowCaster) {
        int index = circleShadowCasters.indexOf(circleShadowCaster);
        circleShadowCasters.set(index, circleShadowCasters.get(circleShadowCasters.size() - 1));
        circleShadowCasters.remove(circleShadowCasters.size() - 1);
    }

    public void addLineShadowCaster(LineShadowCaster lineShadowCaster) {
        lineShadowCasters.add(lineShadowCaster);
    }

    public void removeLineShadowCaster(LineShadowCaster lineShadowCaster) {
        int index = lineShadowCasters.indexOf(lineShadowCaster);
        lineShadowCasters.set(index, lineShadowCasters.get(lineShadowCasters.size() - 1));
        lineShadowCasters.remove(lineShadowCasters.size() - 1);
    }

    public void setCameraPosition(float x, float y) {
        camera.positionX = x;
        camera.positionY = y;
    }

    public void addUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        int index = updateListeners.indexOf(listener);
        updateListeners.set(index, updateListeners.get(updateListeners.size() - 1));
        updateListeners.remove(updateListeners.size() - 1);
    }

    private static int drawSprites20(List<Sprite> sprites, FloatBuffer spriteBuffer) {
        int numberOfSpritesPending = 0;
        for(Sprite sprite : sprites) {
            spriteBuffer.put(sprite.positionX);
            spriteBuffer.put(sprite.positionY);
            spriteBuffer.put(0.0f);
            spriteBuffer.put(sprite.texU);
            spriteBuffer.put(sprite.texV);

            spriteBuffer.put(sprite.positionX);
            spriteBuffer.put(sprite.positionY + sprite.height);
            spriteBuffer.put(sprite.height);
            spriteBuffer.put(sprite.texU);
            spriteBuffer.put(sprite.texV + sprite.texWidth);

            spriteBuffer.put(sprite.positionX + sprite.width);
            spriteBuffer.put(sprite.positionY);
            spriteBuffer.put(0.0f);
            spriteBuffer.put(sprite.texU + sprite.texWidth);
            spriteBuffer.put(sprite.texV);


            spriteBuffer.put(sprite.positionX + sprite.width);
            spriteBuffer.put(sprite.positionY);
            spriteBuffer.put(0.0f);
            spriteBuffer.put(sprite.texU + sprite.texWidth);
            spriteBuffer.put(sprite.texV);

            spriteBuffer.put(sprite.positionX);
            spriteBuffer.put(sprite.positionY + sprite.height);
            spriteBuffer.put(sprite.height);
            spriteBuffer.put(sprite.texU);
            spriteBuffer.put(sprite.texV + sprite.texWidth);

            spriteBuffer.put(sprite.positionX + sprite.width);
            spriteBuffer.put(sprite.positionY + sprite.height);
            spriteBuffer.put(sprite.height);
            spriteBuffer.put(sprite.texU + sprite.texWidth);
            spriteBuffer.put(sprite.texV + sprite.texHeight);
            numberOfSpritesPending++;
        }
        return numberOfSpritesPending;
    }

    @TargetApi(18)
    private void startRendering30() {
        //wait for buffer
        if(frameSyncObjects[currentBufferIndex] != -1) {
            int waitReturn = GLES30.GL_UNSIGNALED;
            while (waitReturn != GLES30.GL_ALREADY_SIGNALED && waitReturn != GLES30.GL_CONDITION_SATISFIED)
            {
                waitReturn = GLES30.glClientWaitSync(frameSyncObjects[currentBufferIndex],
                        GLES30.GL_SYNC_FLUSH_COMMANDS_BIT, 1);
            }
            GLES30.glDeleteSync(frameSyncObjects[currentBufferIndex]);
        }
    }

    @TargetApi(18)
    private void endRendering30() {
        //queue signal buffer on gpu
        frameSyncObjects[currentBufferIndex] = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE,
                0);
        currentBufferIndex = currentBufferIndex ^ 1;
    }

    private int drawSprites30(List<Sprite> sprites, FloatBuffer buffer) {
        int numberOfSpritesPending = 0;
        for(Sprite sprite : sprites) {
            buffer.put(sprite.positionX);
            buffer.put(sprite.positionY);
            buffer.put(sprite.width);
            buffer.put(sprite.height);
            buffer.put(sprite.texU);
            buffer.put(sprite.texV);
            buffer.put(sprite.texWidth);
            buffer.put(sprite.texHeight);
            numberOfSpritesPending++;
        }
        return numberOfSpritesPending;
    }

    private void submitSprites20(int offsetInBytes, int spriteCount,
                                 int spritePositionHandle, int spriteTexCoordinatesHandle) {
        final int oldPosition = spriteBuffer.position();
        spriteBuffer.position(offsetInBytes / 4);
        GLES20.glVertexAttribPointer(spritePositionHandle, 3,
                GLES20.GL_FLOAT, false, 5, spriteBuffer);

        spriteBuffer.position(offsetInBytes / 4 + 3);
        GLES20.glVertexAttribPointer(spriteTexCoordinatesHandle, 2,
                GLES20.GL_FLOAT, false, 5, spriteBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, spriteCount * 6);
        spriteBuffer.position(oldPosition);
    }

    @TargetApi(18)
    private void submitSprites30(int offsetInBytes, int spriteCount,
                                 int spritePositionHandle, int spriteTexCoordinatesHandle) {

        GLES30.glFlushMappedBufferRange(GLES30.GL_ARRAY_BUFFER, offsetInBytes,
                spriteCount * SPRITE_SIZE_IN_BYTES30);
        GLES30.glVertexAttribPointer(spritePositionHandle, 4,
                GLES30.GL_FLOAT, false, 8, offsetInBytes);

        GLES30.glVertexAttribPointer(spriteTexCoordinatesHandle, 4,
                GLES30.GL_FLOAT, false, 8, offsetInBytes + 4);


        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0, 4, spriteCount);
    }

    private void sortTransparentSprites() {
        sort(transparentSprites, new Comparator<Sprite>() {
            @Override
            public int compare(Sprite s1, Sprite s2) {
                final float lowerY1 = s1.positionY + s1.height;
                final float lowerY2 = s2.positionY + s2.height;
                if(lowerY1 > lowerY2){
                    return 1;
                }
                else if (lowerY1 == lowerY2){
                    return 0;
                }
                return -1;
            }
        });
    }
}
