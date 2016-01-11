package com.mikroskil.dor;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Frendy on 12/30/2015.
 */
public class Cowboy {

    public enum State {
        IDLE, SHOOT, WIN, LOSE, HIDE
    }

    static final float SIZE = 1f; // half a unit
    int cowboyId;
    int score=0;
    int score_limit = 0;
    float speed;
    float topSpeed = 0;
    Vector2 position = new Vector2();
    Rectangle bounds = new Rectangle();
    State state = State.IDLE;
    boolean flagHit = false;
    boolean flagShot = false;
    boolean flagReady = false;

    public State getState(){
        return state;
    }
    public void setState(State state){
        this.state = state;
    }

    public int getCowboyId(){
        return cowboyId;
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public Vector2 getPosition(){
        return this.position;
    }

    public void setScore_limit(int limit){
        this.score_limit = limit;
    }
    public int getScore_limit(){
        return this.score_limit;
    }

    public void setTopSpeed(float speed){
        this.topSpeed = speed;
    }
    public float getTopSpeed(){
        return this.topSpeed;
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }
    public float getSpeed(){
        return this.speed;
    }

    public int getScore(){
        return this.score;
    }
    public void setScore(int score){
        this.score = score;
    }
    public void addScore(){
        this.score++;
    }
    public void resetScore(){ this.score = 0;}

    public boolean isFlagHit(){
        return this.flagHit;
    }
    public void setFlagHit(boolean flag){
        this.flagHit = flag;
    }

    public boolean isFlagShot(){
        return this.flagShot;
    }
    public void setFlagShot(boolean flag){
        this.flagShot = flag;
    }

    public boolean isFlagReady() {
        return this.flagReady;
    }
    public void setFlagReady(boolean flag){
        this.flagReady = flag;
    }

    public float getSize(){
        return this.SIZE;
    }

    public Cowboy(int id, Vector2 pos){
        this.cowboyId = id;
        this.position = pos;
        this.bounds.height = 1f;
        this.bounds.width = 1f;
    }
}
