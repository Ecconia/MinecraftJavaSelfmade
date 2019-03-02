#version 330 core
layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vColor;
layout (location = 2) in vec3 nVector;

out VS_OUT {
	vec3 normal;
	mat4 helper;
} vs_out;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
	vs_out.helper = projection * view;
	gl_Position = model * vec4(vPosition.xyz, 1.0);
	vs_out.normal = nVector;
}