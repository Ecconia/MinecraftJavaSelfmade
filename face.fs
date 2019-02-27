#version 330 core
out vec4 oColor;

in vec3 fColor;

void main()
{
    oColor = vec4(fColor.xyz, 1.0f);
}