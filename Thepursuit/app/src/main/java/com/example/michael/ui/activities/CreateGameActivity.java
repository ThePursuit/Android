package com.example.michael.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CreateGameActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    @InjectView(R.id.nickNameText) EditText nickNameText;
    @InjectView(R.id.radiusSeekBar) SeekBar radiusSeekBar;
    @InjectView(R.id.timeSeekBar) SeekBar timeSeekBar;
    @InjectView(R.id.textView3) TextView tv3;
    @InjectView(R.id.textView4) TextView tv4;
    private int catchRadiusProgress;
    private int timeProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.inject(this);
        radiusSeekBar.setOnSeekBarChangeListener(this);
        timeSeekBar.setOnSeekBarChangeListener(this);
        //timeSeekBar.setEnabled(false); To disable game time?
        catchRadiusProgress = radiusSeekBar.getProgress() + 1;
        timeProgress = timeSeekBar.getProgress() + 1;
        tv3.setText("Catch Radius: " + catchRadiusProgress + "m");
        tv4.setText("Time: " + timeProgress + "min");
    }

    @Override
    public void onBackPressed(){
       super.onBackPressed();
        //TODO: Back touch/button pressed...
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
                    ParseObject playerObj = map.get("player");
                    playerObj.put("name", nickNameText.getText().toString());
                    playerObj.saveInBackground();
                    String gameID = game.get("gameID").toString();

                    HashMap<String, Object> setRulesInfo = new HashMap<>();
                    setRulesInfo.put("gameID", gameID);
                    setRulesInfo.put("radius", 100);
                    setRulesInfo.put("catchRadius", catchRadiusProgress);
                    setRulesInfo.put("duration", timeProgress);
                    setRulesInfo.put("maxPlayers", 8);
                    ParseCloud.callFunctionInBackground("setRules", setRulesInfo, new FunctionCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject game, ParseException e) {
                            //TODO: Do something when u created rules or not? Toast.makeText(getApplicationContext(), "NEMAS PROBLEMAS!", Toast.LENGTH_LONG).show();
                        }
                    });

                    try {
                        for (ParseObject player : game.getRelation("players").getQuery().find()) {
                            players.add(player.get("name").toString());
                        }
                        Intent intent = new Intent(CreateGameActivity.this, LobbyActivity.class);
                        intent.putStringArrayListExtra("players", players);
                        intent.putExtra("gameID", gameID);
                        intent.putExtra("nickName", nickNameText.getText().toString());
                        intent.putExtra("playerObjID", playerObj.getObjectId());
                        intent.putExtra("isLobbyLeader", true);
                        startActivity(intent);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        //TODO: Print query error
                    }
                } else {
                    //TODO: Implement error notification/window about failing to create game
                    Toast.makeText(getApplicationContext(), "Failed to create a game. Check application/server error. Might be internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == radiusSeekBar){
            catchRadiusProgress = progress + 1;
            tv3.setText("Catch Radius: " + catchRadiusProgress + "m");
        } else{
            timeProgress = progress + 1;
            tv4.setText("Time: " + timeProgress + "min");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
