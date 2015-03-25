package com.example.michael.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.michael.model.game.Game;
import com.example.michael.model.game.Player;
import com.example.michael.network.provider.BusProvider;
import com.example.michael.network.provider.ServiceProvider;
import com.example.michael.ui.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class JoinGameActivity extends ActionBarActivity {

    @InjectView(R.id.playerName) EditText playerName;
    @InjectView(R.id.gameCode) EditText gameCode;

    private static HashMap<String,String> join = new HashMap<>();
    private Player player;

    public static HashMap<String,String> getJoin(){
        return join;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        ButterKnife.inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onGetPlayer(Player p){
        this.player = p;
    }

    @Override
    public void onResume(){
        super.onResume();
        BusProvider.getBus().register(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        BusProvider.getBus().unregister(this);
    }

 //   @OnClick(R.id.joinButton)
    public void joinButton(View view){

        //join.put("playerID", playerName.getText().toString());
        player.put("gameID", gameCode.getText().toString());
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.setAction("joinGame");
        //ServiceProvider.getPositionService().onJoinGame(player.getPlayer());
        ParseCloud.callFunctionInBackground("joinGame", player.getPlayer(), new FunctionCallback<ParseObject>() {
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    
                } else{

                }
            }
        });
        startActivity(intent);
    }
}