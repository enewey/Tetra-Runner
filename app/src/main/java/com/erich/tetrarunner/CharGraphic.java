package com.erich.tetrarunner;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Erich on 12/11/2014.
 * Abstract class; inherited by the StringGraphic and NumberGraphic classes.
 */
public abstract class CharGraphic
{
    float[] points;
    float[] texPoints;
    float[] mvm;

    /**
     *  Method that renders this graphic.
     */
    public void render()
    {
        //Allocate buffers
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(points.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer pointsBuffer = byteBuffer.asFloatBuffer();
        pointsBuffer.put(points);
        pointsBuffer.rewind();

        byteBuffer = ByteBuffer.allocateDirect(texPoints.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer texBuffer = byteBuffer.asFloatBuffer();
        texBuffer.put(texPoints);
        texBuffer.rewind();
        //Done with buffers

        //Set matrix uniforms
        GLES20.glUniformMatrix4fv(GameActivity._wordsMvmLoc, 1, false, mvm, 0);
        GLES20.glUniformMatrix4fv(GameActivity._wordsProjLoc, 1, false, GameActivity._wordsProjectionMatrix, 0);

        //Bind texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GameActivity._textures[0]);
        GLES20.glUniform1i(GameActivity._textureSamplerLoc, 0);

        //Set vector attribute pointers appropriately
        GLES20.glVertexAttribPointer(GameActivity.POSITION_ATTRIBUTE_ID, 4, GLES20.GL_FLOAT, false, 4 * 4, pointsBuffer);
        GLES20.glVertexAttribPointer(GameActivity.TEXTURE_ATTRIBUTE_ID, 2, GLES20.GL_FLOAT, false, 2 * 4, texBuffer);

        //Enable/disable flags necessary
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        //DRAW 'EM
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, (points.length / 4));
    }
}
