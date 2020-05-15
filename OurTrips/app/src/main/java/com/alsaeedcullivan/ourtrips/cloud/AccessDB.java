package com.alsaeedcullivan.ourtrips.cloud;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class to handle interactions with the database
 */
public class AccessDB {

    // reference to the database
    private FirebaseFirestore mFirebase;

    /**
     * public constructor
     */
    public AccessDB() {
        mFirebase = FirebaseFirestore.getInstance();
    }

    /**
     * saveNewUser()
     * saves a new user to the database
     */
    public void saveNewUser() {
    }

    /**
     * updateUserProfile()
     * updates the profile of a user
     */
    public void updateUserProfile() {
    }

    /**
     * updateUserFriends()
     * updates the friends that the user is associated
     */
    public void updateUserFriends() {
    }

    /**
     * updateUserTrips()
     * updates the trips that the user is associated with
     */
    public void updateUserTrips() {
    }

    /**
     * deleteUser()
     * deletes a user from the database
     */
    public void deleteUser() {
    }

    /**
     * addTrip()
     * adds a new trip to the database
     */
    public void addTrip() {
    }

    /**
     * deleteTrip()
     * deletes a trip from the database
     */
    public void deleteTrip() {
    }

}
