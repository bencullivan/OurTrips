package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.models.Place;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 0;
    private static final int ZOOM_LEVEL = 14;

    private GoogleMap mMap;
    private ArrayList<Place> mPlaces;
    private String mTripId, mLocationName;
    private LocationManager mLocationManager;
    private String mProvider;
    private Button mRemove, mAdd, mCancel, mRemoveText, mAddText;
    private HashMap<LatLng, Place> mPlaceMap = new HashMap<>();
    private LatLng mSelectedLatLng;
    private Place mHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) finish();
        else mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent != null && intent.getParcelableArrayListExtra(Const.PLACE_LIST_TAG) != null &&
                intent.getStringExtra(Const.TRIP_ID_TAG) != null) {
            mPlaces = intent.getParcelableArrayListExtra(Const.PLACE_LIST_TAG);
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);
        } else finish();

        // get widget references
        mRemove = findViewById(R.id.remove_location);
        mAdd = findViewById(R.id.add_location);
        mCancel = findViewById(R.id.cancel_map);
        mRemoveText = findViewById(R.id.remove_text);
        mAddText = findViewById(R.id.add_text);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(Const.TAG, "onMapReady: ");

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // if the user has not granted permission, check permission
        if (!permissionGranted()) {
            checkPermission();
        }
//        // if the user has clicked don't ask again, offer to take them to settings
//        if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ||
//                !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//            CustomDialogFragment.newInstance(CustomDialogFragment.LOCATION_SETTINGS_ID)
//                    .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
//                    .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
//        }
        // start the map with the most recent location on the trip or the user's current location
        else startMapWithLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED)  {
            startMapWithLocation();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // permission is required
            CustomDialogFragment.newInstance(CustomDialogFragment.LOCATION_REQUIRED_ID)
                    .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
        } else {
            // offer to go to settings
            CustomDialogFragment.newInstance(CustomDialogFragment.LOCATION_SETTINGS_ID)
                    .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
        }
    }

    /**
     * permissionGranted()
     * returns whether location permissions have been granted
     */
    public boolean permissionGranted() {
        return checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * checkPermission()
     * checks the permissions for location access
     */
    public void checkPermission() {
        // if the locations have not been granted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // request the needed permissions
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    /**
     * startMapWithCurrent()
     * starts the map with the user's current location
     */
    private void startMapWithLocation() {
        // add all the places to the map
        for (Place place : mPlaces) {
            mPlaceMap.put(place.getLocation(), place);
            mMap.addMarker(new MarkerOptions().position(place.getLocation())
                    .title(place.getPlaceName()));
        }

        // start the location provider
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setPowerRequirement(Criteria.POWER_LOW);
        c.setCostAllowed(true);
        c.setSpeedRequired(false);
        c.setBearingRequired(false);
        mProvider = mLocationManager.getBestProvider(c, true);

        if (mPlaces.size() > 0) {
            // zoom in on the last known location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mPlaces.get(mPlaces.size() - 1)
                    .getLocation(), ZOOM_LEVEL));
        } else {
            checkPermission();
            // zoom in on the current location
            Location current = mLocationManager.getLastKnownLocation(mProvider);
            if (current != null) {
                LatLng coordinates = new LatLng(current.getLatitude(), current.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, ZOOM_LEVEL));
            }
        }
        // set on click listeners
        mRemove.setOnClickListener(removeListener());
        mCancel.setOnClickListener(cancelListener());
        mAdd.setOnClickListener(addListener());
    }

    /**
     * initiateSelect()
     * allows the user to select a location to drop a marker
     * adds this location to the database
     */
    public void initiateSelect() {
        if (mLocationName == null || mLocationName.replaceAll("\\s","").equals("")) {
            Toast t = Toast.makeText(this, "You must give the location a name.", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
            cancelAction();
            return;
        }
        Log.d(Const.TAG, "initiateAdd: " + mLocationName);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // drop a marker at this location
                mMap.addMarker(new MarkerOptions().position(latLng).title(mLocationName));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
                cancelAction();

                mSelectedLatLng = latLng;

                // save this location to the db
                if (mTripId == null) return;
                new AddLocationTask().execute();
            }
        });
    }

    /**
     * useCurrent()
     * adds a marker at the user's current location
     * adds this location to the database
     */
    public void useCurrent() {
        if (mLocationName == null || mLocationName.replaceAll("\\s","").equals("")) {
            Toast t = Toast.makeText(this, "You must give the location a name.", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
            cancelAction();
            return;
        }
        Log.d(Const.TAG, "initiateAdd: " + mLocationName);

        // drop the marker at the current location
        checkPermission();
        Location here = mLocationManager.getLastKnownLocation(mProvider);
        if (here == null) return;
        mSelectedLatLng = new LatLng(here.getLatitude(), here.getLongitude());
        mMap.addMarker(new MarkerOptions().position(mSelectedLatLng).title(mLocationName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatLng, ZOOM_LEVEL));
        cancelAction();

        // save this location to the db
        if (mTripId == null) return;
        new AddLocationTask().execute();
    }

    /**
     * cancelAction()
     * takes the map out of edit mode
     */
    private void cancelAction() {
        // remove listeners
        mMap.setOnInfoWindowLongClickListener(null);
        mMap.setOnMapLongClickListener(null);

        mAddText.setVisibility(View.GONE);
        mRemoveText.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
        mRemove.setVisibility(View.VISIBLE);
        mAdd.setVisibility(View.VISIBLE);
    }

    // takes the user to device settings for this app
    public void goToSettings() {
        getApplicationContext().startActivity(new Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", this.getPackageName(), null)));
    }

    // on click listeners

    private View.OnClickListener removeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // adjust visibility
                mRemove.setVisibility(View.GONE);
                mAdd.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mRemoveText.setVisibility(View.VISIBLE);

                // set map click listener
                mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                    @Override
                    public void onInfoWindowLongClick(Marker marker) {
                        // remove this marker from the map
                        marker.remove();
                        cancelAction();

                        // get the place corresponding to this location
                        mHere = mPlaceMap.get(marker.getPosition());
                        if (mHere == null || mTripId == null || mHere.getDocId() == null) return;

                        //delete this place from the db
                        new DeleteLocationTask().execute();
                    }
                });
            }
        };
    }
    private View.OnClickListener addListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // adjust visibility
                mRemove.setVisibility(View.GONE);
                mAdd.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mAddText.setVisibility(View.VISIBLE);

                // show the dialog for name input
                CustomDialogFragment.newInstance(CustomDialogFragment.ADD_LOCATION_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }
    private View.OnClickListener cancelListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAction();
            }
        };
    }

    // setters

    public void setLocationName(String name) {
        mLocationName = name;
    }


    // ASYNC TASKS

    /**
     * AddLocationTask
     * adds a location to the db
     */
    private class AddLocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null || mLocationName == null || mSelectedLatLng == null) return null;

            // add this location to the db
            AccessDB.addTripLocation(mTripId, mSelectedLatLng, mLocationName, new Date().getTime());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // set the name to null
            mLocationName = null;
        }
    }

    /**
     * DeleteLocationTask
     * deletes a location from the db
     */
    private class DeleteLocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mHere == null || mTripId == null || mHere.getDocId() == null) return null;

            // remove this place from the database
            AccessDB.deleteTripLocation(mTripId, mHere.getDocId());

            return null;
        }
    }
}
