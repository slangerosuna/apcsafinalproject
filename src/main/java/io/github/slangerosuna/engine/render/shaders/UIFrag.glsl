#version 450 core

in vec2 passUV;

out vec4 color;

uniform sampler2D tex;

void main() {
    color = texture(tex, vec2(-passUV.x, passUV.y));
}