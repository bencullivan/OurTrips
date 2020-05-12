package com.alsaeedcullivan.ourtrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

// this is the activity where the user will be able to create, view and update trips
public class UpdateTripActivity extends AppCompatActivity {

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        // get an instance of FirebaseStorage with the default bucket
        mStorage = FirebaseStorage.getInstance();
        // get a StorageReference
        mStorageReference = mStorage.getReference();


    }
}
