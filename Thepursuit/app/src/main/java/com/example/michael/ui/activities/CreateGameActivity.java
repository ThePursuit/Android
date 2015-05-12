package com.example.michael.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
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


public class CreateGameActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener, Switch.OnCheckedChangeListener, EditText.OnKeyListener {

    @InjectView(R.id.nickNameText) EditText nickNameText;
    @InjectView(R.id.catchRadiusSeekBar) SeekBar radiusSeekBar;
    @InjectView(R.id.timeSeekBar) SeekBar timeSeekBar;
    @InjectView(R.id.catchRadiusText) TextView catchRadiusText;
    @InjectView(R.id.timeText) TextView timeText;
    @InjectView(R.id.areaRadiusText) TextView areaRadiusText;
    @InjectView(R.id.areaRadiusSeekBar) SeekBar areaRadiusSeekBar;
    @InjectView(R.id.fixedGASwitch) Switch fixedGASwitch;
    private int catchRadiusProgress;
    private int timeProgress;
    private int areaRadiusProgress;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.inject(this);

        radiusSeekBar.setOnSeekBarChangeListener(this);
        timeSeekBar.setOnSeekBarChangeListener(this);
        areaRadiusSeekBar.setOnSeekBarChangeListener(this);
        fixedGASwitch.setOnCheckedChangeListener(this);
        nickNameText.setOnKeyListener(this);

        sharedPref = getSharedPreferences("com.example.michael.PREFERENCE_FILE_KEY", MODE_PRIVATE);
        editor = sharedPref.edit();
        String nickName = sharedPref.getString("nickname", "");
        nickNameText.setText(nickName);

        //timeSeekBar.setEnabled(false); To disable game time?
        catchRadiusProgress = radiusSeekBar.getProgress() + 1;
        timeProgress = timeSeekBar.getProgress() + 2;
        areaRadiusProgress = (areaRadiusSeekBar.getProgress() + 1)*100;
        catchRadiusText.setText("Catch Radius: " + catchRadiusProgress + "m");
        timeText.setText("Time: " + timeProgress + "min");
        areaRadiusText.setText("Area radius: " + areaRadiusProgress + "m");
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
        //Store nickname locally
        editor.putString("nickname", nickNameText.getText().toString());
        editor.commit();

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
                    setRulesInfo.put("radius", areaRadiusProgress);
                    setRulesInfo.put("catchRadius", catchRadiusProgress);
                    setRulesInfo.put("duration", timeProgress);
                    setRulesInfo.put("maxPlayers", 8);//TODO: Delete from server and here...
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
                        intent.putExtra("gameDuration", timeProgress);
                        intent.putExtra("catchRadius", catchRadiusProgress);
                        startActivity(intent);
                        finish();
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
            catchRadiusText.setText("Catch Radius: " + catchRadiusProgress + "m");
        } else if(seekBar == timeSeekBar){
            timeProgress = progress + 2;
            timeText.setText("Time: " + timeProgress + "min");
        } else if(seekBar == areaRadiusSeekBar){
            areaRadiusProgress = (progress + 1)*100;
            areaRadiusText.setText("Area radius: " + areaRadiusProgress + "m");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isChecked){
            areaRadiusSeekBar.setEnabled(false);
            areaRadiusProgress = 0;
            areaRadiusText.setText("Not restricted");
        } else{
            areaRadiusSeekBar.setEnabled(true);
            areaRadiusProgress = (areaRadiusSeekBar.getProgress()+1)*100;
            areaRadiusText.setText("Area radius: " + areaRadiusProgress + "m");
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
            editor.putString("nickname", nickNameText.getText().toString());
            editor.commit();
            return true;
        }
        return false;
    }
}
