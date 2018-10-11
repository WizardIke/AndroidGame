#version 100

precision mediump float;
uniform vec4 scaleAndOffset;
attribute vec2 position;

void main() {
    gl_Position = vec4((position + scaleAndOffset.zw) * scaleAndOffset.xy, 0.0f, 1.0f);
}