package com.example.michael.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.model.position.Game;
import com.example.michael.model.position.Position;
import com.example.michael.network.provider.BusProvider;
import com.example.michael.network.provider.ServiceProvider;
import com.example.michael.network.service.GameService;
import com.example.michael.ui.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class StartActivity extends ActionBarActivity {

    //initiering av UI-objekt i butterknife
    @InjectView(R.id.textView5) TextView text;
    @InjectView(R.id.greetingButton) Button greetingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.inject(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "KYfJJ2eczwaMD5xlinFYoHhMkHFCSIBxHYEdAwTh", "8B31P1CHuX9tdvL7xNGs3AY7umwmAFUqshFKhe5Q");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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

    public void createGame(View view){
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);

    }

    public void joinGame(View view){
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);

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
    public void onFetchPositionSuccess(String s){
        text.setText(s);
    }

    @Subscribe
    public void onFetchPositionFailed(ParseException event){
        Toast.makeText(this, "Failed to get game", Toast.LENGTH_LONG).show();
        text.setText(event.toString());
    }
    @Subscribe
    public void onTest(ArrayList<ParseObject> derp){
        String lista = "";
        for(ParseObject i : derp){
            lista += i.get("playerID") + " ";
        }
        text.setText(lista);
    }

    @OnClick(R.id.greetingButton)
    public void sayHi(){
        HashMap<String,String> join = new HashMap<>();
        join.put("gameID", "123");
        join.put("playerID", "p4");
        //ServiceProvider.getPositionService().onJoinGame(join);
        /*
        //Create 3 players
        HashMap<String,Object> p1 = new HashMap<>();
        p1.put("playerID", "p1");
        p1.put("playerColor","blue");
        HashMap<String,Object> p2 = new HashMap<>();
        p2.put("playerID", "p2");
        p2.put("playerColor","green");
        HashMap<String,Object> p3 = new HashMap<>();
        p3.put("playerID", "p3");
        p3.put("playerColor","yellow");

        ServiceProvider.getPositionService().onCreatePlayers(p1);
        ServiceProvider.getPositionService().onCreatePlayers(p2);
        ServiceProvider.getPositionService().onCreatePlayers(p3);
        */
        //ServiceProvider.getPositionService().onFetchPosition(new Position("derp"));
    }

}
