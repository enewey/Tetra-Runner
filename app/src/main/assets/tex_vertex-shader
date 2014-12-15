attribute vec4 position;
attribute vec2 texture;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

varying vec2 fragTexture;

void main()
{
    gl_Position = projectionMatrix * modelViewMatrix * position;
    fragTexture = texture;
}