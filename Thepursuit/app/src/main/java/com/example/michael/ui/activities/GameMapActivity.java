package com.example.michael.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameMapActivity extends FragmentActivity implements Button.OnTouchListener, MediaPlayer.OnCompletionListener, GameStateDialog.Communicator {

    @InjectView(R.id.distanceView)
    TextView distanceView;
    @InjectView(R.id.catchButton)
    Button catchBtn;
    @InjectView(R.id.talkButton)
    Button talkBtn;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Handler locHandler;
    private Handler updateHandler;
    private Location preyLoc;
    private String playerObjID;
    private Location loc;
    private boolean update;
    private boolean isPrey;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String mFileName;
    private String gameID;
    private int markerRadius;
    private List<byte[]> playedAudioFiles;
    private Map<String, MarkerOptions> markers;
    private Runnable updateLocation;
    private Runnable retrieveAudio;
    private Thread locationThread;
    private Thread audioThread;
    private long updateLocationInterval;
    private int distanceToPrey;
    private CountDownTimer cdt;
    private final long startTime = 5000;
    private final long interval = 1000;
    private String nickName;
    private FragmentManager fragmentManager;
    private GameStateDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);
        ButterKnife.inject(this);
        fragmentManager = getFragmentManager();
        dialog = new GameStateDialog();
        mFileName = getFilesDir().getAbsolutePath();
        mFileName += "/AudioRecord_ThePursuit.3gp";
        gameID = getIntent().getStringExtra("gameID");
        nickName = getIntent().getStringExtra("nickName");
        talkBtn.setOnTouchListener(this);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        preyLoc = new Location("");
        preyLoc.setLatitude(0);
        preyLoc.setLongitude(0);
        update = true; //Make it true elsewhere...
        markerRadius = 25;
        markers = new HashMap<>();
        playedAudioFiles = new ArrayList<>();
        playerObjID = getIntent().getStringExtra("playerObjID");
        isPrey = getIntent().getBooleanExtra("isPrey", false);
        loc = new Location("");
        loc.setLatitude(0);
        loc.setLongitude(0);
        updateLocationInterval = 1000;
        if (isPrey) {
            catchBtn.setEnabled(false);
        }

        locHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //TODO: Change this approach, too laggy...
                getMyLocation();
            }
        };

        updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mMap.clear();
                for (MarkerOptions mo : markers.values()) {
                    mMap.addMarker(mo);
                }
                if (isPrey) {
                    distanceView.setText("You're the Prey, Hide!");//Create separate methods to call when you're prey and so on...
                } else {
                    distanceToPrey = Math.round(loc.distanceTo(preyLoc));
                    if (distanceToPrey < 10) {
                        distanceView.setText("You're very close!");
                    } else {
                        distanceView.setText("Prey: " + distanceToPrey + "m");
                    }

                    //Change update frequency
                    if (distanceToPrey > 100) {
                        updateLocationInterval = 2000;
                    } else if (distanceToPrey > 20) {
                        updateLocationInterval = 1000;
                    } else {
                        updateLocationInterval = 500;
                    }
                }
            }
        };

        updateLocation = new Runnable() {
            @Override
            public void run() {
                while (update) {
                    HashMap<String, Object> updateInfo = new HashMap<>();
                    locHandler.sendEmptyMessage(0);
                    updateInfo.put("gameID", gameID);
                    updateInfo.put("playerObjID", getIntent().getStringExtra("playerObjID"));
                    updateInfo.put("latitude", loc.getLatitude());
                    updateInfo.put("longitude", loc.getLongitude());

                    ParseCloud.callFunctionInBackground("updateGame", updateInfo, new FunctionCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject game, ParseException e) {
                            if (e == null) {
                                try {
                                    for (ParseObject player : game.getRelation("players").getQuery().find()) {
                                        ParseGeoPoint geo = (ParseGeoPoint) player.get("location");
                                        if (player.getBoolean("isPrey")) {
                                            preyLoc.setLatitude(geo.getLatitude());
                                            preyLoc.setLongitude(geo.getLongitude());
                                        } else if (!player.getObjectId().equals(getIntent().getStringExtra("playerObjID"))) {
                                            String playerName = player.get("name").toString();
                                            LatLng latLng = new LatLng(geo.getLatitude(), geo.getLongitude());
                                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(playerName).icon(BitmapDescriptorFactory.fromBitmap(makeMarkerIcon(player.get("playerColor").toString())));
                                            markers.put(playerName, markerOptions);
                                        }
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                    //TODO: Print query error
                                }
                            } else {
                                //TODO: Error msg...
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    updateHandler.sendEmptyMessage(0);

                    try {
                        Thread.sleep(updateLocationInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        retrieveAudio = new Runnable() {
            @Override
            public void run() {
                while (update) {

                    try {
                        ParseObject game = ParseQuery.getQuery("Game").whereEqualTo("gameID", gameID).getFirst();
                        List<ParseObject> audioFiles = game.getRelation("audioFiles").getQuery().find();

                        if (audioFiles != null) {

                            for (ParseObject audioFile : audioFiles) {
                                byte[] serverSoundData = audioFile.getBytes("sound");
                                if (!byteListContains(playedAudioFiles, serverSoundData)) {
                                    playSoundData(serverSoundData);
                                    playedAudioFiles.add(serverSoundData);
                                    break;
                                }
                            }

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        cdt = new CountDownTimer(startTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                catchBtn.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                catchBtn.setText("Catch");
                catchBtn.setEnabled(true);
            }
        };

        setUpMapIfNeeded();

        locationThread = new Thread(updateLocation);
        audioThread = new Thread(retrieveAudio);
        locationThread.start();
        audioThread.start();
    }

    public void getMyLocation() {
        if (mMap.getMyLocation() == null) { // TODO: Better fix, doesn't need to make this check. Make sure it's never null before this method
            Toast.makeText(getApplicationContext(), "Getting current location data...", Toast.LENGTH_LONG).show();
            loc.setLatitude(0);
            loc.setLongitude(0);
        } else {
            loc = mMap.getMyLocation();
            //LatLng myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * This should only be called once and when we are sure that mMap is not null.
     */
    private void setUpMap() {
        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        //Disable scrolling
        //mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    public boolean byteListContains(List<byte[]> byteArrayList, byte[] byteArray) {
        for (byte[] bae : byteArrayList) {
            if (Arrays.equals(bae, byteArray)) {
                return true;
            }
        }
        return false;
    }

    public void playSoundData(byte[] soundData) {
        try {
            File tempFile = File.createTempFile("TempRetrievedAudio", "3gp");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(soundData);
            fos.close();
            FileInputStream storedFIS = new FileInputStream(tempFile);
            //Play sound
            mPlayer.setDataSource(storedFIS.getFD());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("MediaPlayer", "prepare() failed");
        }

        while (mPlayer.isPlaying()) {
            //wait for it to finnish
        }

    }

    @Override
    public void onBackPressed() {
        update = false;
        super.onBackPressed();
        finish();
    }

    /*
     * String of hexColor format can be either #RRGGBB (normal rgb) or #AARRGGBB (with transparent alpha value)
     */
    public Bitmap makeMarkerIcon(String hexColor) {

        Bitmap bmp = Bitmap.createBitmap(markerRadius, markerRadius + 25, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(hexColor));
        paint.setStyle(Paint.Style.FILL);

        // the triangle laid under the circle
        int pointedness = 20;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(markerRadius / 2, markerRadius + 15);
        path.lineTo(markerRadius / 2 + pointedness, markerRadius - 10);
        path.lineTo(markerRadius / 2 - pointedness, markerRadius - 10);
        canvas.drawPath(path, paint);

        // circle background
        RectF rect = new RectF(0, 0, markerRadius, markerRadius);
        canvas.drawRoundRect(rect, markerRadius / 2, markerRadius / 2, paint);

        return bmp;

    }

    public void catchButton(View view) {
        HashMap<String, Object> tryCatchInfo = new HashMap<>();
        tryCatchInfo.put("gameID", gameID);
        tryCatchInfo.put("playerObjID", playerObjID);
        ParseCloud.callFunctionInBackground("tryCatch", tryCatchInfo, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    update = false;
                    dialog.show(fragmentManager, "Title");
                    Toast.makeText(getApplicationContext(), "CAUGHT!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You're not close enough!", Toast.LENGTH_LONG).show();
                    catchBtn.setEnabled(false);
                    cdt.start();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            talkBtn.setPressed(true);
            talkBtn.setText("Recording...");

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("MediaRecorder", "prepare() failed");
            }
            mRecorder.start();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //Add timer for talk button here...
            talkBtn.setPressed(false);
            talkBtn.setText("Talk");

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
            playedAudioFiles.add(data);
            //Upload audio file to server
            try {
                ParseObject game = ParseQuery.getQuery("Game").whereEqualTo("gameID", gameID).getFirst();
                ParseRelation gameAudioRelation = game.getRelation("audioFiles");
                ParseObject audio = new ParseObject("Audio");
                audio.put("sound", data);
                audio.put("timesListened", 1);
                audio.save();
                gameAudioRelation.add(audio);
                game.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        mp.reset();
        talkBtn.setEnabled(true);
    }

    @Override
    public void onDialogMessage() {
        ArrayList<String> players = new ArrayList<>();
        try {
            for (ParseObject player : ParseQuery.getQuery("Game").whereEqualTo("gameID", gameID).getFirst().getRelation("players").getQuery().find()) {
                players.add(player.get("name").toString());
            }
            Intent intent = new Intent(GameMapActivity.this, LobbyActivity.class);
            intent.putStringArrayListExtra("players", players);
            intent.putExtra("gameID", gameID);
            intent.putExtra("nickName", nickName); // May be redundant. Check for other intents.
            intent.putExtra("playerObjID", playerObjID);
            intent.putExtra("isLobbyLeader", false);
            startActivity(intent);
            finish();
        } catch (ParseException e1) {
            e1.printStackTrace();
            //super.onBackPressed();
            //finish();
            //TODO: No internet connection or game leader left which causes game object to destroy?
        }
    }

}