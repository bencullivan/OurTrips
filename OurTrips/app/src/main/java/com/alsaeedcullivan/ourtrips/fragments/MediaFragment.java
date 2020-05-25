package com.alsaeedcullivan.ourtrips.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.alsaeedcullivan.ourtrips.GalleryActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.models.Pic;
import com.alsaeedcullivan.ourtrips.utils.Const;

import java.util.ArrayList;


public class MediaFragment extends Fragment implements View.OnClickListener {

    // widgets
    private ImageButton mPhotoGallery, mVideoGallery;
    private Button mAddPhoto, mAddVideo;
    private ArrayList<Pic> mPics = new ArrayList<>();

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
        mPhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send the user to gallery activity
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra(Const.GALLERY_TAG, mPics);
                startActivity(intent);
            }
        });
        mVideoGallery = view.findViewById(R.id.go_to_videos);
        mAddPhoto = view.findViewById(R.id.add_photo);
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAddVideo = view.findViewById(R.id.add_video);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * goToGallery()
     * method to take the user to the photo gallery for this trip
     */
    public void goToGallery(View v) {

    }

    /**
     * addPhoto()
     * method to add a photo for this trip
     */
    public void addPhoto() {

    }
}
