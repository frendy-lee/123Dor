package com.mikroskil.dor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Frendy on 12/30/2015.
 */
public class World {

    Array<Cowboy> cowboys = new Array<Cowboy>();

    public Array<Cowboy> getCowboys(){
        return cowboys;
    }

    public World(){
        createWorld();
    }

    private void createWorld(){
        cowboys.add(new Cowboy(1, new Vector2(2,3)));
        cowboys.add(new Cowboy(2, new Vector2(7,3)));
    }

}
