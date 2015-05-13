package com.example.michael.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    public void createGame(View view) {
        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);
    }

    public void joinGame(View view) {
        ParseCloud.callFunctionInBackground("createPlayer", new HashMap<String, Object>(), new FunctionCallback<ParseObject>() {
            public void done(ParseObject player, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(StartActivity.this, JoinGameActivity.class);
                    intent.putExtra("playerObjID", player.getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to connect to server. Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void rulesBtn(View view) {
        FragmentManager fm = getFragmentManager();
        GameStateDialog dialog = new GameStateDialog();
        dialog.show(fm, "Game rules");
    }

}
