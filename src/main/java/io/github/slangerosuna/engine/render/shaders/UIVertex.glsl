#version 450 core

in vec3 vertPos;
in vec3 normal; //can be ignored
in vec2 uv;

out passUV;

uniform vec3 scale;
uniform vec3 pos;

void main() {
    gl_Position = vec4((vertPos * scale) + pos,0.0, 0.0);
    passUV = uv;
}