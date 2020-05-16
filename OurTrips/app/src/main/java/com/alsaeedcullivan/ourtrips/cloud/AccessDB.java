package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alsaeedcullivan.ourtrips.models.User;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Class to handle interactions with the database
 */
public class AccessDB {

    // reference to the database
    private FirebaseFirestore mFirebase;
    private FirebaseAuth mAuth;

    /**
     * public constructor
     */
    public AccessDB() {
        mFirebase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * saveNewUser()
     * saves a new user to the database
     */
    public Task<Void> addNewUser(User user) {
        String id = user.getUserId();

        // create a map to store the data to set
        Map<String, Object> data = new HashMap<>();

        // add the user data to the map
        data.put(Const.USER_NAME_KEY, user.getName());
        data.put(Const.USER_AGE_KEY, user.getAge());
        data.put(Const.USER_GENDER_KEY, user.getGender());
        data.put(Const.USER_AFFILIATION_KEY, user.getAffiliation());

        Log.d(Const.TAG, "saveNewUser: ");

        Task<Void> addNewUser = mFirebase.collection(Const.USERS_COLLECTION)
                .document(id).set(data);
        addNewUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(Const.TAG, "onSuccess: user added");
            }
        });
        addNewUser.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Const.TAG, "onFailure: " + e);
                Log.d(Const.TAG, "onFailure: user not added");
            }
        });
        return addNewUser;
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
    public Task<DocumentReference> addUserFriend(String friendId) {
//        if (mAuth.getCurrentUser() == null) return;
//        String id = mAuth.getCurrentUser().getUid();

        String id = "test_user_id";

        Map<String, Object> data = new HashMap<>();
        data.put(Const.FRIEND_ID_KEY, friendId);

        Task<DocumentReference> addFriend = mFirebase.collection(Const.USERS_COLLECTION)
                .document(id).collection(Const.USER_FRIENDS_COLLECTION).add(data);

        addFriend.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(Const.TAG, "onSuccess: user friend added");
            }
        });
        addFriend.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Const.TAG, "onFailure: " + e);
                Log.d(Const.TAG, "onFailure: user friend not added");
            }
        });

        return addFriend;
    }

    /**
     * updateUserTrips()
     * updates the trips that the user is associated with
     */
    public Task<DocumentReference> addUserTrip(String tripId) {
//        if (mAuth.getCurrentUser() == null) return;
//        String id = mAuth.getCurrentUser().getUid();

        String id = "test_user_id";

        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_ID_KEY, tripId);

        Task<DocumentReference> addTrip = mFirebase.collection(Const.USERS_COLLECTION)
                .document(id).collection(Const.USER_TRIPS_COLLECTION).add(data);

        addTrip.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(Const.TAG, "onSuccess: user trip added");
            }
        });
        addTrip.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Const.TAG, "onFailure: " + e);
                Log.d(Const.TAG, "onFailure: user trip not added");
            }
        });

        return addTrip;
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
