#version 450 core

in vec3 position;
in vec3 normal;
in vec2 UV;

out vec3 passNormal;
out vec2 passUV;

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;

void main() {
	gl_Position = (vec4(position, 1.0) * model * view * projection);
	mat3 normalMatrix = transpose(inverse(mat3(model)));
	passNormal = normalize(normalMatrix * normal);
	passUV = UV;
}