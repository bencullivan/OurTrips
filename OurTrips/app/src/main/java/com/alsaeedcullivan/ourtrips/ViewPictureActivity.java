package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.glide.GlideApp;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPictureActivity extends AppCompatActivity {

    private Pic mPhoto;
    private String mTripId;
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        // set the back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(Const.TRIP_ID_TAG) != null &&
                intent.getParcelableExtra(Const.PIC_TAG) != null) {
            mPhoto = intent.getParcelableExtra(Const.PIC_TAG);
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);
        } else finish();

        // get a reference to the image view
        mImage = findViewById(R.id.view_image_picture);

        // load the picture
        StorageReference ref = FirebaseStorage.getInstance().getReference(mPhoto.getPicPath());
        GlideApp.with(this).load(ref).into(mImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_picture_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete_photo:
                deletePhoto();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * deletePhoto()
     * deletes this photo from storage and the db
     */
    private void deletePhoto() {
        if (mPhoto == null || mTripId == null || mPhoto.getDocId() == null) return;
        Log.d(Const.TAG, "deletePhoto: " + mPhoto.getDocId());
        Log.d(Const.TAG, "deletePhoto: " + mPhoto.getPicPath());
        // remove the photo from the db and storage
        Task<Void> dbTask = AccessDB.deleteTripPhoto(mTripId, mPhoto.getDocId());
        Task<Void> storeTask = AccessBucket.deleteFromStorage(mPhoto.getPicPath());
    }
}
