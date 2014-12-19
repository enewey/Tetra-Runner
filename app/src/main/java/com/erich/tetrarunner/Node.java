package com.erich.tetrarunner;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Erich on 11/26/2014.
 */
public class Node
{
    float[] points;
    float[] normals;
    public void setPoints(PointsPackage p) {
        this.points = p.points;
        this.normals = p.normals;
    }

    float[] transformationMatrix;
    Node child;
    Node sibling;
    float[] ambient;
    float[] diffuse;
    float[] specular;
    float shine;

    public Node(float[] trans)
    {
        transformationMatrix = trans;
    }

    public void setTransformationMatrix(float[] trans)
    {
        transformationMatrix = trans;
    }

    public void setColor(float[] amb, float[] diff, float[] spec)
    {
        this.ambient = amb;
        this.diffuse = diff;
        this.specular = spec;
    }

    public void setShine(float s)
    {
        this.shine = s;
    }

    public void render()
    {
        float[] emissive = new float[4]; // Nodes have no emissive term

        //Buffer binding, can be made its own method
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(points.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer pointsBuffer = byteBuffer.asFloatBuffer();
        pointsBuffer.put(points);
        pointsBuffer.rewind();
        //End buffer binding

        //Buffer binding, can be made its own method
        byteBuffer = ByteBuffer.allocateDirect(normals.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer normalPointsBuffer = byteBuffer.asFloatBuffer();
        normalPointsBuffer.put(normals);
        normalPointsBuffer.rewind();
        //End buffer binding

        GLES20.glVertexAttribPointer(GameActivity.POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, pointsBuffer);
        GLES20.glVertexAttribPointer(GameActivity.NORMAL_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, normalPointsBuffer);

        float[] normalMatrix = GameActivity.getNormalMatrix(transformationMatrix);

        GLES20.glUniformMatrix4fv(GameActivity._mvmLoc, 1, false, transformationMatrix, 0);
        GLES20.glUniformMatrix4fv(GameActivity._normalMatrixLoc, 1, false, normalMatrix, 0);
        GLES20.glUniform4fv(GameActivity._ambientLoc, 1, ambient, 0);
        GLES20.glUniform4fv(GameActivity._diffuseLoc, 1, diffuse, 0);
        GLES20.glUniform4fv(GameActivity._specularLoc, 1, specular, 0);
        GLES20.glUniform4fv(GameActivity._emissiveLoc, 1, emissive, 0);
        GLES20.glUniform1f(GameActivity._shineLoc, shine);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, (int)(points.length / 4));
    }
}
