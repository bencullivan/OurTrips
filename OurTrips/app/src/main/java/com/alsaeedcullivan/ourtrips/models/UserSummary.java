package com.alsaeedcullivan.ourtrips.models;

/**
 * Model to contain the summary data associated with a user
 */
public class UserSummary {

    // data
    private String name = "";

    /*
    possibly
    private Bitmap profilePic;
     */

    // getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
