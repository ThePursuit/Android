package com.example.michael.ui.activities;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.ui.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameMapActivity extends FragmentActivity implements LocationListener {

    @InjectView(R.id.distanceView) TextView distanceView;
    @InjectView(R.id.catchButton) Button catchBtn;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double latitude, longitude;
    private Handler locHandler = new Handler();
    private Location preyLoc = new Location("");
    private String myObjID;
    private Location loc;
    private boolean update = true;//Make it true elsewhere...
    private boolean isPrey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);
        setUpMapIfNeeded();
        ButterKnife.inject(this);
        myObjID = getIntent().getStringExtra("playerObjID");
        isPrey = getIntent().getBooleanExtra("isPrey", false);
        if(isPrey){
            catchBtn.setEnabled(false);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        setUpMapIfNeeded();
        //BusProvider.getBus().register(this);
    }

    /*
    @Override
    public void onPause(){
        super.onPause();
        BusProvider.getBus().unregister(this);
    }
    */

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

    /*
    @Subscribe
    public void updateGame(Game game){
        ArrayList<ParseGeoPoint> locations = new ArrayList<>();
        try {
            for(ParseObject player : game.getPlayers().getQuery().find()){
                ParseGeoPoint loc = (ParseGeoPoint) player.get("location");
                mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(player.get("playerID").toString()).snippet("Consider yourself located"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    */

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        /*

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if(myLocation != null) {

            // Get latitude of the current location
            latitude = myLocation.getLatitude();

            // Get longitude of the current location
            longitude = myLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(57.6881964, 11.97916389)).title("Kappa!").snippet("WOLOLOLO"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(57.68847167, 11.97744727)).title("Keepo?").snippet("askldaskld"));
        }

        */

        locHandler.postDelayed(new Runnable() {
            @Override
            public void run(){
                if(update) {
                    HashMap<String, Object> updateInfo = new HashMap<>();
                    loc = getLocation();
                    updateInfo.put("gameID", getIntent().getStringExtra("gameID"));
                    updateInfo.put("playerObjID", getIntent().getStringExtra("playerObjID"));
                    updateInfo.put("latitude", loc.getLatitude());
                    updateInfo.put("longitude", loc.getLongitude());

                    ParseCloud.callFunctionInBackground("updateGame", updateInfo, new FunctionCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject game, ParseException e) {
                            mMap.clear();
                            try {
                                for (ParseObject player : game.getRelation("players").getQuery().find()) {
                                    //if(!player.get("playerID").toString().equals(getIntent().getStringExtra("playerID"))) {//Add marker to everyone but yourself
                                    ParseGeoPoint geo = (ParseGeoPoint) player.get("location");
                                    if(player.getBoolean("isPrey")){
                                        preyLoc.setLatitude(geo.getLatitude());
                                        preyLoc.setLongitude(geo.getLongitude());
                                    } else{
                                        LatLng latLng = new LatLng(geo.getLatitude(), geo.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(player.get("name").toString()).snippet("Consider yourself located"));
                                    }
                                    // Show the current location in Google Map
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    //TODO: Update marker instead of clear and add again...
                                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                                    //locations.put(player.get("playerID").toString(), geo);
                                    //}
                                }
                                if(isPrey){
                                    distanceView.setText("You're the Prey, Hide!");//Create separate methods to call when you're prey and so on...
                                } else{
                                    distanceView.setText("Prey: " + loc.distanceTo(preyLoc) + "m");
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                                //TODO: Print query error
                            }
                        }
                    });
                /*
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
                    locHandler.postDelayed(this, 1000);
                }
                */
                    locHandler.postDelayed(this, 2000);
                }
            }

        }, 2000);

    }

    @Override
    public void onBackPressed(){
        update = false;
        super.onBackPressed();
    }

    public void catchButton(View view){
        HashMap<String, Object> tryCatchInfo = new HashMap<>();
        String gameID = getIntent().getStringExtra("gameID").toString();
        String playerObjID = getIntent().getStringExtra("playerObjID");
        tryCatchInfo.put("gameID", gameID);
        tryCatchInfo.put("playerObjID", playerObjID);
        ParseCloud.callFunctionInBackground("tryCatch", tryCatchInfo, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject game, ParseException e) {
                if(e == null){
                    Toast.makeText(getApplicationContext(), "CAUGHT!", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Location getLocation() {
        LocationManager locationManager;
        boolean isNetworkEnabled;
        boolean isGPSEnabled;
        //boolean canGetLocation;
        Location location = null;

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                1, this);
                        //Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                //canGetLocation = true;
                else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            1, this);
                    //Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void talkButton(View view){//Test method for getting and displaying current location for now

        HashMap<String, Object> updateInfo = new HashMap<>();
        String gameID = getIntent().getStringExtra("gameID");
        String nickName = getIntent().getStringExtra("nickName");

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                1, this);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        //Location loc = getLocation();

        if(myLocation != null) {
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            mMap.addMarker(new MarkerOptions().position(latLng).title("MY POSITION!").snippet("WOLOLOLO"));
        } else{
            //TODO: Notify failure of getting users current position
            Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_LONG).show();
        }

        //mMap.clear();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
