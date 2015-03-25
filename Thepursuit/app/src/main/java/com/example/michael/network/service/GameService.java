package com.example.michael.network.service;

import com.example.michael.model.game.Game;
import com.example.michael.model.game.Player;
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

    public void onCreateGame(HashMap<String, String> rules){
        ParseCloud.callFunctionInBackground("createGame", rules, GAME_CALLBACK);
    }

    public void onJoinGame(HashMap<String, Object> s){
        ParseCloud.callFunctionInBackground("joinGame", s, GAME_CALLBACK);
    }

    public void onCreatePlayer(){
        ParseCloud.callFunctionInBackground("createPlayer", new HashMap<String, Object>(), PLAYER_CALLBACK);
    }

    public void onUpdateGame(HashMap<String, Object> game){
        ParseCloud.callFunctionInBackground("updateGame", game, GAME_CALLBACK);
    }

    private final FunctionCallback<ParseObject> GAME_CALLBACK = new FunctionCallback<ParseObject>() {

        @Override
        public void done(ParseObject parseObject, ParseException e) {
            if(e == null) {
                eventBus.post(new Game(parseObject));
            }
            else
                eventBus.post(e);
        }
    };

    private final FunctionCallback<ParseObject> PLAYER_CALLBACK = new FunctionCallback<ParseObject>() {

        @Override
        public void done(ParseObject parseObject, ParseException e) {
            if(e == null) {
                eventBus.post(new Player(parseObject));
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
