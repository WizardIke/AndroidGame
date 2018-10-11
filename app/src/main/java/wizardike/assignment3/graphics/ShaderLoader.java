package wizardike.assignment3.graphics;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ShaderLoader {
    public static String loadStringFromRawResource(Resources resources, int resId) {
        InputStream rawResource = resources.openRawResource(resId);
        String content = streamToString(rawResource);
        try {rawResource.close();} catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        //debug checks
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("ShaderLoader", GLES20.glGetShaderInfoLog(shader));
        }

        return shader;
    }

    public static void linkProgram(int program) {
        GLES20.glLinkProgram(program);
        int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == GLES20.GL_FALSE) {
            Log.e("ShaderLoader", GLES20.glGetProgramInfoLog(program));
        }
    }

    public static int loadShaderFromResource(Resources resources, int resId, int shaderType) {
        String shaderSource = loadStringFromRawResource(resources, resId);
        return loadShader(shaderType, shaderSource);
    }

    private static String streamToString(InputStream in) {
        String l;
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder s = new StringBuilder();
        try {
            while ((l = r.readLine()) != null) {
                s.append(l);
                s.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }
}
