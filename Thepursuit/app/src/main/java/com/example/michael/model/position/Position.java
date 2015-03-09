package com.example.michael.model.position;

import java.util.HashMap;

/**
 * Created by christofer on 2/23/15.
 */
public class Position {
    private HashMap<String, String> position = new HashMap<>();
    private double lon;
//    private double lat;

    public Position(String lon /*, double lat*/){
        position.put("name", lon);
   //     position.put("lat", lat);
    }

    public HashMap<String, String> getPosJson(){
        return position;
    }
}
