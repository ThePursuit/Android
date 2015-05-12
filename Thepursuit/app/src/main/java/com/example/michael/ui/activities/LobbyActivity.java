package com.example.michael.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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


public class LobbyActivity extends ActionBarActivity implements GameStateDialog.Communicator {

    @InjectView(R.id.lobbyGameCodeView)
    TextView lobbyGameCodeView;
    @InjectView(R.id.listView)
    ListView playerList;
    @InjectView(R.id.playButton)
    Button playBtn;
    //@InjectView(R.id.readyButton) Button readyBtn;
    private Handler handler = new Handler();
    private boolean canStart = false;
    private boolean abort; //Bool to kill the thread
    private boolean update = true;
    private boolean isLobbyLeader;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> players = new ArrayList<>();
    private ArrayList<Integer> indices = new ArrayList<>();
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);
        nickName = getIntent().getStringExtra("nickName");
        isLobbyLeader = getIntent().getBooleanExtra("isLobbyLeader", false);
        lobbyGameCodeView.setText("Game code: " + getIntent().getStringExtra("gameID").toString() + "\n"
                                     + "The duration of the game is: " + "\n" + "The radius of the game is: ");;
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_checked, getIntent().getStringArrayListExtra("players")); // Ugly for now, doesn't show connected players at FIRST!
        playerList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        playerList.setEnabled(false);
        playerList.setAdapter(adapter);

        /*
         * Thread that updates Player list in the Game session
         * and displays them in the lobby. Currently updating every
         * 3 seconds (3000 ms). TODO: Cut down and separate code in the query, holy shit it's long
         * Stops thread when you press on "Play" button, since flag will be false.
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                players.clear();
                indices.clear();
                if (update) {
                    try {
                        int index = 0;
                        for (ParseObject player : ParseQuery.getQuery("Game").whereEqualTo("gameID", getIntent().getStringExtra("gameID").toString()).getFirst().getRelation("players").getQuery().find()) {
                            players.add(player.get("name").toString());
                            if (player.getBoolean("isReady")) {
                                indices.add(index);
                            }
                            index++;
                        }
                        adapter.clear();
                        adapter.addAll(players);
                        playerList.setAdapter(adapter);
                        for (Integer n : indices) {
                            playerList.setItemChecked(n, true);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        //onResume();
                        //finish();
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

    @Override
    public void onBackPressed() {
        update = false;
        abort = true;
        super.onBackPressed();
    }

    public void playGame(View view) {

        ParseObject playerObj = null;
        try {
            playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (playBtn.getText().toString().equals("Ready")) {
            abort = false;
            playBtn.setText("Waiting...");
            final String gameID = getIntent().getStringExtra("gameID");
            final Intent intent = new Intent(this, CountDownActivity.class);
            intent.putExtra("gameID", gameID);
            intent.putExtra("nickName", nickName);
            intent.putExtra("playerObjID", getIntent().getStringExtra("playerObjID"));
            intent.putExtra("isLobbyLeader", isLobbyLeader);
            playerObj.put("isReady", true);
            playerObj.saveInBackground();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!canStart) {
                        try {
                            canStart = true;
                            for (ParseObject player : ParseQuery.getQuery("Game").whereEqualTo("gameID", gameID).getFirst().getRelation("players").getQuery().find()) {
                                if (!player.getBoolean("isReady")) {
                                    canStart = false;
                                    break;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        handler.postDelayed(this, 1000);
                    } else if (abort) {
                        //Do nothing, doesn't repeat thread. Might still be a bug when pressing back and everyone is ready...
                    } else {
                        update = false;

                        if (isLobbyLeader) {
                            HashMap<String, Object> startGameInfo = new HashMap<>();
                            startGameInfo.put("gameID", gameID);
                            ParseCloud.callFunctionInBackground("startGame", startGameInfo, new FunctionCallback<ParseObject>() {
                                public void done(ParseObject game, ParseException e) {
                                    try {
                                        ParseObject playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
                                        intent.putExtra("isPrey", playerObj.getBoolean("isPrey"));
                                        intent.putExtra("playerObjID", getIntent().getStringExtra("playerObjID"));
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            //NOTE: We need to ask for the query again to get the updated state, it's not a bug/redundancy!
                            try {

                                ParseObject gameState = ParseQuery.getQuery("Game").whereEqualTo("gameID", gameID).getFirst().getRelation("state").getQuery().getFirst();
                                if(!gameState.getBoolean("isPlaying")){
                                    handler.postDelayed(this, 1000);
                                    //Toast.makeText(getApplicationContext(), gameState.getBoolean("isPlaying")+"", Toast.LENGTH_SHORT).show();
                                } else{
                                    //Toast.makeText(getApplicationContext(), gameState.getBoolean("isPlaying")+"", Toast.LENGTH_SHORT).show();
                                    ParseObject playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
                                    intent.putExtra("isPrey", playerObj.getBoolean("isPrey"));
                                    intent.putExtra("playerObjID", getIntent().getStringExtra("playerObjID"));
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                    }
                }
            }, 1000);

        } else {
            playerObj.put("isReady", false);
            playerObj.saveInBackground();
            abort = true;
            playBtn.setText("Play");
        }

    }

    public void rulesInfo(View view){
        FragmentManager fm = getFragmentManager();
        GameStateDialog dialog = new GameStateDialog();
        dialog.setStatusText("Game rules: Catch the prey = win");
        dialog.show(fm, "Game rules");
    }

    @Override
    public void onDialogMessage() {
        //This is where you get after you've pressed Ok on the dialog and the dialog has been dismissed.
    }

    /*
    public void readyGame(View view) throws ParseException {
        ParseObject playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
        if (readyBtn.getText().toString().equals("Ready")) {
            readyBtn.setText("Unready");
            playerObj.put("isReady", true);
            playerObj.saveInBackground();
        } else {
            readyBtn.setText("Ready");
            playerObj.put("isReady", false);
            playerObj.saveInBackground();
        }
    }
    */

}
