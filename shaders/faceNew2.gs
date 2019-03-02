#version 330 core
layout (points) in;
layout (line_strip, max_vertices = 4) out;

in VS_OUT {
	vec3 normal;
	mat4 helper;
} gs_in[];

void main()
{
	//Helpers:
	vec3 n = gs_in[0].normal;
	mat4 h = gs_in[0].helper;

	//Get the direction of the vector
	float direction = n.x + n.y + n.z;
	//Make positive:
	vec3 nn = n * direction;
	
	//Shrink to desired offset.
	direction = direction * 0.006;

	//Create pattern:
	vec4 a = vec4(0.5, 0.5, -0.5, -0.5);
	vec4 b = vec4(direction, direction, direction, direction);
	vec4 c = vec4(0.5, -0.5, 0.5, -0.5);

	vec4 first = nn.x * b + nn.y * c + nn.z * a;
	vec4 second = nn.x * a + nn.y * b + nn.z * c;
	vec4 third = nn.x * c + nn.y * a + nn.z * b;

	vec4 corner1 = h * (gl_in[0].gl_Position + vec4(first.x, second.x, third.x, 0));
	vec4 corner2 = h * (gl_in[0].gl_Position + vec4(first.y, second.y, third.y, 0));
	vec4 corner3 = h * (gl_in[0].gl_Position + vec4(first.w, second.w, third.w, 0));
	vec4 corner4 = h * (gl_in[0].gl_Position + vec4(first.z, second.z, third.z, 0));

	gl_Position = corner1;
	EmitVertex();
	gl_Position = corner2;
	EmitVertex();
	EndPrimitive();

	gl_Position = corner2;
	EmitVertex();
	gl_Position = corner3;
	EmitVertex();
	EndPrimitive();

	gl_Position = corner3;
	EmitVertex();
	gl_Position = corner4;
	EmitVertex();
	EndPrimitive();

	gl_Position = corner4;
	EmitVertex();
	gl_Position = corner1;
	EmitVertex();
	EndPrimitive();
/*
	gl_Position = corner1;
	EmitVertex();
	gl_Position = corner3;
	EmitVertex();
	EndPrimitive();

	gl_Position = corner4;
	EmitVertex();
	gl_Position = corner2;
	EmitVertex();
	EndPrimitive();
	*/
}
