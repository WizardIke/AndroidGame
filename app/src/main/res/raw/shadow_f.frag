#version 100

uniform sampler2D depthTexture;
uniform vec2 oneOverScreenWidthAndHeight;
varying vec3 fcolor;

void main() {
    vec2 texCoords = gl_FragCoord.xy * oneOverScreenWidthAndHeight;
    float depth = texture2D(depthTexture, texCoords).r;
    if(fcolor.r <= depth && fcolor.g >= depth) {
        gl_FragColor = vec4(0.0f, 0.0f, 0.0f, 1.0f);
    } else {
        gl_FragColor = vec4(0.0f, 1.0f, 0.0f, fcolor.b);
    }
}