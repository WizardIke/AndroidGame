#version 300 es

uniform sampler2D baseTexture;
in vec2 out_texCoords;
out vec4 fragmentColor;

 void main() {
    vec4 color = texture(baseTexture, out_texCoords);
    if(color.a < 0.5f) discard;
    fragmentColor = vec4(color.rgb, 0.0f);
 }