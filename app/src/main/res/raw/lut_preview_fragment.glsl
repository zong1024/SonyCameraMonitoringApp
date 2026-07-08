precision mediump float;
uniform sampler2D uTextureSampler;
uniform vec3 uLutLift;
uniform float uIntensity;
varying vec2 vTextureCoord;
void main()
{
    vec4 src = texture2D(uTextureSampler, vTextureCoord);
    vec3 corrected = clamp(src.rgb + uLutLift, 0.0, 1.0);
    gl_FragColor = vec4(mix(src.rgb, corrected, uIntensity), src.a);
}

