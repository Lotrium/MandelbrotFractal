package com.example.mandelbrotfractal;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MandelbrotRenderer implements GLSurfaceView.Renderer {

    private float zoom = 1.0f;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private int width;
    private int height;

    private int program;
    private int positionHandle;
    private int resolutionHandle;
    private int zoomHandle;
    private int offsetHandle;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision highp float;" +
                    "uniform vec2 resolution;" +
                    "uniform float zoom;" +
                    "uniform vec2 offset;" +
                    "vec2 cmul(vec2 a, vec2 b) {" +
                    "    return vec2(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x);" +
                    "}" +
                    "vec3 iceColor(float t) {" +
                    "    vec3 color1 = vec3(0.0, 0.0, 0.0);" +
                    "    vec3 color2 = vec3(0.0, 0.0, 0.5);" +
                    "    vec3 color3 = vec3(0.0, 0.5, 1.0);" +
                    "    vec3 color4 = vec3(0.5, 1.0, 1.0);" +
                    "    if (t < 0.33) {" +
                    "        return mix(color1, color2, t * 3.0);" +
                    "    } else if (t < 0.66) {" +
                    "        return mix(color2, color3, (t - 0.33) * 3.0);" +
                    "    } else {" +
                    "        return mix(color3, color4, (t - 0.66) * 3.0);" +
                    "    }" +
                    "}" +
                    "void main() {" +
                    "    vec2 z, c;" +
                    "    c.x = (gl_FragCoord.x - resolution.x/2.0) / (0.5 * zoom * resolution.y) + offset.x;" +
                    "    c.y = (gl_FragCoord.y - resolution.y/2.0) / (0.5 * zoom * resolution.y) + offset.y;" +
                    "    z = c;" +
                    "    float iter = 0.0;" +
                    "    const float max_iter = 300.0;" +
                    "    for(iter = 0.0; iter < max_iter; iter++) {" +
                    "        if(length(z) > 2.0) break;" +
                    "        z = cmul(z, z) + c;" +
                    "    }" +
                    "    if(iter == max_iter) {" +
                    "        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);" +
                    "    } else {" +
                    "        float t = iter / max_iter;" +
                    "        t = pow(t, 0.5);" +
                    "        vec3 color = iceColor(t);" +
                    "        gl_FragColor = vec4(color, 1.0);" +
                    "    }" +
                    "}";

    private float[] vertices = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f
    };

    private FloatBuffer vertexBuffer;

    public MandelbrotRenderer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        resolutionHandle = GLES20.glGetUniformLocation(program, "resolution");
        zoomHandle = GLES20.glGetUniformLocation(program, "zoom");
        offsetHandle = GLES20.glGetUniformLocation(program, "offset");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
        GLES20.glUniform2f(resolutionHandle, width, height);
        GLES20.glUniform1f(zoomHandle, zoom);
        GLES20.glUniform2f(offsetHandle, offsetX, offsetY);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void setZoom(float newZoom) {
        zoom = newZoom;
    }

    public void setOffset(float newOffsetX, float newOffsetY) {
        offsetX = newOffsetX;
        offsetY = newOffsetY;
    }

    public float getZoom() {
        return zoom;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }
}
