package com.example.michael.network.service;

import com.example.michael.model.game.Game;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.List;

/**
 * Created by christofer on 2/23/15.
 */
public class GameService {

    private Bus eventBus;

    public GameService(Bus eventBus) {
        this.eventBus = eventBus;
    }

    /*
    public void onFetchPosition(Coordinate pos){
        ParseCloud.callFunctionInBackground("hello", pos.getPosJson(), ON_FETCH_POSITION_CALLBACK);
    }
    */
    public void onCreatePlayers(HashMap<String, Object> hash){
        ParseCloud.callFunctionInBackground("createPlayer", hash, ON_FETCH_POSITION_CALLBACK);
    }

    public void onCreateGame(HashMap<String, String> rules){
        ParseCloud.callFunctionInBackground("createGame", rules, testar);
    }

    public void onJoinGame(HashMap<String, String> s){
        ParseCloud.callFunctionInBackground("joinGame", s, testar);
    }

    private final FunctionCallback<ParseObject> testar = new FunctionCallback<ParseObject>() {

        @Override
        public void done(ParseObject parseObject, ParseException e) {
            if(e == null) {
                eventBus.post(new Game(parseObject));
            }
            else
                eventBus.post(e);
        }
    };

    private final FunctionCallback<String> ON_FETCH_POSITION_CALLBACK = new FunctionCallback<String>() {
        @Override
        public void done(String s, ParseException e) {
            if(e == null){
                eventBus.post(s);
            }
            else
                eventBus.post(e);
        }
    };
}
