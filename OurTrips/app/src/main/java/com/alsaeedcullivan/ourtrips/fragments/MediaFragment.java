package com.alsaeedcullivan.ourtrips.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.GalleryActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This fragment uses the Android ImagePicker open source library
 * Android ImagePicker is licensed under the Apache License, Version 2.0
 * Android ImagePicker can be found on github at https://github.com/Dhaval2404/ImagePicker
 * The library has been in no way modified, we merely implement it in this fragment for photo cropping
 */
public class MediaFragment extends Fragment implements View.OnClickListener {

    // widgets
    private ImageButton mPhotoGallery, mVideoGallery;
    private Button mAddPhoto, mAddVideo;

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

        // get widget references and add on click listeners
        mPhotoGallery = view.findViewById(R.id.go_to_gallery);
        mPhotoGallery.setOnClickListener(galleryListener());
        mVideoGallery = view.findViewById(R.id.go_to_videos);
        mAddPhoto = view.findViewById(R.id.add_photo);
        mAddPhoto.setOnClickListener(photoListener());
        mAddVideo = view.findViewById(R.id.add_video);
        
    }

    @Override
    public void onClick(View v) {

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

    /**
     * selectPic()
     * allows user to select a pic from the gallery or take a new picture
     */
    private void selectPic() {
        if (getContext() == null) return;
        // start picker to get the image for cropping and then use the result
        ImagePicker.Companion.with(this).start();
    }

    private void addPic(Uri uri) {
        if (getActivity() != null)
        try {

            long timeStamp = new Date().getTime();
            String id = ((TripActivity) getActivity()).getTripId();
            // establish the path where this picture will be stored in the bucket
            String path = Const.TRIP_PIC_PATH + "/" + id +
                    "/" + Const.TRIP_PHOTO_KEY + timeStamp + Const.PIC_JPG;
            // open an input stream for the uri
            InputStream is = getActivity().getContentResolver().openInputStream(uri);
            // add this photo to the database and storage
            AccessDB.addTripPhoto(id, path, is, timeStamp);
        } catch (IOException e) {
            Log.d(Const.TAG, Log.getStackTraceString(e));
        }
    }

    // on click listener for the photo gallery button
    private View.OnClickListener galleryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) return;
                // get the trip id from the parent activity
                String tripId = ((TripActivity)getActivity()).getTripId();
                if (tripId == null) return;
                // load the photo paths associated with this trip
                AccessDB.getTripPhotos(tripId).addOnCompleteListener(new OnCompleteListener<List<Pic>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Pic>> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                ArrayList<Pic> pics = (ArrayList<Pic>) task.getResult();
                                Log.d(Const.TAG, "onComplete: " + pics.get(0).getPicPath());
                                // send the user to gallery activity
                                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                                intent.putExtra(Const.GALLERY_TAG, pics);
                                startActivity(intent);
                            } else {
                                Toast t = Toast.makeText(getActivity(), "There are no photos " +
                                        "in the photo gallery.", Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                t.show();
                            }
                        } else {
                            Toast t = Toast.makeText(getActivity(), "The photo gallery could " +
                                    "not be loaded.", Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                        }
                    }
                });
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
}
