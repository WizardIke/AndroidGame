#version 100

uniform vec4 scaleAndOffset;
attribute vec4 positionAndWidthAndHeight;
attribute vec4 texCoordinates;
noperspective varying vec2 out_texCoords;

void main() {
    const vec2 worldPosition = (positionAndWidthAndHeight.xy + scaleAndOffset.zw) * scaleAndOffset.xy;
    const vec2 widthAndHeight = positionAndWidthAndHeight.zw * scaleAndOffset.xy;
    // vertexID 0 = 0000, uv = (0, 0)
    // vertexID 1 = 0001, uv = (1, 0)
    // vertexID 2 = 0010, uv = (0, 1)
    // vertexID 3 = 0011, uv = (1, 1)
    const vec2 uv = vec2(gl_VertexID & 1, (gl_VertexID >> 1) & 1);
    const float z = positionAndWidthAndHeight.w * 0.25f * uv.y;
    gl_Position = vec4(worldPosition.x + (widthAndHeight.x * uv.x),
        worldPosition.y - (widthAndHeight.y * uv.y), z, 1.0f);
    out_texCoords = vec2(texCoordinates.x + (texCoordinates.z * uv.x), texCoordinates.y + (texCoordinates.w * uv.y));
}