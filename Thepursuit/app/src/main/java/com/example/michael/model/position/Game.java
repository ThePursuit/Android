package com.example.michael.model.position;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christofer on 3/6/15.
 */
public class Game {
    private static ArrayList<ParseObject> players = new ArrayList<>();
    private Rules rules = new Rules();
    private State state = new State();

    public Game(List<ParseObject> state){
        players = new ArrayList<>(state);
    }

    public static void setPlayers(List<ParseObject> newGameState){
        players = new ArrayList<>(newGameState);
    }

    public static ArrayList<ParseObject> getPlayers(){
        return players;
    }
}
