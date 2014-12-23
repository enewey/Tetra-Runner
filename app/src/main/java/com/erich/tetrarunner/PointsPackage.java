package com.erich.tetrarunner;

/**
 * Created by Erich on 12/17/2014.
 * Class used to pair vectors and their normals together
 */
public class PointsPackage
{
    public float[] points;
    public float[] normals;

    public PointsPackage(float[] p, float[] n)
    {
        this.points = p;
        this.normals = n;
    }
}
