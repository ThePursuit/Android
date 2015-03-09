package com.example.michael.model.position;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christofer on 3/6/15.
 */
public class Game {
    private static ArrayList<ParseObject> gameState = new ArrayList<>();

    public Game(List<ParseObject> state){
        gameState = new ArrayList<>(state);
    }

    public static void setGameState(List<ParseObject> newGameState){
        gameState = new ArrayList<>(newGameState);
    }

    public static ArrayList<ParseObject> getGameState(){
        return gameState;
    }
}
