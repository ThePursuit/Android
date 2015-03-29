package com.example.michael.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CreateGameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_game, menu);
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

    public void createGameButton(View view){

        ParseCloud.callFunctionInBackground("createGame", new HashMap<String, Object>(), new FunctionCallback<Map<String, ParseObject>>() {
            public void done(Map<String, ParseObject> map, ParseException e) {
                ArrayList<String> players = new ArrayList<>();
                if (e == null) {
                    ParseObject game = map.get("game");
                    String playerID = map.get("player").get("playerID").toString();
                    String gameID = map.get("game").get("gameID").toString();
                    try {
                        for (ParseObject player : game.getRelation("players").getQuery().find()) {
                            players.add(player.get("playerID").toString());
                        }
                        Intent intent = new Intent(CreateGameActivity.this, LobbyActivity.class);
                        intent.putStringArrayListExtra("players", players);
                        intent.putExtra("gameID", gameID);
                        intent.putExtra("playerID", playerID);
                        startActivity(intent);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        //TODO: Print query error
                    }
                } else {
                    //TODO: Implement error notification/window about failing to create game
                    Toast.makeText(getApplicationContext(), "Failed to create a game. Check application/server error.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
