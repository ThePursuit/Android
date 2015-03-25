package com.example.michael.model.game;

import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by christofer on 3/6/15.
 */
public class Game {
    private static ParseRelation<ParseObject> players;
    private static ParseObject game;
    //private Rules rules = new Rules();
    //private State state = new State();


    public Game(ParseObject state){
        this.game = state;
        players = game.getRelation("players");
    }


    public static void setPlayers(ParseObject newGameState){
        players = newGameState.getRelation("players");
    }

    public static ParseObject getGame(){
        return game;
    }

    public static ParseRelation<ParseObject> getPlayers(){
        return players;
    }
}
