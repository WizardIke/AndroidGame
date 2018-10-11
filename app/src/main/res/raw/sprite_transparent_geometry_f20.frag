#version 100

uniform sampler2D baseTexture;
varying vec2 out_texCoords;

 void main() {
    gl_FragColor = texture2D(baseTexture, out_texCoords);
 }