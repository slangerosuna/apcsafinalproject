#version 450 core

in vec3 position;
in vec3 normal; //can be ignored
in vec2 UV;

out vec2 passUV;

uniform vec3 scale;
uniform vec3 pos;

void main() {
    gl_Position = vec4((position * scale).xy + pos.xy, 0.0, 0.0);
    passUV = UV;
}