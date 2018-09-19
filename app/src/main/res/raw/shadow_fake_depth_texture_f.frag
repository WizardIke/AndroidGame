#version 100

uniform sampler2D depthTexture;
varying vec3 fcolor;

float unpackFloat(const vec4 value) {
  const vec4 bitSh = vec4(1.0/(128.0*128.0*128.0), 1.0/(128.0*128.0), 1.0/128.0, 1.0);
  return dot(value, bitSh);
}

void main() {
    const float depth = unpackFloat(texture2D(baseTexture, out_texCoords));
    if(fcolor.r <= depth && fcolor.g >= depth) {
        gl_FragColor = vec4(0.0f, 0.0f, 0.0f, 1.0f);
    } else {
        gl_FragColor = vec4(0.0f, 1.0f, 0.0f, fcolor.b);
    }
}