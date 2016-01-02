package com.mikroskil.dor;

/**
 * Created by Frendy on 12/31/2015.
 */
public class GameMode {

    public enum GameType {
        CLASSIC, FIRST_TO_1, FIRST_TO_3, FIRST_TO_5
    }

    private int round=0;
    private int round_limit=3;
    private int score_limit = 0;
    private boolean flagUseRound = true;

    GameType type = GameType.CLASSIC;

    public GameMode(GameType mode){
        if(mode.equals(GameType.CLASSIC)){
            this.type = GameType.CLASSIC;
            setRound(1);
        }else if(mode.equals(GameType.FIRST_TO_1)) {
            this.type = GameType.FIRST_TO_1;
            this.flagUseRound = false;
            this.score_limit = 1;
        }else if(mode.equals(GameType.FIRST_TO_3)){
            this.type = GameType.FIRST_TO_3;
            this.flagUseRound = false;
            this.score_limit = 3;
        }else if(mode.equals(GameType.FIRST_TO_5)) {
            this.type = GameType.FIRST_TO_5;
            this.flagUseRound = false;
            this.score_limit = 5;
        }else{
            this.type = GameType.CLASSIC;
            setRound(1);
        }
    }

    public int getScore_limit(){
        return this.score_limit;
    }

    public void setRound(int round){
        this.round = round;
    }
    public int getRound(){
        return this.round;
    }
    public boolean nextRound(){
        if((this.round + 1) <= this.round_limit){
            this.round = this.round+1;
            return true;
        }else{
            return false;
        }
    }

    public GameType getGameType(){
        return this.type;
    }

}
