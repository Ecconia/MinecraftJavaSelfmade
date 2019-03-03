#version 330 core

layout (location = 0) in vec4 vPosition;
layout (location = 1) in vec3 vColor;
layout (location = 2) in vec3 nVector; //Unused
layout (location = 3) in vec2 tVector;

out vec3 fColor;
out vec2 fCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(vPosition.xyz, 1.0);

	fColor = vColor;
	fCoord = vec2(tVector.x, tVector.y);
}
