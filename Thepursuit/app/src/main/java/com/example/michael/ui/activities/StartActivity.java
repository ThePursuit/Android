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

import com.example.michael.network.provider.BusProvider;
import com.example.michael.network.provider.ServiceProvider;
import com.example.michael.ui.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

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
        ServiceProvider.getPositionService().onCreatePlayer();
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

}
