package com.erich.tetrarunner;

/**
 * Created by Erich on 11/28/2014.
 */
public class ActorGroup
{
    GameActor[] _upperLayer;
    GameActor[] _lowerLayer;

    int position;
    public int getPosition() {
        return position;
    }

    public GameActor[] getUpperLayer()
    {
        return _upperLayer;
    }

    public GameActor[] getLowerLayer()
    {
        return _lowerLayer;
    }

    public ActorGroup(GameActor[] up, GameActor[] floor, int p)
    {
        this._upperLayer = up;
        this._lowerLayer = floor;
        position = p;
    }
}
