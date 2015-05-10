package com.example.michael.ui.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class StartActivity extends ActionBarActivity implements Button.OnTouchListener, MediaPlayer.OnCompletionListener {
    //@InjectView(R.id.recordButton)
    //Button recordBtn;
    String mFileName;
    MediaPlayer mPlayer;
    MediaRecorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.inject(this);
        mFileName = getFilesDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        //recordBtn.setOnTouchListener(this);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
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
                    //intent.putExtra("playerID", player.get("playerID").toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to connect to server. Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
     //       recordBtn.setPressed(true);
     //       recordBtn.setText("Kappa");

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("KappaTest", "prepare() failed");
            }
            mRecorder.start();
            return true;
        } else if(event.getAction() == MotionEvent.ACTION_UP){
           // recordBtn.setPressed(false);
           // recordBtn.setText("Record");
           // recordBtn.setEnabled(false);

            //Stop recording
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            //Convert
            FileInputStream fis;
            File fileObj = new File(mFileName);
            byte[] data = new byte[(int) fileObj.length()];

            try {
                fis = new FileInputStream(fileObj);
                fis.read(data);
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Upload soundfile to server
            try {
                ParseObject game = ParseQuery.getQuery("Game").get("TEbNXOLE2p");//Hard coded, change
                game.put("sound", data);
                game.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Retrieve sound file from server
            try {
                ParseObject game = ParseQuery.getQuery("Game").get("TEbNXOLE2p");
                /*
                ParseFile sf = game.getParseFile("soundFile");
                soundURL = sf.getUrl();
                */
                byte[] storedSound = game.getBytes("sound");
                //File storedFile = new File(getFilesDir().getAbsolutePath(), "RetrievedAudio");
                File tempFile = File.createTempFile("TempRetrievedAudio", "3gp");

                FileOutputStream FOS = new FileOutputStream(tempFile);
                FOS.write(storedSound);
                FOS.close();

                FileInputStream storedFIS = new FileInputStream(tempFile);

                //Play sound
                try {
                    mPlayer.setDataSource(storedFIS.getFD());
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e("KappaTest", "prepare() failed");
                }
                return true;

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //recordBtn.setText("DONE!");
        //recordBtn.setEnabled(true);
        mp.reset();
        //mp.release(); TODO: Use this when a game session has ended!
    }

}
