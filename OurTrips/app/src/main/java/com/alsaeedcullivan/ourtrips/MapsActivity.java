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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

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

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 0;
    private static final int ZOOM_LEVEL = 17;

    private GoogleMap mMap;
    private ArrayList<Place> mPlaces;
    private String mTripId;
    private LocationManager mLocationManager;
    private String mProvider;

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
        for (Place place : mPlaces) mMap.addMarker(new MarkerOptions().position(place.getLocation())
                .title(place.getPlaceName()));

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
    }

    // takes the user to device settings for this app
    public void goToSettings() {
        getApplicationContext().startActivity(new Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", this.getPackageName(), null)));
    }
}
