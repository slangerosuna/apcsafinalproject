#version 450 core

in vec3 position;
in vec4 color;
in vec2 UV;

out vec4 passColor;
out vec2 passUV;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main() {
	gl_Position = projection * view * model * vec4(position, 1.0);
	passColor = color;
	passUV = UV;
}