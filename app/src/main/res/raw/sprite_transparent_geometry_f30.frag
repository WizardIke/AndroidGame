#version 300 es

uniform sampler2D baseTexture;
in vec2 out_texCoords;
out vec4 fragmentColor;

 void main() {
    fragmentColor = texture(baseTexture, out_texCoords);
 }