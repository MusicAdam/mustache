#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main()
{
    vec2 stepSize = vec2(0.125, 0.071);
    float alpha = 4.0 * texture2D(u_texture, v_texCoords).a;
    alpha -= texture2D( u_texture, v_texCoords + vec2(stepSize.x, 0) ).a;
    alpha -= texture2D( u_texture, v_texCoords + vec2(-stepSize.x, 0) ).a;
    alpha -= texture2D( u_texture, v_texCoords + vec2(0, stepSize.y) ).a;
    alpha -= texture2D( u_texture, v_texCoords + vec2(0, -stepSize.y) ).a;
    gl_FragColor = v_color * vec4(1.0, 1.0, 1.0, alpha);
}
