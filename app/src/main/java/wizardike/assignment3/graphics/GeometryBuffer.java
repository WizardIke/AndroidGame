package wizardike.assignment3.graphics;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.io.Closeable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import wizardike.assignment3.R;

/**
 * Format: rgb hold base color and a is 1 for ground and 0 for everything else.
 */
public class GeometryBuffer implements Closeable {
    private static final int SPRITE_BATCH_SIZE = 128;
    private static final int SPRITE_SIZE_IN_BYTES30 = 8 * 4;
    private static final int SPRITE_SIZE_IN_BYTES20 = 30 * 4;

    private int depthPassFrameBufferHandle = -2;
    private int frameBufferHandle;
    private int colorTextureHandle;

    private FloatBuffer spriteBuffer;
    private int[] spriteBufferHandles;

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

    GeometryBuffer(int width, int height, int depthTextureHandle, int depthRenderBufferHandle,
                   int openGLVersion, Resources resources) {

        int[] temp = new int[1];
        GLES20.glGenFramebuffers(1, temp, 0);
        frameBufferHandle = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        colorTextureHandle = temp[0];

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, colorTextureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB5_A1, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //the buffer size is the same as the screen size so GL_NEAREST sampling is good.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //Non-power-of-two textures might only support GL_CLAMP_TO_EDGE.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, colorTextureHandle, 0);
        if(depthRenderBufferHandle != -2) {
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderBufferHandle);

            //bind depthTextureHandle as color to depthPassFrameBufferHandle
            GLES20.glGenFramebuffers(1, temp, 0);
            depthPassFrameBufferHandle = temp[0];
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, depthTextureHandle, 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderBufferHandle);
        } else {
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_TEXTURE_2D, depthTextureHandle, 0);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


        int sprite_vertex_shader;
        int sprite_opaque_frag_shader;
        int sprite_transparent_frag_shader;
        if(openGLVersion >= 30) {
            spriteBufferHandles = new int[2]; //increasing this to 3 buffers might increase performance at the cost of latency and memory

            GLES20.glGenBuffers(2, spriteBufferHandles, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, spriteBufferHandles[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES30, null,
                    GLES20.GL_DYNAMIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, spriteBufferHandles[1]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES30, null,
                    GLES20.GL_DYNAMIC_DRAW);

            sprite_vertex_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.geometry_v30, GLES20.GL_VERTEX_SHADER);
            sprite_opaque_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.sprite_opaque_geometry_f30, GLES20.GL_FRAGMENT_SHADER);
            sprite_transparent_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.sprite_transparent_geometry_f30, GLES20.GL_FRAGMENT_SHADER);
        } else {
            spriteBuffer = ByteBuffer.allocateDirect(SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES20)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            sprite_vertex_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.geometry_v20, GLES20.GL_VERTEX_SHADER);
            sprite_opaque_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.sprite_opaque_geometry_f20, GLES20.GL_FRAGMENT_SHADER);
            sprite_transparent_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.sprite_transparent_geometry_f20, GLES20.GL_FRAGMENT_SHADER);
        }

        if(depthRenderBufferHandle == -1) { //depth textures as not supported
            int sprite_depth_frag_shader;
            if(openGLVersion >= 30) {
                sprite_depth_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                        R.raw.sprite_depth_f30, GLES20.GL_FRAGMENT_SHADER);
            } else {
                sprite_depth_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                        R.raw.sprite_depth_f20, GLES20.GL_FRAGMENT_SHADER);
            }
            spriteDepthProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(spriteDepthProgram, sprite_vertex_shader);
            GLES20.glAttachShader(spriteDepthProgram, sprite_depth_frag_shader);
            GLES20.glLinkProgram(spriteDepthProgram);

            spriteDepthPositionHandle = GLES20.glGetAttribLocation(spriteDepthProgram, "position");
            spriteDepthTexCoordinatesHandle = GLES20.glGetAttribLocation(spriteDepthProgram, "texCoordinates");
            spriteDepthCameraScaleAndOffsetHandle = GLES20.glGetUniformLocation(spriteDepthProgram, "scaleAndOffset");
        }

        opaqueSpriteProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(opaqueSpriteProgram, sprite_vertex_shader);
        GLES20.glAttachShader(opaqueSpriteProgram, sprite_opaque_frag_shader);
        ShaderLoader.linkProgram(opaqueSpriteProgram);

        transparentSpriteProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(transparentSpriteProgram, sprite_vertex_shader);
        GLES20.glAttachShader(transparentSpriteProgram, sprite_transparent_frag_shader);
        ShaderLoader.linkProgram(transparentSpriteProgram);

        opaqueSpritePositionHandle = GLES20.glGetAttribLocation(opaqueSpriteProgram, "positionAndWidthAndHeight");
        opaqueSpriteTexCoordinatesHandle = GLES20.glGetAttribLocation(opaqueSpriteProgram, "texCoordinates");
        opaqueSpriteCameraScaleAndOffsetHandle = GLES20.glGetUniformLocation(opaqueSpriteProgram, "scaleAndOffset");

        transparentSpritePositionHandle = GLES20.glGetAttribLocation(transparentSpriteProgram, "positionAndWidthAndHeight");
        transparentSpriteTexCoordinatesHandle = GLES20.glGetAttribLocation(transparentSpriteProgram, "texCoordinates");
        transparentSpriteCameraScaleAndOffsetHandle = GLES20.glGetUniformLocation(transparentSpriteProgram, "scaleAndOffset");
    }

    @TargetApi(18)
    public void prepareToGenerateMeshes30(final int currentBufferIndex) {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, spriteBufferHandles[currentBufferIndex]);
        final int flags = GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_FLUSH_EXPLICIT_BIT
                | GLES30.GL_MAP_UNSYNCHRONIZED_BIT;
        Buffer buffer = GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER, 0,
                SPRITE_BATCH_SIZE * SPRITE_SIZE_IN_BYTES30, flags);
        spriteBuffer = ((ByteBuffer)buffer).asFloatBuffer();
    }

    public int getCurrentMeshOffset() {
        return spriteBuffer.position() * 4;
    }

    public void drawSprite30(Sprite sprite) {
        spriteBuffer.put(sprite.positionX);
        spriteBuffer.put(sprite.positionY);
        spriteBuffer.put(sprite.width);
        spriteBuffer.put(sprite.height);
        spriteBuffer.put(sprite.texU);
        spriteBuffer.put(sprite.texV);
        spriteBuffer.put(sprite.texWidth);
        spriteBuffer.put(sprite.texHeight);
    }

    @TargetApi(18)
    public void finishGeneratingMeshes30() {
        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);
    }

    public void prepareToRenderMeshes(int textureHandle) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
    }

    @TargetApi(18)
    public void prepareToRenderOpaqueSpritesUsingDepthTexture30(Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(opaqueSpriteProgram);
        GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                -camera.positionX, -camera.positionY);
        GLES20.glEnableVertexAttribArray(opaqueSpritePositionHandle);
        GLES20.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
        GLES30.glVertexAttribDivisor(opaqueSpritePositionHandle, 1);
        GLES30.glVertexAttribDivisor(opaqueSpriteTexCoordinatesHandle, 1);

        GLES20.glDepthMask(true);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDepthFunc(GLES20.GL_GEQUAL);
    }

    public void finishRenderingOpaqueSpritesUsingDepthTexture30() {
        GLES30.glDisableVertexAttribArray(opaqueSpritePositionHandle);
        GLES30.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
        GLES30.glDepthMask(false);
    }

    @TargetApi(18)
    public void prepareToRenderOpaqueSpritesDepthOnly30(Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, depthPassFrameBufferHandle);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(spriteDepthProgram);
        GLES20.glUniform4f(spriteDepthCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                -camera.positionX, -camera.positionY);
        GLES20.glEnableVertexAttribArray(spriteDepthPositionHandle);
        GLES20.glEnableVertexAttribArray(spriteDepthTexCoordinatesHandle);
        GLES30.glVertexAttribDivisor(spriteDepthPositionHandle, 1);
        GLES30.glVertexAttribDivisor(spriteDepthTexCoordinatesHandle, 1);

        GLES20.glDepthMask(true);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDepthFunc(GLES20.GL_GEQUAL);
    }

    public void finishRenderingOpaqueSpritesDepthOnly30() {
        GLES30.glDisableVertexAttribArray(spriteDepthPositionHandle);
        GLES30.glDisableVertexAttribArray(spriteDepthTexCoordinatesHandle);
    }

    @TargetApi(18)
    public void prepareToRenderOpaqueSpritesColorOnly30(Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDepthMask(false);
        GLES20.glUseProgram(opaqueSpriteProgram);
        GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                -camera.positionX, -camera.positionY);
        GLES20.glEnableVertexAttribArray(opaqueSpritePositionHandle);
        GLES20.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
        GLES30.glVertexAttribDivisor(opaqueSpritePositionHandle, 1);
        GLES30.glVertexAttribDivisor(opaqueSpriteTexCoordinatesHandle, 1);
    }

    public void finishRenderingOpaqueSpritesColorOnly30() {
        GLES30.glDisableVertexAttribArray(opaqueSpritePositionHandle);
        GLES30.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
    }

    @TargetApi(18)
    public void renderOpaqueSpritesMesh30(int offsetInBytes, int spriteCount) {
        renderSprites30(offsetInBytes, spriteCount, opaqueSpritePositionHandle, opaqueSpriteTexCoordinatesHandle);
    }

    @TargetApi(18)
    public void prepareToRenderTransparentSprites30(Camera camera) {
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
    }

    public void renderTransparentSpritesMesh30(int offsetInBytes, int spriteCount) {
        renderSprites30(offsetInBytes, spriteCount, transparentSpritePositionHandle, transparentSpriteTexCoordinatesHandle);
    }

    public void finishRenderingTransparentSprites30() {
        GLES30.glDisableVertexAttribArray(transparentSpritePositionHandle);
        GLES30.glDisableVertexAttribArray(transparentSpriteTexCoordinatesHandle);
    }

    @TargetApi(18)
    private static void renderSprites30(int offsetInBytes, int spriteCount,
                                        int spritePositionHandle, int spriteTexCoordinatesHandle) {

        GLES30.glFlushMappedBufferRange(GLES30.GL_ARRAY_BUFFER, offsetInBytes,
                spriteCount * SPRITE_SIZE_IN_BYTES30);
        GLES30.glVertexAttribPointer(spritePositionHandle, 4,
                GLES30.GL_FLOAT, false, 8, offsetInBytes);

        GLES30.glVertexAttribPointer(spriteTexCoordinatesHandle, 4,
                GLES30.GL_FLOAT, false, 8, offsetInBytes + 4);


        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0, 4, spriteCount);
    }

    public int getColorTextureHandle() {
        return colorTextureHandle;
    }


    public void drawSprite20(Sprite sprite) {
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
    }

    public void prepareToRenderOpaqueSpritesUsingDepthTexture20(Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
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
    }

    public void renderOpaqueSpritesMesh20(int offsetInBytes, int spriteCount) {
        renderSprites20(offsetInBytes, spriteCount, opaqueSpritePositionHandle, opaqueSpriteTexCoordinatesHandle);
    }

    public void finishRenderingOpaqueSpritesUsingDepthTexture20() {
        GLES20.glDisableVertexAttribArray(opaqueSpritePositionHandle);
        GLES20.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
        GLES20.glDepthMask(false);
    }

    public void prepareToRenderOpaqueSpritesDepthOnly20(Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, depthPassFrameBufferHandle);
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
    }

    public void finishRenderingOpaqueSpritesDepthOnly20() {
        GLES20.glDisableVertexAttribArray(spriteDepthPositionHandle);
        GLES20.glDisableVertexAttribArray(spriteDepthTexCoordinatesHandle);
    }

    public void prepareToRenderOpaqueSpritesColorOnly20(Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDepthMask(false);
        GLES20.glUseProgram(opaqueSpriteProgram);
        GLES20.glUniform4f(opaqueSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                -camera.positionX, -camera.positionY);
        GLES20.glEnableVertexAttribArray(opaqueSpritePositionHandle);
        GLES20.glEnableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
    }

    public void finishRenderingOpaqueSpritesColorOnly20() {
        GLES20.glDisableVertexAttribArray(opaqueSpritePositionHandle);
        GLES20.glDisableVertexAttribArray(opaqueSpriteTexCoordinatesHandle);
    }

    public void prepareToRenderTransparentSprites20(Camera camera) {
        GLES30.glUseProgram(transparentSpriteProgram);
        //set camera constants on gpu
        GLES20.glUniform4f(transparentSpriteCameraScaleAndOffsetHandle, camera.scaleX, camera.scaleY,
                -camera.positionX, -camera.positionY);
        GLES20.glEnableVertexAttribArray(transparentSpritePositionHandle);
        GLES20.glEnableVertexAttribArray(transparentSpriteTexCoordinatesHandle);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
    }

    public void renderTransparentSpritesMesh20(int offsetInBytes, int spriteCount) {
        renderSprites20(offsetInBytes, spriteCount, transparentSpritePositionHandle, transparentSpriteTexCoordinatesHandle);
    }

    public void finishRenderingTransparentSprites20() {
        GLES20.glDisableVertexAttribArray(transparentSpritePositionHandle);
        GLES20.glDisableVertexAttribArray(transparentSpriteTexCoordinatesHandle);
    }

    private void renderSprites20(int offsetInBytes, int spriteCount, int spritePositionHandle,
                                        int spriteTexCoordinatesHandle) {
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

    public void resize(int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, colorTextureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB5_A1, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
    }

    @Override
    public void close() {
        int[] temp = new int[1];
        temp[0] = colorTextureHandle;
        GLES20.glDeleteTextures(1, temp, 0);
        temp[0] = frameBufferHandle;
        GLES20.glDeleteFramebuffers(1, temp, 0);
        if(depthPassFrameBufferHandle != -2) {
            temp[0] = depthPassFrameBufferHandle;
            GLES20.glDeleteFramebuffers(1, temp, 0);
        }
        //TODO close the shaders
    }
}
