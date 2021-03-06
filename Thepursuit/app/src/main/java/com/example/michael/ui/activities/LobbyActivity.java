package com.example.michael.ui.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private String gameID;
    private int gameDuration;
    private int catchRadius;
    private boolean noLobbyLeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);
        nickName = getIntent().getStringExtra("nickName");
        gameID = getIntent().getStringExtra("gameID");
        isLobbyLeader = getIntent().getBooleanExtra("isLobbyLeader", false);
        gameDuration = getIntent().getIntExtra("gameDuration", 0);
        catchRadius = getIntent().getIntExtra("catchRadius", 0);
        lobbyGameCodeView.setText("Game code: " + gameID + "\n"
                                     + "The duration of the game is: " + gameDuration + "min\n" + "The catch radius of the game is: " + catchRadius + "m");;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, getIntent().getStringArrayListExtra("players")) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);

                text.setTextColor(Color.WHITE);

                return view;
            }
        };

        playerList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        playerList.setEnabled(false);
        playerList.setAdapter(adapter);
        /*
         * Thread that updates Player list in the Game session
         * and displays them in the lobby. Currently updating every
         * 1 second (1000 ms). TODO: Cut down and separate code in the query, holy shit it's long
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
                        noLobbyLeader = true;
                        for (ParseObject player : ParseQuery.getQuery("Game").whereEqualTo("gameID", gameID).getFirst().getRelation("players").getQuery().find()) {
                            players.add(player.get("name").toString());
                            if(!isLobbyLeader && player.getBoolean("isCreator")){
                                noLobbyLeader = false;
                            }
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
                        if(!isLobbyLeader && noLobbyLeader){
                            update = false;
                            abort = true;
                            new AlertDialog.Builder(LobbyActivity.this)
                                    .setTitle("Lobby leader has left the game")
                                    .setMessage("Try to join or create a new game")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            try {
                                                ParseObject playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
                                                playerObj.delete();
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                //TODO: Internet connection error?
                                            }
                                        }
                                    })
                                    .show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        //onResume();
                        //finish();
                        //TODO: Could use this to notify player that Game session has been DESTROYED SOMEHOW OO YEAH!!! :> (and maybe force them to change activity view back to previous)
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

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
        new AlertDialog.Builder(this)
                .setTitle("Exit the Players Lobby")
                .setMessage("Are you sure you want to exit the Game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update = false;
                        abort = true;
                        finish();
                        try {
                            ParseObject playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
                            playerObj.delete();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            //TODO: Internet connection error?
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void playGame(View view) {

        if (playBtn.getText().toString().equals("Ready")) {

            ParseObject playerObj;
            try {
                playerObj = ParseQuery.getQuery("Player").get(getIntent().getStringExtra("playerObjID"));
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Internet connection lost. Try again!", Toast.LENGTH_LONG).show();
                return;
            }
            abort = false;
            playBtn.setText("Waiting...");
            final Intent intent = new Intent(this, CountDownActivity.class);
            intent.putExtra("gameID", gameID);
            intent.putExtra("nickName", nickName);
            intent.putExtra("playerObjID", getIntent().getStringExtra("playerObjID"));
            intent.putExtra("isLobbyLeader", isLobbyLeader);
            intent.putExtra("gameDuration", gameDuration);
            intent.putExtra("catchRadius", catchRadius);
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
                                if (!gameState.getBoolean("isPlaying")) {
                                    handler.postDelayed(this, 1000);
                                    //Toast.makeText(getApplicationContext(), gameState.getBoolean("isPlaying")+"", Toast.LENGTH_SHORT).show();
                                } else {
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

        }
        /*
        else {
            playerObj.put("isReady", false);
            playerObj.saveInBackground();
            abort = true;
            playBtn.setText("Play");
        }
        */

    }

    public void rulesInfo(View view){
        FragmentManager fm = getFragmentManager();
        GameStateDialog dialog = new GameStateDialog();
        //dialog.setStatusText("Game rules: Catch the prey = win");
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
