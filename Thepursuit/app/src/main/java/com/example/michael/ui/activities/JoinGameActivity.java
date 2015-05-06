package com.example.michael.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class JoinGameActivity extends ActionBarActivity implements EditText.OnKeyListener {

    @InjectView(R.id.gameCode) EditText gameCode;
    @InjectView(R.id.playerName) EditText playerName;
    private String gameID;
    private String playerObjID;
    private String nickName;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        ButterKnife.inject(this);
        playerName.setOnKeyListener(this);
        sharedPref = getSharedPreferences("com.example.michael.PREFERENCE_FILE_KEY", MODE_PRIVATE);
        editor = sharedPref.edit();
        String nickName = sharedPref.getString("nickname", "");
        playerName.setText(nickName);
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

    /*
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
    */

    @Override
    public void onBackPressed(){
        try {
            ParseObject playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
            playerObj.delete();
        } catch (ParseException e) {
            e.printStackTrace();
            //TODO: Internet connection error?
        }
        super.onBackPressed();
    }

    public void joinButton(View view){
        //Store name locally
        editor.putString("nickname", playerName.getText().toString());
        editor.commit();

        HashMap<String, Object> joinInfo = new HashMap<>();
        gameID = gameCode.getText().toString();
        playerObjID = getIntent().getStringExtra("playerObjID");
        nickName = playerName.getText().toString(); //Check if it's an empty string, make it "null"?
        joinInfo.put("playerObjID", playerObjID);
        joinInfo.put("gameID", gameID);

        /*
         * Store nickname in server database
         */
        try {
            ParseObject playerObj = ParseQuery.getQuery("Player").get(playerObjID);
            playerObj.put("name", nickName);
            playerObj.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseCloud.callFunctionInBackground("joinGame", joinInfo, new FunctionCallback<ParseObject>() {
            public void done(ParseObject game, ParseException e) {
                ArrayList<String> players = new ArrayList<>();
                if (e == null) {
                    try {
                        for (ParseObject player : game.getRelation("players").getQuery().find()) {
                            players.add(player.get("name").toString());
                        }
                        Intent intent = new Intent(JoinGameActivity.this, LobbyActivity.class);
                        intent.putStringArrayListExtra("players", players);
                        intent.putExtra("gameID", gameID);
                        intent.putExtra("nickName", nickName); // May be redundant. Check for other intents.
                        intent.putExtra("playerObjID", playerObjID);
                        intent.putExtra("isLobbyLeader", false);
                        startActivity(intent);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        //TODO: Print query error
                    }
                } else {
                    //TODO: Implement error notification/window about failing to join game
                    Toast.makeText(getApplicationContext(), "Failed to join a game. Check application/server error.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //Store nickname locally so it remembers everytime at startup
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
            editor.putString("nickname", playerName.getText().toString());
            editor.commit();
            return true;
        }
        return false;
    }
}