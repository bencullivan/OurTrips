package com.alsaeedcullivan.ourtrips.models;

import java.util.ArrayList;

// this is a trip object that will contain all the data about the trip
public class Trip {

    // member variables
    private String mTitle = "";
    private ArrayList<String> mCommentsList = new ArrayList<>();
    ArrayList<String> mPhotoPaths = new ArrayList<>();


    // getters and setters

    public void setTitle(String title) {
        mTitle = title;
    }
    public String getTitle() {
        return mTitle;
    }
    public void addComment(String comment) {
        mCommentsList.add(comment);
    }
    public ArrayList<String> getComments() {
        return mCommentsList;
    }
    public ArrayList<String> getPhotoPaths() {
        return mPhotoPaths;
    }
}
