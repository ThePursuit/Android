package com.example.michael.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.model.position.Game;
import com.example.michael.network.provider.BusProvider;
import com.example.michael.network.provider.ServiceProvider;
import com.example.michael.ui.R;
import com.parse.ParseObject;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LobbyActivity extends ActionBarActivity {

    @InjectView(R.id.lobbyGameCodeView) TextView lobbyGameCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);
        lobbyGameCodeView.setText("Game code: " + JoinGameActivity.getJoin().get("gameID"));

        ServiceProvider.getPositionService().onJoinGame(JoinGameActivity.getJoin());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
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
    @Subscribe
    public void derp(Game test) {
        ArrayList<String> players = new ArrayList<>();

        for(ParseObject player : test.getGameState()){
           players.add(player.get("playerID").toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                       android.R.layout.simple_list_item_1, players);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

//    @OnClick(R.id.joinButton)
    public void playGame(View view){
        Intent intent = new Intent(this, GameMapActivity.class);
        startActivity(intent);
    }
}
