package com.example.michael.model.position;

import java.util.HashMap;

/**
 * Created by christofer on 3/6/15.
 */
public class Player {
    private HashMap<String, Object> player = new HashMap<>();
    private Coordinate coordinate = new Coordinate();

    public Player(String playerID){
        player.put("gameID", null); //remove later on
        player.put("playerID", playerID);
        player.put("isPrey", false);
        player.put("playerColor", null);
        player.put("isReady", null);
        player.put("coordinate", coordinate);
    }

    public void setGameID(String gameID){
        player.put("gameID", gameID);
    } // remove later on
    public void setPlayerID(){ } // should be removed later on since the player can't change name in a game and the name should be initilized(derp?) on creation

    public void setIsPrey(boolean isPrey){
        player.put("isPrey", isPrey);
    }
    public void setPlayerColor(String playerColor){
        player.put("playerColor", playerColor);
    }
    public void setCoordinate(){
        //change
        player.put("coordinate", coordinate.getCoordinate());
    }

    public HashMap<String, Object> getPlayer(){
        return player;
    }
}
