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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GameMapActivity extends FragmentActivity implements LocationListener {

    @InjectView(R.id.distanceView) TextView distanceView;
    @InjectView(R.id.catchButton) Button catchBtn;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double latitude, longitude;
    private Handler locHandler;
    private Location preyLoc;
    private String myObjID;
    private Location loc;
    private boolean update;
    private boolean isPrey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);
        ButterKnife.inject(this);
        locHandler = new Handler();
        preyLoc = new Location("");
        update = true; //Make it true elsewhere...
        myObjID = getIntent().getStringExtra("playerObjID");
        isPrey = getIntent().getBooleanExtra("isPrey", false);
        if(isPrey){
            catchBtn.setEnabled(false);
        }
        setUpMapIfNeeded();
    }

    @Override
    public void onResume(){
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

        /**
         * Thread that updates player's current location to the server
         * and gets every players location in the game session every 2 seconds
         */
        locHandler.postDelayed(new Runnable() {
            @Override
            public void run(){
                if(update) {
                    HashMap<String, Object> updateInfo = new HashMap<>();
                    if(mMap.getMyLocation() == null){ // TODO: Better fix, doesn't need to make this check. Make sure it's never null before this method
                        Toast.makeText(getApplicationContext(), "Getting current location data...", Toast.LENGTH_LONG).show();
                        loc = getLocation();
                    } else{
                        loc = mMap.getMyLocation();
                    }
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
                                    ParseGeoPoint geo = (ParseGeoPoint) player.get("location");
                                    if(player.getBoolean("isPrey")){
                                        preyLoc.setLatitude(geo.getLatitude());
                                        preyLoc.setLongitude(geo.getLongitude());
                                    } else{
                                        LatLng latLng = new LatLng(geo.getLatitude(), geo.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(latLng).title(player.get("name").toString()).snippet("Consider yourself located"));
                                        //TODO: Update marker instead of clear and add again...
                                    }
                                }
                                if(isPrey){
                                    distanceView.setText("You're the Prey, Hide!");//Create separate methods to call when you're prey and so on...
                                } else{
                                    distanceView.setText("Prey: " + loc.distanceTo(preyLoc) + "m"); //Convert to int...
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                                //TODO: Print query error
                            }
                        }
                    });
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
        String gameID = getIntent().getStringExtra("gameID");
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

    public void talkButton(View view){
        // Test method to add marker at player's location for now
        /*
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // This invokes onLocationChanged method for given parameters
        locationManager.requestLocationUpdates(provider, 1000, 1, this);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if(myLocation != null) {
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            mMap.addMarker(new MarkerOptions().position(latLng).title("MY POSITION!").snippet("WOLOLOLO"));
        } else{
            //TODO: Notify failure of getting users current position
        }
        */

        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.addMarker(new MarkerOptions().position(latLng).title("MY POSITION!").snippet("WOLOLOLO"));
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
                Toast.makeText(getApplicationContext(), "NO LOCATION PROVIDER ENABLED!", Toast.LENGTH_LONG).show();
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

        if(location == null){
            location = new Location("");
            location.setLatitude(0);
            location.setLongitude(0);
            return location;
        } else{
            return location;
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        /* Disable for now
        loc.setLatitude(location.getLatitude());
        loc.setLongitude(location.getLongitude());
        */
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
