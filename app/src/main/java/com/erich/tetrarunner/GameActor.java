package com.erich.tetrarunner;

/**
 * Created by Erich on 11/28/2014.
 */
public class GameActor
{
    float[] points;
    float[] ambient;
    float[] diffuse;
    float[] specular;
    float shine;
    public float[] getPoints()
    {
        return points;
    }

    public enum ActorType
    {
        barrier, pit, coin, floor, empty
    }

    ActorType _type;
    public ActorType getType()
    {
        return _type;
    }

    public GameActor(ActorType t)
    {
        _type = t;
        if (_type == ActorType.barrier) {
            points = GeometryBuilder.getCube();
            ambient = new float[]{0.3f, 0.1f, 0.1f, 1.0f};
            diffuse = new float[]{0.6f, 0.3f, 0.3f, 1.0f};
            specular = new float[]{0.7f, 0.5f, 0.5f, 1.0f};
            shine = 2.0f;
        }
        else if (_type == ActorType.coin) {
            points = GeometryBuilder.getTetrahedron();
            ambient = new float[]{0.3f, 0.3f, 0.1f, 0.8f};
            diffuse = new float[]{0.7f, 0.7f, 0.2f, 0.8f};
            specular = new float[]{1.0f, 1.0f, 0.3f, 1.0f};
            shine = 5.0f;
        }
        else if (_type == ActorType.pit) {
            points = GeometryBuilder.getFourSides();
            ambient = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
            diffuse = new float[]{0.2f, 0.4f, 0.4f, 1.0f};
            specular = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
            shine = 1.0f;
        }
        else if (_type == ActorType.floor) {
            points = GeometryBuilder.getPlane();
            ambient = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
            diffuse = new float[]{0.2f, 0.4f, 0.4f, 1.0f};
            specular = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
            shine = 1.0f;
        }
    }
}
