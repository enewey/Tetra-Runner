package com.erich.tetrarunner;

import android.opengl.Matrix;

/**
 * Created by Erich on 12/11/2014.
 */
public class NumberGraphic extends CharGraphic
{
    //CURRENTLY ONLY SUPPORTS NUMBERS.
    public NumberGraphic(float x, float y, char c, float scale)
    {
        mvm = new float[16];
        Matrix.setIdentityM(mvm, 0);
        Matrix.translateM(mvm, 0, x, y, 0.0f);
        Matrix.scaleM(mvm, 0, scale, scale, 1.0f);

        points = GeometryBuilder.getSquare();
        texPoints = new float[]{
            0.0f, 0.25f,
            0.0f, 0.0f,
            0.1f, 0.25f,
            0.1f, 0.25f,
            0.0f, 0.0f,
            0.1f, 0.0f
        };

        int charX = ((int)(c) - 48) % 10;
        int charY = ((int)(c) - 48) / 10;

        for (int i = 0; i < 12; i+=2)
        {
            texPoints[i] += (float)(0.1 * charX);
            texPoints[i+1] += (float)(0.25 * charY);
        }
    }
}
