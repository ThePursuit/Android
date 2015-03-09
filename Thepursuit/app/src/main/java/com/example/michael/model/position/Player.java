package com.example.michael.model.position;

import java.util.HashMap;

/**
 * Created by christofer on 3/6/15.
 */
public class Player {
    private HashMap<String, Object> player = new HashMap<>();

    public Player(String playerID){
        player.put("gameID", null);
        player.put("playerID", playerID);
        player.put("isPrey", false);
        player.put("playerColor", null);
        player.put("long", 0.0);
        player.put("lat", 0.0);
    }

    public void setGameID(String gameID){
        player.put("gameID", gameID);
    }
    public void setPlayerID(){

    }
    public void setIsPrey(boolean isPrey){
        player.put("isPrey", isPrey);
    }
    public void setPlayerColor(String playerColor){
        player.put("playerColor", playerColor);
    }
    public void setLong(double lon){
        player.put("long", lon);
    }
    public void setLat(double lat){
        player.put("lat", lat);
    }

    public HashMap<String, Object> getPlayer(){
        return player;
    }
}
