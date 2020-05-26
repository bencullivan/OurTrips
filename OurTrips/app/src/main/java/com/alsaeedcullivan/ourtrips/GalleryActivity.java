package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.alsaeedcullivan.ourtrips.adapters.GalleryAdapter;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private ArrayList<Pic> mPictures;
    private String mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // set title and back button
        setTitle("Gallery");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the list of pictures
        Intent intent = getIntent();
        mPictures = intent.getParcelableArrayListExtra(Const.GALLERY_TAG);
        mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);

        // set up the recyclerview
        RecyclerView rec = findViewById(R.id.recycle_gallery);
        GridLayoutManager grid = new GridLayoutManager(this, 2);
        rec.setLayoutManager(grid);
        GalleryAdapter g = new GalleryAdapter(this, mPictures);
        rec.setAdapter(g);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // getters

    public String getTripId() {
        return mTripId;
    }

    public ArrayList<Pic> getPics() {
        return mPictures;
    }
}
