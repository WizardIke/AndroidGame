package wizardike.assignment3.graphics;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import wizardike.assignment3.Engine;
import wizardike.assignment3.worlds.World;
import wizardike.assignment3.worlds.WorldUpdatingSystem;

public class GraphicsManager extends GLSurfaceView implements GLSurfaceView.Renderer {
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

    private WorldUpdatingSystem worldUpdatingSystem = new WorldUpdatingSystem();
    private GeometryBuffer geometryBuffer;
    private LightBuffer lightBuffer;
    private TextureManager textureManager;
    private float viewScaleX, viewScaleY;
    private long[] frameSyncObjects;
    private int currentBufferIndex;
    private int openGLVersion;
    private int depthRenderBufferHandle;
    private int depthTextureHandle;

    public GraphicsManager(Context context){
        super(context);
        init();
    }

    public GraphicsManager(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init() {
        // Create an OpenGL ES 3.1, 3.0 or 2.0 context
        setEGLContextFactory(new EGLContextCreator());
        //make sure the default framebuffer doesn't have a depth buffer
        setEGLConfigChooser(false);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(this);
    }

    public void setEngine(Engine engine) {
        textureManager = new TextureManager(engine);
    }

    public int getOpenGlVersion() {
        return openGLVersion;
    }

    public GeometryBuffer getGeometryBuffer() {
        return geometryBuffer;
    }

    public int getCurrentBufferIndex() {
        return currentBufferIndex;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }

    public boolean isDepthTextureSupported() {
        return depthRenderBufferHandle == -1;
    }

    public LightBuffer getLightBuffer() {
        return lightBuffer;
    }

    public float getViewScaleX() {
        return viewScaleX;
    }

    public float getViewScaleY() {
        return viewScaleY;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClearDepthf(0.0f);

        if(openGLVersion >= 30 && Build.VERSION.SDK_INT >= 18) {
            frameSyncObjects = new long[2];
            frameSyncObjects[0] = -1;
            frameSyncObjects[1] = -1;
            currentBufferIndex = 0;
        }

        int[] temp = new int[1];
        GLES20.glGenTextures(1, temp, 0);
        depthTextureHandle = temp[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureHandle);
        //the buffer size is the same as the screen size so GL_NEAREST sampling is good.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //Non-power-of-two textures might only support GL_CLAMP_TO_EDGE.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        depthRenderBufferHandle = -1;
        if(openGLVersion <= 20) {
            String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
            if(!extensions.contains("OES_depth_texture")) {
                //set depth textures as not supported
                GLES20.glGenRenderbuffers(1, temp, 0);
                depthRenderBufferHandle = temp[0];
            }
        }

        geometryBuffer = new GeometryBuffer(depthTextureHandle, depthRenderBufferHandle,
                openGLVersion, getResources());

        lightBuffer = new LightBuffer(getResources(), depthTextureHandle,
                depthRenderBufferHandle == -1);

        textureManager.reload();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if(openGLVersion >= 30 && Build.VERSION.SDK_INT >= 18) {
            startRendering30();
            worldUpdatingSystem.update();
            endRendering30();
        } else {
            worldUpdatingSystem.update();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        if(width > height) {
            viewScaleX = 2 / MIN_VIEW_PORT_LENGTH_IN_METERS;
            viewScaleY = height / width * viewScaleX;
        } else {
            viewScaleY = 2 / MIN_VIEW_PORT_LENGTH_IN_METERS;
            viewScaleX = width / height * viewScaleY;
        }
        GLES20.glViewport(0, 0, width, height);

        if(depthRenderBufferHandle != -1) {
            //create depth buffer
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBufferHandle);
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

            //create 'fake' depth texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureHandle);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA4, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        } else {
            //create depth texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureHandle);
            GLES20.glTexImage2D (GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT16, width, height, 0,
                    GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);
        }

        geometryBuffer.setSize(width, height);
        lightBuffer.setSize(width, height);
    }

    public void addWorld(World world) {
        worldUpdatingSystem.addWorld(world);
    }

    public void removeWorld(World world) {
        worldUpdatingSystem.removeWorld(world);
    }

    public interface Callback {
        void onLoadComplete(GraphicsManager graphicsManager);
    }

    @TargetApi(18)
    private void startRendering30() {
        //wait for buffer
        if(frameSyncObjects[currentBufferIndex] != -1) {
            int waitReturn = GLES30.GL_UNSIGNALED;
            while (waitReturn != GLES30.GL_ALREADY_SIGNALED && waitReturn != GLES30.GL_CONDITION_SATISFIED)
            {
                waitReturn = GLES30.glClientWaitSync(frameSyncObjects[currentBufferIndex],
                        GLES30.GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
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
}
