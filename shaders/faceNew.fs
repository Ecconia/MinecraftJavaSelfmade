#version 330 core
out vec4 oColor;

in vec3 fColor;

void main()
{
    oColor = vec4(1 - fColor.x, 1 - fColor.y, 1 - fColor.z, 1.0f);
}