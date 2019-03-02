#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 6) out;

in VS_OUT {
	vec3 normal;
	vec3 color;
	mat4 helper;
} gs_in[];

out vec3 fColor;  

void main()
{
	fColor = gs_in[0].color;

	//Helpers:
	vec3 n = gs_in[0].normal;
	mat4 h = gs_in[0].helper;

	//Get the direction of the vector
	float direction = n.x + n.y + n.z;

	//Create pattern:
	vec4 a = vec4(0.5, 0.5, -0.5, -0.5);
	vec4 b = vec4(0, 0, 0, 0);
	vec4 c = vec4(0.5, -0.5, 0.5, -0.5);

	//Make positive:
	vec3 nn = n * direction;

	vec4 first = nn.x * b + nn.y * c + nn.z * a;
	vec4 second = nn.x * a + nn.y * b + nn.z * c;
	vec4 third = nn.x * c + nn.y * a + nn.z * b;

	gl_Position = h * (gl_in[0].gl_Position + vec4(first.x, second.x, third.x, 0));
	EmitVertex();
	gl_Position = h * (gl_in[0].gl_Position + vec4(first.y, second.y, third.y, 0));
	EmitVertex();
	gl_Position = h * (gl_in[0].gl_Position + vec4(first.w, second.w, third.w, 0));
	EmitVertex();
	EndPrimitive();

	gl_Position = h * (gl_in[0].gl_Position + vec4(first.x, second.x, third.x, 0));
	EmitVertex();
	gl_Position = h * (gl_in[0].gl_Position + vec4(first.z, second.z, third.z, 0));
	EmitVertex();
	gl_Position = h * (gl_in[0].gl_Position + vec4(first.w, second.w, third.w, 0));
	EmitVertex();
	EndPrimitive();
}
