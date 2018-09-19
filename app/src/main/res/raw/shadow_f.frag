#version 100

uniform sampler2D depthTexture;
varying vec3 fcolor;

void main() {
    const float depth = texture2D(baseTexture, out_texCoords).r;
    if(fcolor.r <= depth && fcolor.g >= depth) {
        gl_FragColor = vec4(0.0f, 0.0f, 0.0f, 1.0f);
    } else {
        gl_FragColor = vec4(0.0f, 1.0f, 0.0f, fcolor.b);
    }
}