package com.alsaeedcullivan.ourtrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RequestTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_trip);

        // set up the back button and the
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Arrange a trip!");


    }


}
