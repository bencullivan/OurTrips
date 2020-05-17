package com.alsaeedcullivan.ourtrips.cloud;

import com.alsaeedcullivan.ourtrips.utils.Const;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to handle interactions with the database
 */
public class AccessDB {

    /**
     * addNewUser()
     * saves a new user to the database
     * @param id the id of the user being added
     * @param data the user info that is being added
     */
    public static Task<Void> addNewUser(String id, Map<String, Object> data) {
        // add a new document to the users collection
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(id)
                .set(data);
    }

    /**
     * updateUserProfile()
     * updates the profile of a user
     */
    public static Task<Void> updateUserProfile(String userId, Map<String, Object> data) {
        // update the user's profile info
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .update(data);
    }

    /**
     * addUserFriend()
     * adds a new friend to the current user's friends sub-collection
     */
    public static Task<Void> addUserFriend(String userId, String friendId) {
        // map to contain the friend id
        Map<String, Object> data = new HashMap<>();
        data.put(Const.FRIEND_ID_KEY, friendId);

        // add the friend to the friends sub-collection
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .document(friendId)
                .set(data);
    }

    /**
     * addUserTrip()
     * adds a new trip to the current user's trips sub-collection
     */
    public static Task<Void> addUserTrip(String userId, String tripId) {
        // map to contain the document data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_ID_KEY, tripId);

        // add the trip to the user_trips sub-collection
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_TRIPS_COLLECTION)
                .document(tripId)
                .set(data);
    }

    /**
     * deleteUser()
     * deletes a user from the database
     * NOTE: ** This function only deletes the document associated with a user
     *         it does NOT delete the user_friends or user_trips sub collections
     *         it also does NOT delete this user from the friends lists of their friends.
     *         These three actions are performed by the onUserDeleted Cloud Function
     *         (see index.js) **
     */
    public static Task<Void> deleteUser(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .delete();
    }

    /**
     * addTrip()
     * adds a new trip to the database
     */
    public static Task<Void> addTrip(String tripId, Map<String, Object> data) {
//        // map to contain the trip data
//        HashMap<String, Object> data = new HashMap<>();
//
//        // add the trip data to the map
//        data.put(Const.TRIP_ID_KEY, trip.getTripId());
//        data.put(Const.TRIP_TITLE_KEY, trip.getTitle());
//        data.put(Const.TRIP_COMMENTS_LIST_KEY, trip.getCommentsList());
//        data.put(Const.TRIP_USERS_LIST_KEY, trip.getUsersList());

        // add the trip to the database
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .set(data);
    }

    public void updateTrip() {

    }

    /**
     * deleteTrip()
     * deletes a trip from the database
     */
    public void deleteTrip() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

    }

    /**
     * getFriendsList()
     * gets the friends list of a user
     */
    public static Task<List<String>> getFriendsList(String userId) {
        // get a list of the friends ids of a user
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .get()
                .continueWith(new Continuation<QuerySnapshot, List<String>>() {
                    @Override
                    public List<String> then(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() == null ||
                                task.getResult().getDocuments().size() == 0)
                            return new ArrayList<>();

                        // extract and return the ids of the documents
                        List<DocumentSnapshot> docList = task.getResult().getDocuments();
                        List<String> friendIds = new ArrayList<>();
                        for (DocumentSnapshot doc : docList) {
                            friendIds.add(doc.getId());
                        }
                        return friendIds;
                    }
                });
    }

}
