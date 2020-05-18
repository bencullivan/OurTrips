package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

// this is the activity where the user will be able to create, view and update trips
public class TripActivity extends AppCompatActivity {

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        // set-up title
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_activity_trip);

        // get an instance of FirebaseStorage with the default bucket
        mStorage = FirebaseStorage.getInstance();
        // get a StorageReference
        mStorageReference = mStorage.getReference();

        // set up navigation bar
        mBottomNavigation = findViewById(R.id.navigation_trip);
        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    // on click listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.summary:
                    // set summary fragment

                    return true;
                case R.id.plan:
                    // set plan fragment

                    return true;
                case R.id.media:
                    // set media fragment

                    return true;
                case R.id.trippers:
                    // set trippers fragment

                    return true;
            }
            return false;
        }
    };
}
