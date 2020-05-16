package com.alsaeedcullivan.ourtrips.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Model to contain all the data associated with a trip
 */
public class Trip {

    // Trip data
    private String tripId = "";
    private String title = "";
    private ArrayList<String> usersList = new ArrayList<>();
    private ArrayList<String> commentsList = new ArrayList<>();
    private ArrayList<Bitmap> photoAlbum = new ArrayList<>();

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<String> commentsList) {
        this.commentsList = commentsList;
    }

    public ArrayList<Bitmap> getPhotoAlbum() {
        return photoAlbum;
    }

    public void setPhotoAlbum(ArrayList<Bitmap> photoAlbum) {
        this.photoAlbum = photoAlbum;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public ArrayList<String> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<String> usersList) {
        this.usersList = usersList;
    }
}
