package com.alsaeedcullivan.ourtrips.cloud;

import com.alsaeedcullivan.ourtrips.models.Trip;
import com.alsaeedcullivan.ourtrips.models.User;
import com.alsaeedcullivan.ourtrips.utils.Const;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;

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

        // create a map to store the document data
        Map<String, Object> data = new HashMap<>();

        // add the user data to the map
        data.put(Const.USER_ID_KEY, user.getUserId());
        data.put(Const.USER_NAME_KEY, user.getName());
        data.put(Const.USER_AGE_KEY, user.getAge());
        data.put(Const.USER_GENDER_KEY, user.getGender());
        data.put(Const.USER_AFFILIATION_KEY, user.getAffiliation());

        Log.d(Const.TAG, "saveNewUser: ");

        // add a new document to the users collection
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
     * addUserFriend()
     * adds a new friend to the current user's friends sub-collection
     */
    public Task<DocumentReference> addUserFriend(String friendId) {
//        if (mAuth.getCurrentUser() == null) return;
//        String id = mAuth.getCurrentUser().getUid();

        String id = "testUserId";

        // map to contain the document data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.FRIEND_ID_KEY, friendId);

        // add the friend to the friends sub-collection
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
     * addUserTrip()
     * adds a new trip to the current user's trips sub-collection
     */
    public Task<DocumentReference> addUserTrip(String tripId) {
//        if (mAuth.getCurrentUser() == null) return;
//        String id = mAuth.getCurrentUser().getUid();

        String id = "testUserId";

        // map to contain the document data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_ID_KEY, tripId);

        // add the trip to the trips sub-collection
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
//        if (mAuth.getCurrentUser() == null) return;
//        String id = mAuth.getCurrentUser().getUid();
        String id = "test_user_id";

        String path1 = Const.USERS_PATH+"/"+id+"/"+Const.USER_TRIPS_COLLECTION;
    }

    /**
     * addTrip()
     * adds a new trip to the database
     */
    public Task<Void> addTrip(Trip trip) {
        // map to contain the trip data
        HashMap<String, Object> data = new HashMap<>();

        // add the trip data to the map
        data.put(Const.TRIP_ID_KEY, trip.getTripId());
        data.put(Const.TRIP_TITLE_KEY, trip.getTitle());
        data.put(Const.TRIP_COMMENTS_LIST_KEY, trip.getCommentsList());
        data.put(Const.TRIP_USERS_LIST_KEY, trip.getUsersList());

        Task<Void> addTrip = mFirebase.collection(Const.TRIPS_COLLECTION)
                .document(trip.getTripId()).set(data);

        addTrip.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(Const.TAG, "onSuccess: trip added");
            }
        });
        addTrip.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Const.TAG, "onFailure: " + e);
                Log.d(Const.TAG, "onFailure: trip not added");
            }
        });

        return addTrip;
    }

    /**
     * deleteTrip()
     * deletes a trip from the database
     */
    public void deleteTrip() {
    }


    // invocations of cloud functions

    /**
     * recursiveDelete()
     * this calls the recursiveDelete cloud function (see index.js) which deletes an entire
     * collection at a given path in the database
     * this will be invoked when a user deletes their account to delete their entire collection of
     * references to trips as well as their entire collection of references to friends
     */
    public Task<String> recursiveDelete(String path) {

        // get a reference to the firebase cloud functions
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        Task<String> task = functions.getHttpsCallable(Const.FUNC_RECURSIVE_DELETE)
                .call(path)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        if (task.getResult() == null) return "";
                        return (String) task.getResult().getData();
                    }
                });

        return task;

    }

}
