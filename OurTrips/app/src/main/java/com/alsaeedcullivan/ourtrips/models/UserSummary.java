package com.alsaeedcullivan.ourtrips.models;

/**
 * Model to contain the summary data associated with a user
 */
public class UserSummary {

    // data
    private String name = "";
    private String email = "";
    private String userId = "";

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
