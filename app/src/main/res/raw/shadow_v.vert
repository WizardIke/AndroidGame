#version 100

uniform vec4 scaleAndOffset;
attribute vec2 position;
attribute vec3 heightAndColor;
varying vec3 fcolor;

void main() {
    gl_Position = vec4((position.xy + scaleAndOffset.zw) * scaleAndOffset.xy, 0.0f, 1.0f);
    fcolor = heightAndColor;
}