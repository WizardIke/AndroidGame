package wizardike.assignment3.graphics;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import wizardike.assignment3.R;

public class LightBuffer {
    private static final int BYTES_PER_SHADOW = 36 * 4;
    private static final int BYTES_PER_LIGHT = 21 * 4;
    private static final int MAX_SHADOWS = 128;
    private static final float AMBIENT_LIGHT_STRENGTH = 0.1f;

    private int lightTextureHandle;
    private int lightFrameBufferHandle;

    private int depthTextureHandle;

    private final int color3DProgram;
    private final int color3DColorLocation;
    private final int color3DCameraUniformLocation;
    private final int color3DPositionLocation;

    private final int applyLightProgram;
    private final int cameraUniformLocation;
    private final int lightPositionUniformLocation;
    private final int lightColorUniformLocation;
    private final int oneOverWidthAndHeightUniformLocation;

    private final int shadowProgram;
    private final int shadowScaleAndOffsetUniformLocation;
    private final int shadowPositionLocation;
    private final int shadowHeightAndColorLocation;

    private FloatBuffer shadowMeshes;

    private boolean isFirstLight;

    LightBuffer(int width, int height, Resources resources, int depthTextureHandle,
                boolean supportsDepthTexture) {
        this.depthTextureHandle = depthTextureHandle;
        int[] temp = new int[1];
        //make the light texture
        GLES20.glGenTextures(1, temp, 0);
        lightTextureHandle = temp[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lightTextureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB565, width, height, 0,
                GLES20.GL_RGB565, GLES20.GL_UNSIGNED_BYTE, null);
        //the buffer size is the same as the screen size so GL_NEAREST sampling is good.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //Non-power-of-two textures might only support GL_CLAMP_TO_EDGE.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //make the frame buffer
        GLES20.glGenFramebuffers(1, temp, 0);
        lightFrameBufferHandle = temp[0];
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, lightTextureHandle, 0);

