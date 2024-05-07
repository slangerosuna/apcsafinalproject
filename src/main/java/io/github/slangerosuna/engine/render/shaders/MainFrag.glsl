#version 450 core

in vec3 passNormal;
in vec2 passUV;

out vec4 color;

uniform sampler2D tex;
uniform vec3 lightDir;
uniform vec3 dirLightColor;
uniform vec3 ambient;

void main() {
    vec3 normalizedLightDir = normalize(lightDir);
    vec3 normalizedNormal = normalize(passNormal);

    // Ambient component
    vec3 ambientLight = ambient;

    // Diffuse component
    float diff = max(dot(normalizedNormal, normalizedLightDir), 0.0);
    vec3 diffuseLight = diff * dirLightColor;

    /*// Specular component (you can adjust the shininess to your liking)
    float shininess = 32.0;
    vec3 viewDir = normalize(-passNormal);
    vec3 reflectDir = reflect(-normalizedLightDir, normalizedNormal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specularLight = spec * dirLightColor * 0.1f;*/

    vec4 texColor = texture(tex, passUV);
    color = vec4(ambientLight + diffuseLight, 1.0) * texColor;// + vec4(specularLight, 1.0);
}