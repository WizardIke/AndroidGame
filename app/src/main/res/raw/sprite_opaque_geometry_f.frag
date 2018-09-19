#version 100

uniform sampler2D baseTexture;
noperspective varying vec2 out_texCoords;

 void main() {
    const vec4 color = texture2D(baseTexture, out_texCoords);
    if(color.a < 0.5f) discard;
    gl_FragColor = vec4(color.rgb, 0.0f);
 }