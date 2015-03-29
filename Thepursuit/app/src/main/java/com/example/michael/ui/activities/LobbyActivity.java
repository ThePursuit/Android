package com.example.michael.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.michael.ui.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LobbyActivity extends ActionBarActivity {

    @InjectView(R.id.lobbyGameCodeView) TextView lobbyGameCodeView;
    @InjectView(R.id.listView) ListView playerList;
    private Handler handler = new Handler();
    private boolean update = true;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);

        lobbyGameCodeView.setText("Game code: " + getIntent().getStringExtra("gameID").toString());
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getIntent().getStringArrayListExtra("players"));
        playerList.setAdapter(adapter);

        /*
         * Thread that updates Player list in the Game session
         * and displays them in the lobby. Currently updating every
         * 3 seconds (3000 ms). TODO: Cut down and separate code in the query, holy shit it's long
         * Stops thread when you press on "Play" button, since flag will be false.
         */
        handler.postDelayed(new Runnable() {
        @Override
        public void run(){
            ArrayList<String> players = new ArrayList<>();
            if(update){
                try {
                    for(ParseObject player : ParseQuery.getQuery("Game").whereEqualTo("gameID", getIntent().getStringExtra("gameID").toString()).getFirst().getRelation("players").getQuery().find()){
                        players.add(player.get("playerID").toString());
                    }
                    adapter.clear();
                    adapter.addAll(players);
                    playerList.setAdapter(adapter);
                } catch (ParseException e) {
                    e.printStackTrace();
                    //TODO: Could use this to notify player that Game session has been DESTROYED SOMEHOW OO YEAH!!! :> (and maybe force them to change activity view back to previous)
                }
                handler.postDelayed(this, 3000);
            }
        }
        }, 3000);

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

    public void playGame(View view){
        String gameID = getIntent().getStringExtra("gameID");
        //TODO: Change attribute name to playerName or playerNick etc...
        String playerID = getIntent().getStringExtra("playerID");
        Intent intent = new Intent(this, GameMapActivity.class);
        intent.putExtra("gameID", gameID);
        intent.putExtra("playerID", playerID);
        update = false;
        startActivity(intent);
    }

}
