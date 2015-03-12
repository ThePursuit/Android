package com.example.michael.thepursuit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JoinGameActivity extends ActionBarActivity {

    public static ArrayList<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
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

    public void joinButton(View view){
        players = new ArrayList<>();
        HashMap<String, Object> request = new HashMap<>();
        EditText text = (EditText) findViewById(R.id.gameCode);
        String gameCode = text.getText().toString();
        request.put("gameID", gameCode);
        ParseCloud.callFunctionInBackground("joinGame", request, new FunctionCallback<List<ParseObject>>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (ParseObject po : result){
                        players.add(po.getString("playerID"));
                    }
                }
            }
        });

        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
