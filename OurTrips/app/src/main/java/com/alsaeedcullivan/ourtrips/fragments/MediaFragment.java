package com.alsaeedcullivan.ourtrips.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.GalleryActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.comparators.PicComparator;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This fragment uses the Android ImagePicker open source library
 * Android ImagePicker is licensed under the Apache License, Version 2.0
 * Android ImagePicker can be found on github at https://github.com/Dhaval2404/ImagePicker
 * The library has been in no way modified, we merely implement it in this fragment for photo picking
 */
public class MediaFragment extends Fragment {

    private static final int VID_REQUEST = 101;

    // widgets
    private ImageButton mPhotoGallery;
    private Button mAddPhoto;
    private ProgressBar mSpinner;
    private TextView mLoading, mPhotoText;
    private ArrayList<Pic> mPics;

    public MediaFragment() {
        // Required empty public constructor
    }

    public static MediaFragment newInstance() {
        return new MediaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get widget references, add on click listeners and set initial visibility
        mPhotoGallery = view.findViewById(R.id.go_to_gallery);
        mPhotoGallery.setOnClickListener(galleryListener());
        mAddPhoto = view.findViewById(R.id.add_photo);
        mAddPhoto.setOnClickListener(photoListener());
        mPhotoText = view.findViewById(R.id.photo_gallery_text);
        mSpinner = view.findViewById(R.id.gallery_spinner);
        mLoading = view.findViewById(R.id.gallery_loading);
        mSpinner.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            // get the uri
            File file = ImagePicker.Companion.getFile(data);
            if (file != null) {
                Uri uri = Uri.fromFile(file);
                addPic(uri);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        hideSpinner();
    }

    /**
     * selectPic()
     * allows user to select a pic from the gallery or take a new picture
     */
    private void selectPic() {
        if (getContext() == null) return;
        // start picker to get the image and then use the result
        ImagePicker.Companion.with(this).start();
    }

    /**
     * addPic()
     * adds this picture to the storage bucket and saves its path in the db
     * @param uri the uri of the file where the photo is stored
     */
    private void addPic(Uri uri) {
        if (getActivity() == null) return;
        try {
            mLoading.setText(getString(R.string.adding_to_the_gallery));
            showSpinner();
            final long timeStamp = new Date().getTime();
            final String id = ((TripActivity) getActivity()).getTripId();
            // establish the path where this picture will be stored in the bucket
            final String path = Const.TRIP_PIC_PATH + "/" + id +
                    "/" + Const.TRIP_PHOTO_KEY + timeStamp + Const.PIC_JPG;
            // open an input stream for the uri
            final InputStream is = getActivity().getContentResolver().openInputStream(uri);
            //db operations in background
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // add this photo to the storage bucket
                    UploadTask storeTask = AccessBucket.uploadPicture(path, is);
                    // add this photo to the database and storage
                    Task<DocumentReference> docTask = AccessDB.addTripPhoto(id, path, timeStamp)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    hideSpinner();
                                }
                            });
                    // when both tasks are complete, hide the progress bar
                    Tasks.whenAll(storeTask, docTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideSpinner();
                        }
                    });
                }
            }).start();
        } catch (IOException e) {
            hideSpinner();
            Log.d(Const.TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * showSpinner()
     * displays the progress bar
     */
    private void showSpinner() {
        mPhotoGallery.setVisibility(View.GONE);
        mAddPhoto.setVisibility(View.GONE);
        mPhotoText.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * hideSpinner()
     * hides the progress bar
     */
    private void hideSpinner() {
        mSpinner.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mPhotoGallery.setVisibility(View.VISIBLE);
        mAddPhoto.setVisibility(View.VISIBLE);
        mPhotoText.setVisibility(View.VISIBLE);
    }

    /**
     * goToGallery()
     * sends the user to gallery activity, this is called when the pictures are done being
     * sorted on the Async Task background thread
     */
    private void goToGallery() {
        if (getActivity() == null) return;
        // send the user to gallery activity
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        intent.putExtra(Const.GALLERY_TAG, mPics);
        intent.putExtra(Const.TRIP_ID_TAG, ((TripActivity)getActivity()).getTripId());
        startActivity(intent);
    }

    // on click listener for the photo gallery button
    private View.OnClickListener galleryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) return;
                // get the trip id from the parent activity
                final String tripId = ((TripActivity)getActivity()).getTripId();
                if (tripId == null) return;
                mLoading.setText(R.string.loading_photos);
                showSpinner();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // load the photo paths associated with this trip
                        AccessDB.getTripPhotos(tripId).addOnCompleteListener(new OnCompleteListener<List<Pic>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Pic>> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null && task.getResult().size() > 0) {
                                        mPics = (ArrayList<Pic>) task.getResult();
                                        Log.d(Const.TAG, "onComplete: " + mPics.get(0).getPicPath());
                                        new PicSortTask().execute();
                                    } else {
                                        Toast t = Toast.makeText(getActivity(), "There are no photos " +
                                                "in the photo gallery.", Toast.LENGTH_SHORT);
                                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        t.show();
                                        hideSpinner();
                                    }
                                } else {
                                    Toast t = Toast.makeText(getActivity(), "The photo gallery could " +
                                            "not be loaded.", Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    t.show();
                                    hideSpinner();
                                }
                            }
                        });
                    }
                }).start();
            }
        };
    }

    // on click listener for the add photo button
    private View.OnClickListener photoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPic();
            }
        };
    }


    private class PicSortTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mPics != null && mPics.size() > 0) mPics.sort(new PicComparator());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            goToGallery();
        }
    }
}
