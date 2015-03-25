package com.example.michael.model.game;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.HashMap;

/**
 * Created by christofer on 3/6/15.
 */
public class Player extends ParseObject {
    private HashMap<String, Object> player = new HashMap<>();



    public Player(String gameID, String playerID, boolean isPrey, String playerColor, boolean isReady, ParseGeoPoint location){
        player.put("gameID", gameID); //remove later on
        player.put("playerID", playerID);
        player.put("isPrey", isPrey);
        player.put("playerColor", playerColor);
        player.put("isReady", isReady);
        player.put("location", location);
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
    public void setIsReady(boolean isReady){
        player.put("isReady", isReady);
    }
    public void setLocation(ParseGeoPoint location){
        player.put("location", location);
    }
    public HashMap<String, Object> getPlayer(){
        return player;
    }
}
