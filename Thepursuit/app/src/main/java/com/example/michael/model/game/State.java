package com.example.michael.model.game;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by christofer on 2/27/15.
 */
public class State {
    private HashMap<String, Object> state = new HashMap<>();

    public State(Date startTime, boolean isPlaying){
        state.put("startTime", startTime);
        state.put("isPlaying", isPlaying);
    }
    public void setStartTime(Date startTime){
        state.put("startTime", startTime);
    }
    public void setIsPlaying(boolean isPlaying){
        state.put("isPlaying", isPlaying);
    }
    public HashMap<String,Object> getState(){
        return state;
    }
}
