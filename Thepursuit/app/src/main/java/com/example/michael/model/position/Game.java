package com.example.michael.model.position;

import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christofer on 3/6/15.
 */
public class Game {
    private static ParseRelation<ParseObject> players;
    private Rules rules = new Rules();
    private State state = new State();


    public Game(ParseObject state){
        players = state.getRelation("players");
    }


    public static void setPlayers(ParseObject newGameState){
        players = newGameState.getRelation("players");
    }

    public static ParseRelation<ParseObject> getPlayers(){
        return players;
    }
}
