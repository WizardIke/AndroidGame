#version 100

uniform sampler2D depthTexture;
uniform vec2 oneOverScreenWidthAndHeight;
varying vec3 fcolor;

float unpackFloat(vec4 value) {
  vec4 bitSh = vec4(1.0/(128.0*128.0*128.0), 1.0/(128.0*128.0), 1.0/128.0, 1.0);
  return dot(value, bitSh);
}

void main() {
    vec2 texCoords = gl_FragCoord.xy * oneOverScreenWidthAndHeight;
    float depth = unpackFloat(texture2D(depthTexture, texCoords));
    if(fcolor.r <= depth && fcolor.g >= depth) {
        gl_FragColor = vec4(0.0f, 0.0f, 0.0f, 1.0f);
    } else {
        gl_FragColor = vec4(0.0f, 1.0f, 0.0f, fcolor.b);
    }
}