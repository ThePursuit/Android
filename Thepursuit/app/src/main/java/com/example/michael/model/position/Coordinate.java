package com.example.michael.model.position;

import java.util.HashMap;

/**
 * Created by christofer on 2/23/15.
 */
public class Coordinate {
    private HashMap<String, String> coordinate = new HashMap<>();

    public Coordinate(){
        coordinate.put("longitude", null);
        coordinate.put("latitude", null);
    }
    public void setCoordinate(String longitude, String latitude){
        coordinate.put("longitude", longitude);
        coordinate.put("latitude", latitude);
    }
    public HashMap<String, String> getCoordinate(){
        return coordinate;
    }
}
