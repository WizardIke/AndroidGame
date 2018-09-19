#version 100

uniform vec4 scaleAndOffset;
attribute vec3 position;
attribute vec4 color;
varying vec4 fcolor;

void main() {
    gl_Position = vec4((position.xy + scaleAndOffset.zw) * scaleAndOffset.xy, position.z * 0.25f, 1.0f);
    fcolor = color;
}