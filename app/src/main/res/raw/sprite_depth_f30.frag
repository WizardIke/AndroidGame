#version 300 es

uniform sampler2D baseTexture;
in vec2 out_texCoords;
out vec4 fragmentColor;

vec4 packFloat(float value) {
  vec4 bitSh = vec4(128.0*128.0*128.0, 128.0*128.0, 128.0, 1.0);
  vec4 bitMsk = vec4(0.0, 1.0/128.0, 1.0/128.0, 1.0/128.0);
  vec4 res = fract(value * bitSh);
  res -= res.xxyz * bitMsk;
  return res;
}

 void main() {
    vec4 color = texture(baseTexture, out_texCoords);
    if(color.a < 0.5f) discard;
    fragmentColor = packFloat(gl_FragCoord.z);
 }