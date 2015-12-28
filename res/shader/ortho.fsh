#version 430

uniform mat4 m_transform;
uniform mat4 m_view;

uniform sampler2D t_sampler;
in vec2 pass_texCoord;

layout(location = 0) out vec4 out_color;
void main() {
	out_color = texture2D(t_sampler, pass_texCoord);
}