        //unbind resources
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        int lighting_vertex_shader = ShaderLoader.loadShaderFromResource(resources,
                R.raw.lighting_apply_v, GLES20.GL_VERTEX_SHADER);
        int lighting_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                R.raw.lighting_apply_f, GLES20.GL_FRAGMENT_SHADER);

        applyLightProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(applyLightProgram, lighting_vertex_shader);
        GLES20.glAttachShader(applyLightProgram, lighting_frag_shader);
        GLES20.glLinkProgram(applyLightProgram);

        cameraUniformLocation = GLES20.glGetUniformLocation(applyLightProgram, "scaleAndOffset");
        oneOverWidthAndHeightUniformLocation = GLES20.glGetUniformLocation(applyLightProgram,
                "oneOverScreenWidthAndHeight");
        GLES20.glUniform2f(oneOverWidthAndHeightUniformLocation, 1.0f / width, 1.0f / height);
        int baseTextureUniformLocation = GLES20.glGetUniformLocation(applyLightProgram, "baseTexture");
        int lightTextureUniformLocation = GLES20.glGetUniformLocation(applyLightProgram, "lightAmountTexture");
        GLES20.glUniform1i(baseTextureUniformLocation, 0);
        GLES20.glUniform1i(lightTextureUniformLocation, 1);
        lightPositionUniformLocation = GLES20.glGetUniformLocation(applyLightProgram, "lightPosition");
        lightColorUniformLocation = GLES20.glGetUniformLocation(applyLightProgram, "lightColor");

        //load color3DProgram
        int color3d_vertex_shader = ShaderLoader.loadShaderFromResource(resources,
                R.raw.color_3d_v, GLES20.GL_VERTEX_SHADER);
        int color3d_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                R.raw.color_3d_f, GLES20.GL_FRAGMENT_SHADER);

        color3DProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(color3DProgram, color3d_vertex_shader);
        GLES20.glAttachShader(color3DProgram, color3d_frag_shader);
        GLES20.glLinkProgram(color3DProgram);

        color3DColorLocation = GLES20.glGetAttribLocation(color3DProgram, "color");
        color3DCameraUniformLocation = GLES20.glGetUniformLocation(color3DProgram, "scaleAndOffset");
        color3DPositionLocation = GLES20.glGetAttribLocation(color3DProgram, "position");

        //load shadowProgram
        int shadow_vertex_shader = ShaderLoader.loadShaderFromResource(resources,
                R.raw.shadow_v, GLES20.GL_VERTEX_SHADER);
        int shadow_frag_shader;
        if(supportsDepthTexture) {
            shadow_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.shadow_f, GLES20.GL_FRAGMENT_SHADER);
        } else {
            shadow_frag_shader = ShaderLoader.loadShaderFromResource(resources,
                    R.raw.shadow_fake_depth_texture_f, GLES20.GL_FRAGMENT_SHADER);
        }
        shadowProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shadowProgram, shadow_vertex_shader);
        GLES20.glAttachShader(shadowProgram, shadow_frag_shader);
        GLES20.glLinkProgram(shadowProgram);

        shadowScaleAndOffsetUniformLocation = GLES20.glGetUniformLocation(shadowProgram, "scaleAndOffset");
        shadowPositionLocation = GLES20.glGetAttribLocation(shadowProgram, "position");
        shadowHeightAndColorLocation = GLES20.glGetAttribLocation(shadowProgram, "heightAndColor");


        shadowMeshes = ByteBuffer.allocateDirect(MAX_SHADOWS * BYTES_PER_SHADOW + BYTES_PER_LIGHT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    public void prepareToRenderLights() {
        GLES20.glDepthMask(false);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        isFirstLight = true;
    }

    public void renderLight(PointLight light, Camera camera) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, lightFrameBufferHandle);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(color3DProgram);
        GLES20.glUniform4f(color3DCameraUniformLocation, camera.scaleX * light.radius,
                camera.scaleY * light.radius, -camera.positionX, -camera.positionY);

        //draw light circle
        addLightGeometry(light.positionX, light.positionY, light.radius);
        shadowMeshes.position(0);
        GLES20.glVertexAttribPointer(color3DPositionLocation, 3,
                GLES20.GL_FLOAT, false, 7, shadowMeshes);
        shadowMeshes.position(3);
        GLES20.glVertexAttribPointer(color3DColorLocation, 4,
                GLES20.GL_FLOAT, false, 7, shadowMeshes);
        shadowMeshes.position(0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        //prepare to render shadows
        GLES20.glUseProgram(shadowProgram);
        //by outputting (0.0, 1.0, 0.0, ambientLightMultiplier) we can set direct light to zero
        //while multiplying ambient light by ambientLightMultiplier
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendEquation(GLES20.GL_FUNC_REVERSE_SUBTRACT);
        GLES20.glBlendFunc(GLES20.GL_CONSTANT_ALPHA, GLES20.GL_SRC_ALPHA);
        GLES20.glBlendColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glUniform4f(shadowScaleAndOffsetUniformLocation, camera.scaleX, camera.scaleY,
                -camera.positionX, -camera.positionY);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureHandle);
    }

    /**
     * Adds triangle to shadowMeshes that fits around the circle given.
     */
    private void addLightGeometry(float x, float y, float radius) {
        final float root3 = (float)Math.sqrt(3.0f);
        //position
        shadowMeshes.put(x - root3 * radius);
        shadowMeshes.put(y + radius);
        shadowMeshes.put(0.0f);
        //color
        shadowMeshes.put(AMBIENT_LIGHT_STRENGTH);
        shadowMeshes.put(1.0f);
        shadowMeshes.put(0.0f);
        shadowMeshes.put(1.0f);
        //position
        shadowMeshes.put(x + root3 * radius);
        shadowMeshes.put(y + radius);
        shadowMeshes.put(0.0f);
        //color
        shadowMeshes.put(AMBIENT_LIGHT_STRENGTH);
        shadowMeshes.put(1.0f);
        shadowMeshes.put(0.0f);
        shadowMeshes.put(1.0f);
        //position
        shadowMeshes.put(x);
        shadowMeshes.put(y - 2.0f * radius);
        shadowMeshes.put(0.0f);
        //color
        shadowMeshes.put(AMBIENT_LIGHT_STRENGTH);
        shadowMeshes.put(1.0f);
        shadowMeshes.put(0.0f);
        shadowMeshes.put(1.0f);
    }

    public void renderCircleShadow(CircleShadowCaster shadowCaster, PointLight light) {
        //find the points p and q to cast the shadow from
        final float ux = shadowCaster.positionX - light.positionX;
        final float uy = shadowCaster.positionY - light.positionY;
        final float ul2 = ux * ux + uy * uy;
        final float r2 = shadowCaster.radius * shadowCaster.radius;
        if(ul2 - r2 <= 0.0f) return; //we are in the shadow caster
        float vy1;
        float vy2;
        float vx1;
        float vx2;
        if(Math.abs(uy) >= Math.abs(ux)) {
            final float yOffset = shadowCaster.radius * ux * (float)Math.sqrt(ul2 - r2);
            final float yConstant = r2 * uy;
            vy1 = (yConstant + yOffset) / ul2;
            vy2 = (yConstant - yOffset) / ul2;
            vx1 = (r2 - vy1 * uy) / ux;
            vx2 = (r2 - vy2 * uy) / ux;
        } else {
            final float xOffset = shadowCaster.radius * uy * (float)Math.sqrt(ul2 - r2);
            final float xConstant = r2 * ux;
            vx1 = (xConstant + xOffset) / ul2;
            vx2 = (xConstant - xOffset) / ul2;
            vy1 = (r2 - vx1 * ux) / uy;
            vy2 = (r2 - vx2 * ux) / uy;
        }

        float px;
        float py;
        float qx;
        float qy;
        if(shadowCaster.positionY + vy1 > shadowCaster.positionY + vy2) {
            px = shadowCaster.positionX + vx2;
            py = shadowCaster.positionY + vy2;
            qx = shadowCaster.positionX + vx1;
            qy = shadowCaster.positionY + vy1;
        } else {
            px = shadowCaster.positionX + vx1;
            py = shadowCaster.positionY + vy1;
            qx = shadowCaster.positionX + vx2;
            qy = shadowCaster.positionY + vy2;
        }
        //find shadow volume start
        final float uLength = (float)Math.sqrt(ul2);
        final float lightToPX = px - light.positionX;
        final float lightToPY = py - light.positionY;
        final float lightToQX = qx - light.positionX;
        final float lightToQY = qy - light.positionY;
        final float lightToPLength = (float)Math.sqrt(lightToPX * lightToPX + lightToPY * lightToPY);
        final float cosA = lightToPX * ux + lightToPY * uy;
        final float toVolumeStartLength = (uLength + shadowCaster.radius) / cosA;
        final float toVolumeStartMultiplier = toVolumeStartLength / lightToPLength;
        final float volumeStart1X = lightToPX * toVolumeStartMultiplier + light.positionX;
        final float volumeStart1Y = lightToPY * toVolumeStartMultiplier + light.positionY;
        final float volumeStart2X = lightToQX * toVolumeStartMultiplier + light.positionX;
        final float volumeStart2Y = lightToQY * toVolumeStartMultiplier + light.positionY;

        //draw front of shadow on the ground to prevent the sprite from incorrectly shadowing itself
        float multiplier = (uLength - shadowCaster.radius) / uLength;
        float centerX = ux * multiplier;
        float centerY = uy * multiplier;
        float upperX = lightToPX + centerX;
        float upperY = lightToPY + centerY;
        final float upperLength = (float)Math.sqrt(upperX * upperX + upperY * upperY);
        multiplier = shadowCaster.radius / upperLength;
        upperX *= multiplier;
        upperY *= multiplier;
        float lowerX = (lightToQX + centerX) * multiplier; //multiplier should still be the same due to symmetry
        float lowerY = (lightToQY + centerY) * multiplier;
        centerX += light.positionX;
        centerY += light.positionY;
        upperX += light.positionX;
        upperY += light.positionY;
        lowerX += light.positionX;
        lowerY += light.positionY;
        final float ambientLightMultiplier = shadowCaster.ambientLightMultiplier;
        final float shadowX = shadowCaster.positionX;
        final float shadowY = shadowCaster.positionY;
        //position
        shadowMeshes.put(qx);
        shadowMeshes.put(qy);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(shadowX);
        shadowMeshes.put(shadowY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(lowerX);
        shadowMeshes.put(lowerY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);

        //position
        shadowMeshes.put(lowerX);
        shadowMeshes.put(lowerY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(shadowX);
        shadowMeshes.put(shadowY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(centerX);
        shadowMeshes.put(centerY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);

        //position
        shadowMeshes.put(centerX);
        shadowMeshes.put(centerY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(shadowX);
        shadowMeshes.put(shadowY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(upperX);
        shadowMeshes.put(upperY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);

        //position
        shadowMeshes.put(upperX);
        shadowMeshes.put(upperY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(shadowX);
        shadowMeshes.put(shadowY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(px);
        shadowMeshes.put(py);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);

        //position
        shadowMeshes.put(shadowX);
        shadowMeshes.put(shadowY);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(volumeStart1X);
        shadowMeshes.put(volumeStart1Y);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(px);
        shadowMeshes.put(py);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);

        //position
        shadowMeshes.put(volumeStart2X);
        shadowMeshes.put(volumeStart2Y);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(volumeStart1X);
        shadowMeshes.put(volumeStart1Y);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);
        //position
        shadowMeshes.put(qx);
        shadowMeshes.put(qy);
        shadowMeshes.put(0.0f); //lower height
        shadowMeshes.put(0.0f); //upper height
        //color
        shadowMeshes.put(ambientLightMultiplier);

        //draw shadow volume
        addShadowGeometry(volumeStart1X, volumeStart1Y, volumeStart2X, volumeStart2Y,
                shadowCaster.height, light.positionX, light.positionY,
                light.positionZ, light.radius, shadowCaster.ambientLightMultiplier);
    }

    public void renderLineShadow(LineShadowCaster shadowCaster, PointLight light) {
        //TODO back face culling
        addShadowGeometry(shadowCaster.startX, shadowCaster.startY, shadowCaster.endX,
                shadowCaster.endY, shadowCaster.height, light.positionX, light.positionY,
                light.positionZ, light.radius, shadowCaster.ambientLightMultiplier);
    }

    private void addShadowGeometry(
            final float startX, final float startY,
            final float endX, final float endY,
            final float height,
            final float lightX, final float lightY, final float lightZ,
            final float lightRadius,
            final float ambientLightMultiplier) {

        final float vector1X = startX - lightX;
        final float vector1Y = startY - lightY;
        final float vector1XYLength = (float)Math.sqrt(vector1X * vector1X + vector1Y * vector1Y);

        final float vector2X = endX - lightX;
        final float vector2Y = endY - lightY;
        final float vector2XYLength = (float)Math.sqrt(vector2X * vector2X + vector2Y * vector2Y);
        final float vectorZ = height - lightZ;

        final float edgeLength = lightRadius / Math.max(
                (vector1X * vector2X + vector1Y * vector2Y) / (vector1XYLength * vector2XYLength),
                0.05f
        );


        final float vector1Multiplier = Math.max(edgeLength / vector1XYLength, 1.0f);
        float edge1XL = vector1Multiplier * vector1X;
        float edge1YL = vector1Multiplier * vector1Y;
        float edge1ZL = vector1Multiplier * vectorZ;


        final float vector2Multiplier = Math.max(edgeLength / vector2XYLength, 1.0f);
        float edge2XL = vector2Multiplier * vector2X;
        float edge2YL = vector2Multiplier * vector2Y;
        float edge2ZL = vector2Multiplier * vectorZ;

        //Add shadow geometry
        if(startX == endX) {
            if(Math.abs(edge1XL) > Math.abs(edge2XL)) {
                final float multiplier = edge1XL / edge2XL;
                edge2XL *= multiplier;
                edge2YL *= multiplier;
                edge2ZL *= multiplier;
            } else {
                final float multiplier = edge2XL / edge1XL;
                edge1XL *= multiplier;
                edge1YL *= multiplier;
                edge1ZL *= multiplier;
            }

            final float edge1X = edge1XL + lightX;
            final float edge1Y = edge1YL + lightY;
            final float edge1Z = edge1ZL + lightZ;
            final float edge2X = edge2XL + lightX;
            final float edge2Y = edge2YL + lightY;
            final float edge2Z = edge2ZL + lightZ;

            //position
            shadowMeshes.put(endX);
            shadowMeshes.put(endY);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(0.0f); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(edge1X);
            shadowMeshes.put(edge1Y);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(0.0f); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(edge1X);
            shadowMeshes.put(edge1Y - edge1Z);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(edge1Z); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);

            //position
            shadowMeshes.put(endX);
            shadowMeshes.put(endY);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(0.0f); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(edge1X);
            shadowMeshes.put(edge1Y - edge1Z);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(edge1Z); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(endX);
            shadowMeshes.put(endY - height);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(height); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);

            if(endY - height > startX) {
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
            }

            //position
            shadowMeshes.put(startX);
            shadowMeshes.put(startY);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(height); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(edge2X);
            shadowMeshes.put(edge2Y);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(edge2Z); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(edge2X);
            shadowMeshes.put(edge2Y - edge2Z);
            shadowMeshes.put(edge2Z); //lower height
            shadowMeshes.put(edge2Z); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);

            //position
            shadowMeshes.put(startX);
            shadowMeshes.put(startY);
            shadowMeshes.put(0.0f); //lower height
            shadowMeshes.put(height); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(edge2X);
            shadowMeshes.put(edge2Y - edge2Z);
            shadowMeshes.put(edge2Z); //lower height
            shadowMeshes.put(edge2Z); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
            //position
            shadowMeshes.put(startX);
            shadowMeshes.put(startY - height);
            shadowMeshes.put(height); //lower height
            shadowMeshes.put(height); //upper height
            //color
            shadowMeshes.put(ambientLightMultiplier);
        } else if(startY == endY) {
            final float edge1X = edge1XL + lightX;
            final float edge1Y = edge1YL + lightY;
            final float edge1Z = edge1ZL + lightZ;
            final float edge2X = edge2XL + lightX;
            final float edge2Y = edge2YL + lightY;
            final float edge2Z = edge2ZL + lightZ;

            if(lightY <= startY) {
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endY);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(0.0f); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y - edge1Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY - height);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y - edge1Z);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
            } else {
                final float offset = vectorZ * height;

                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(height); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(height); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY - height);
                shadowMeshes.put(height); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(edge2Z); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y - edge2Z);
                shadowMeshes.put(edge2Z); //lower height
                shadowMeshes.put(edge2Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY - height);
                shadowMeshes.put(height); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(height); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(height); //lower height
                shadowMeshes.put(height); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y - edge1Z);
                shadowMeshes.put(edge1Z); //lower height
                shadowMeshes.put(edge1Z); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(edge2X);
                shadowMeshes.put(edge2Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge2Z + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);

                //position
                shadowMeshes.put(edge1X);
                shadowMeshes.put(edge1Y);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(edge1Z + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(endX);
                shadowMeshes.put(endY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
                //position
                shadowMeshes.put(startX);
                shadowMeshes.put(startY);
                shadowMeshes.put(0.0f); //lower height
                shadowMeshes.put(height + offset); //upper height
                //color
                shadowMeshes.put(ambientLightMultiplier);
            }
        } else {
            //TODO implement
        }
    }

    public void applyLighting(int geometryColorBufferHandle, PointLight light, Camera camera) {
        //submit shadows for rendering
        if(shadowMeshes.position() != 0) {
            int numberOfVertices = shadowMeshes.position() / 5;
            shadowMeshes.position(0);
            GLES20.glVertexAttribPointer(shadowPositionLocation, 2,
                    GLES20.GL_FLOAT, false, 5, shadowMeshes);
            shadowMeshes.position(2);
            GLES20.glVertexAttribPointer(shadowHeightAndColorLocation, 3,
                    GLES20.GL_FLOAT, false, 5, shadowMeshes);
            shadowMeshes.position(0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numberOfVertices);
        }

        GLES20.glUseProgram(applyLightProgram);
        GLES20.glUniform4f(cameraUniformLocation, camera.scaleX * light.radius,
                camera.scaleY * light.radius, -camera.positionX, -camera.positionY);
        GLES20.glUniform3f(lightPositionUniformLocation, light.positionX, light.positionX, light.positionZ);
        GLES20.glUniform3f(lightColorUniformLocation, light.colorR, light.colorG, light.colorB);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, geometryColorBufferHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lightTextureHandle);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        if(isFirstLight) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            isFirstLight = false;
        }

        //draw light circle
        final float x = light.positionX;
        final float y = light.positionY;
        final float radius = light.radius;
        final float root3 = (float)Math.sqrt(3.0f);
        shadowMeshes.put(x - root3 * radius);
        shadowMeshes.put(y + radius);
        shadowMeshes.put(x + root3 * radius);
        shadowMeshes.put(y + radius);
        shadowMeshes.put(x);
        shadowMeshes.put(y - 2.0f * radius);
        shadowMeshes.position(0);
        GLES20.glVertexAttribPointer(color3DPositionLocation, 3,
                GLES20.GL_FLOAT, false, 3, shadowMeshes);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public void resize(int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lightTextureHandle);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB565, width, height, 0,
                GLES20.GL_RGB565, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glUniform2f(oneOverWidthAndHeightUniformLocation, 1.0f / width, 1.0f / height);
    }
}
