#version 330 core
out vec4 oColor;

in vec3 fColor;
in vec2 fCoord;

uniform sampler2D tex;

void main()
{
    oColor = texture(tex, fCoord);
}
