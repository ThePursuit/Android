package com.example.michael.model.game;

import java.util.HashMap;

/**
 * Created by christofer on 3/11/15.
 */
public class Rules {
    private HashMap<String, Object> rules = new HashMap<>();

    public Rules(int radius, int catchRadius, int duration, int maxPlayers){
        rules.put("radius", radius);
        rules.put("catchRadius", catchRadius);
        rules.put("duration", duration);
        rules.put("maxPlayers", maxPlayers);
    }
    public void setRadius(int radius){
        rules.put("radius", radius);
    }
    public void setCatchRadius(int catchRadius){
        rules.put("catchRadius", catchRadius);
    }
    public void setDuration(int duration) {
        rules.put("duration", duration);
    }
    public void setMaxPlayers(int maxPlayers) {
        rules.put("maxPlayers", maxPlayers);
    }
    public HashMap<String,Object> getRules(){
        return rules;
    }
}
