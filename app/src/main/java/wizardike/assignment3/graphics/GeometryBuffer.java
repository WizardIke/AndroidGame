package wizardike.assignment3.graphics;

import android.opengl.GLES20;

import java.io.Closeable;

/**
 * Format: rgb hold base color and a is 1 for ground and 0 for everything else.
 */
public class GeometryBuffer implements Closeable {
    private int depthPassFrameBufferHandle;
    private int frameBufferHandle;
    private int colorTextureHandle;

    GeometryBuffer(int width, int height, int depthTextureHandle, int depthRenderBufferHandle) {
        int[] temp = new int[1];
        GLES20.glGenFramebuffers(1, temp, 0);
        frameBufferHandle = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        colorTextureHandle = temp[0];

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, colorTextureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB5_A1, width, height, 0,
                GLES20.GL_RGB5_A1, GLES20.GL_UNSIGNED_BYTE, null);
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
    }

    /**
     * Only necessary if depth textures aren't supported
     */
    public void bindDepthPass() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, depthPassFrameBufferHandle);
    }

    public void bindColorPass() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
    }

    public int getColorTextureHandle() {
        return colorTextureHandle;
    }

    public void resize(int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, colorTextureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB5_A1, width, height, 0,
                GLES20.GL_RGB5_A1, GLES20.GL_UNSIGNED_BYTE, null);
    }

    @Override
    public void close() {
        int[] temp = new int[1];
        temp[0] = colorTextureHandle;
        GLES20.glDeleteTextures(1, temp, 0);
        temp[0] = frameBufferHandle;
        GLES20.glDeleteFramebuffers(1, temp, 0);
    }
}
