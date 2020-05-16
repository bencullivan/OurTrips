package com.alsaeedcullivan.ourtrips.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Model to contain the data associated with a user
 */
public class User {

    // User data
    private String userId = "";
    private Bitmap profilePic;
    private String name = "";
    private String email = "";
    private String gender = "";
    private String affiliation = "";
    private String age = "";
    private ArrayList<String> friendsList = new ArrayList<>();
    private ArrayList<String> tripsList = new ArrayList<>();

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    public ArrayList<String> getTripsList() {
        return tripsList;
    }

    public void setTripsList(ArrayList<String> tripsList) {
        this.tripsList = tripsList;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }
}
