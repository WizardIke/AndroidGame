package wizardike.assignment3.graphics;

import android.opengl.GLES20;

public class TextureManager {
    private int textureHandle;

    TextureManager(int textureSize) {
        int[] temp = new int[1];
        GLES20.glGenTextures(1, temp, 0);
        textureHandle = temp[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB565, textureSize, textureSize, 0,
                GLES20.GL_RGB565, GLES20.GL_UNSIGNED_BYTE, null);
        //should probably be based on user settings
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public int getTextureHandle() {
        return textureHandle;
    }
}
