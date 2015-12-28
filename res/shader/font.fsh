#version 430

uniform sampler2D t_sampler;

uniform float width = 0.5;
uniform float edge = 0.02;
uniform vec4 fontColor = vec4(1, 1, 1, 1);

in vec2 pass_texCoord;

layout(location = 0) out vec4 out_color;
void main() {
	out_color = vec4(fontColor.xyz, smoothstep(1.0 - (width + edge), 1.0 - width, texture(t_sampler, pass_texCoord).a) * fontColor.w);
}