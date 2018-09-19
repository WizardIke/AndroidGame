#version 100

uniform sampler2D baseTexture;
noperspective varying vec2 out_texCoords;

vec4 packFloat(const float value) {
  const vec4 bitSh = vec4(128.0*128.0*128.0, 128.0*128.0, 128.0, 1.0);
  const vec4 bitMsk = vec4(0.0, 1.0/128.0, 1.0/128.0, 1.0/128.0);
  vec4 res = fract(value * bitSh);
  res -= res.xxyz * bitMsk;
  return res;
}

 void main() {
    const vec4 color = texture2D(baseTexture, out_texCoords);
    if(color.a < 0.5f) discard;
    gl_FragColor = packFloat(gl_FragCoord.z);
 }