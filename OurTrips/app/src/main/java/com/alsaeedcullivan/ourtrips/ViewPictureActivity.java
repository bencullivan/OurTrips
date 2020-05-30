package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.glide.GlideApp;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewPictureActivity extends AppCompatActivity {

    private Pic mPhoto;
    private String mTripId;
    private ImageView mImage;
    private TextView mLoading;
    private ProgressBar mSpinner;
    private int mPosition = -1;
    private ArrayList<Pic> mPics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        // set the back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(Const.TRIP_ID_TAG) != null &&
                intent.getParcelableExtra(Const.PIC_TAG) != null &&
                intent.getIntExtra(Const.POSITION_TAG, -1) != -1
                && intent.getParcelableArrayListExtra(Const.GALLERY_TAG) != null) {
            mPhoto = intent.getParcelableExtra(Const.PIC_TAG);
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);
            mPosition = intent.getIntExtra(Const.POSITION_TAG, -1);
            mPics = intent.getParcelableArrayListExtra(Const.GALLERY_TAG);
        } else finish();

        // get a reference to the widgets and set initial visibility
        mImage = findViewById(R.id.view_image_picture);
        mLoading = findViewById(R.id.view_loading);
        mSpinner = findViewById(R.id.view_spinner);
        mLoading.setVisibility(View.GONE);
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);

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
                Intent intent = new Intent(ViewPictureActivity.this, GalleryActivity.class);
                intent.putExtra(Const.GALLERY_TAG, mPics);
                intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                startActivity(intent);
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
        if (mPhoto == null || mTripId == null || mPhoto.getDocId() == null || mPosition == -1 ||
                mPics == null || mPhoto.getPicPath() == null) return;
        Log.d(Const.TAG, "deletePhoto: " + mPhoto.getDocId());
        Log.d(Const.TAG, "deletePhoto: " + mPhoto.getPicPath());
        // show the progress bar
        showSpinner();
        // remove this photo from the list
        mPics.remove(mPosition);
        // delete this picture
        new DeletePicTask().execute();
    }

    /**
     * showSpinner()
     * shows the progress bar
     */
    private void showSpinner() {
        mImage.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * DeletePicTask
     * deletes a picture from the storage bucket and deletes its path from the db
     */
    private class DeletePicTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null || mPhoto == null || mPics == null) return null;

            // remove the photo from the db and storage
            Task<Void> dbTask = AccessDB.deleteTripPhoto(mTripId, mPhoto.getDocId());
            Task<Void> storeTask = AccessBucket.deleteFromStorage(mPhoto.getPicPath());
            // when the photo is deleted, finish the activity
            Tasks.whenAll(dbTask, storeTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(ViewPictureActivity.this, GalleryActivity.class);
                    intent.putExtra(Const.GALLERY_TAG, mPics);
                    intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                    startActivity(intent);
                    Log.d(Const.TAG, "onComplete: finish activity deleted pic");
                    finish();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(Const.TAG, "onPostExecute: delete pic task done");
        }
    }
}
