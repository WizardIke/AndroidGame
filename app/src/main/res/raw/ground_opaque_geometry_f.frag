#version 100

uniform sampler2D baseTexture;
varying vec2 texCoords;

void main() {
    vec4 color = texture2D(baseTexture, texCoords);
    gl_FragColor = vec4(color.rgb, 1.0f);
}