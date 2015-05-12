package com.example.michael.ui.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.michael.ui.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CountDownActivity extends ActionBarActivity {

    @InjectView(R.id.timerText) TextView timerText;
    private CountDownTimer cdt;
    private final long startTime = 30000;
    private final long interval = 1000;
    AnimationDrawable drawable;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_count_down);
        View parent = findViewById(R.id.timerText);
        drawable = (AnimationDrawable) getResources().getDrawable(R.drawable.countdown);
        parent.setBackground(drawable);
        ButterKnife.inject(this);
        timerText.setText(String.valueOf(startTime/1000));
        cdt = new CountDownTimer(startTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timerText.setText("GO!");
                Intent intent = new Intent(CountDownActivity.this, GameMapActivity.class);
                intent.putExtra("gameID", getIntent().getStringExtra("gameID"));
                intent.putExtra("nickName", getIntent().getStringExtra("nickName"));
                intent.putExtra("playerObjID", getIntent().getStringExtra("playerObjID"));
                intent.putExtra("isPrey", getIntent().getBooleanExtra("isPrey", false));
                intent.putExtra("isLobbyLeader", getIntent().getBooleanExtra("isLobbyLeader", false));
                startActivity(intent);
                finish();
            }
        };
        cdt.start();
    }

    @Override
    public void onWindowFocusChanged(boolean gained){
        super.onWindowFocusChanged(gained);
        if(gained){
            drawable.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_down, menu);
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
    public void onBackPressed(){
        cdt.cancel();
        super.onBackPressed();
    }

    public void skipBtn(View view){
        cdt.cancel();
        cdt.onFinish();
    }

}
