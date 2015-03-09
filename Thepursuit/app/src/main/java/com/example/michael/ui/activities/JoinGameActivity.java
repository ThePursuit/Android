package com.example.michael.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.michael.network.provider.ServiceProvider;
import com.example.michael.ui.R;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class JoinGameActivity extends ActionBarActivity {

    @InjectView(R.id.joinButton) Button joinButton;
    @InjectView(R.id.playerName) EditText playerName;
    @InjectView(R.id.gameCode) EditText gameCode;

    private static HashMap<String,String> join = new HashMap<>();

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

 //   @OnClick(R.id.joinButton)
    public void joinButton(View view){

        join.put("gameID", gameCode.getText().toString());
        join.put("playerID", playerName.getText().toString());

        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
