#version 100

precision mediump float;
uniform vec4 scaleAndOffset;
uniform vec2 oneOverScreenWidthAndHeight;
uniform sampler2D baseTexture;
uniform sampler2D lightAmountTexture;
uniform vec3 lightPosition;
uniform vec3 lightColor;

void main() {
    //alpha channel is approximately depth;
    vec2 texCoords = gl_FragCoord.xy * oneOverScreenWidthAndHeight;
    vec4 baseColor = texture2D(baseTexture, texCoords);
    vec4 lightAmountV = texture2D(lightAmountTexture, texCoords);
    float ambientLightAmount = lightAmountV.r;
    float directLightAmount = lightAmountV.g; //1 = fully in light, 0 = fully in shadow
    vec3 newLightPosition = vec3(lightPosition.xy + scaleAndOffset.zw, 4.0f - lightPosition.z); //position of light relative to camera
    vec3 toCamera = vec3((vec2(1.0f, 1.0f) - (texCoords * vec2(2.0f, 2.0f))) / scaleAndOffset.xy, -4.0f * baseColor.a); //vector from current pixel to camera
    vec3 toLight = newLightPosition + toCamera; //vector from current pixel to light
    float distanceSqrt = dot(toLight, toLight);
    vec3 normal = baseColor.a == 0.0f ? vec3(0.0f, -0.34f, -0.94f) : vec3(0.0f, 0.94f, 0.34f); //extract normal from alpha channel
    toLight /= sqrt(distanceSqrt);
    float light = max(dot(toLight, normal), 0.0f) * directLightAmount + ambientLightAmount;
    float falloff = 1.0f / (distanceSqrt + 0.05f); //Needs small constant to prevent division by zero
    vec3 finalColor = light * vec3(falloff, falloff, falloff) * baseColor.xyz * lightColor;
    gl_FragColor = vec4(finalColor, 1.0f);
}