package com.example.michael.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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


<<<<<<< HEAD
public class StartActivity extends ActionBarActivity implements GameStateDialog.Communicator {
    private CountDownTimer cdt;
    private final long startTime = 30000;
    private final long interval = 100;
    private ProgressBar pb;
=======
public class StartActivity extends ActionBarActivity {
>>>>>>> 6af1830ce09c4e4101f0853412f0389e28a5af6d

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        cdt = new CountDownTimer(startTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                pb.setProgress(pb.getProgress()-1);
            }

            @Override
            public void onFinish() {
                pb.setProgress(0);
                Toast.makeText(getApplicationContext(), "Kappa", Toast.LENGTH_LONG).show();
            }
        };
        cdt.start();
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
<<<<<<< HEAD
        dialog.setStatusText("Game rules: Catch the prey = win");
=======
>>>>>>> 6af1830ce09c4e4101f0853412f0389e28a5af6d
        dialog.show(fm, "Game rules");
    }

}
