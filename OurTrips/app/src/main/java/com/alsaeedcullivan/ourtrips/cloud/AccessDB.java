package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import com.alsaeedcullivan.ourtrips.utils.Const;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class of static methods to handle interactions with the FireStore database
 */
public class AccessDB {

    // USER SETTERS

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
     * @param userId the id of the user
     * @param data the profile data to be added
     */
    public static Task<Void> updateUserProfile(String userId, Map<String, Object> data) {
        // update the user's profile info
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .update(data);
    }

    /**
     * setUserDatesFromCal()
     * updates a users list of dates in the cloud FireStore database
     * @param userId the id of the user
     * @param dates a list of Date objects, these will be converted to Strings in the desired
     *              format on a background thread before they are added to the database
     */
    public static void setUserDatesFromCal(String userId, List<Date> dates) {
        // convert the list of dates to a list of strings in the desired format
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy",
                Locale.getDefault());
        List<String> sDates = new ArrayList<>();
        for (Date date : dates) {
            sDates.add(format.format(date));
        }

        Log.d(Const.TAG, "setUserDatesFromCal: " + Thread.currentThread().getId());

        // add the list of dates to the database
        FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .update(Const.DATE_LIST_KEY, sDates);
    }

    /**
     * addUserFriend()
     * adds a new friend to the current user's friends sub-collection
     * @param userId the id of the user
     * @param friendId the id of the friend
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
     * @param userId the id of the user
     * @param tripId the id of the trip
     */
    public static Task<Void> addUserTrip(String userId, String tripId, String tripTitle, String startDate) {
        // map to contain the document data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_ID_KEY, tripId);
        data.put(Const.TRIP_TITLE_KEY, tripTitle);
        data.put(Const.TRIP_START_DATE_KEY, startDate);

        // add the trip to the user_trips sub-collection
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_TRIPS_COLLECTION)
                .document(tripId)
                .set(data);
    }

    /**
     * removeUserTrip()
     * removes a trip from the trips sub-collection of a user
     * @param userId the id of the user
     * @param tripId the id of the trip
     */
    public static Task<Void> removeUserTrip(String userId, String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_TRIPS_COLLECTION)
                .document(tripId)
                .delete();
    }

    /**
     * addUserToken()
     * adds a token that will be used for FCM to a user's entry in FireStore
     * @param userId the id of the user
     * @param token the token that will be used to send them messages
     */
    public static Task<Void> addUserToken(String userId, String token) {
        // add the token to the given user's document in the db
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .update(Const.USER_TOKEN_KEY, token);
    }

    /**
     * deleteUser()
     * deletes a user from the database
     * NOTE: ** This function only deletes the document associated with a user
     *         it does NOT delete the user_friends or user_trips sub collections
     *         it also does NOT delete this user from the friends lists of their friends.
     *         Finally, it also does NOT delete a user's profile photo from the storage bucket.
     *         These four actions are performed by the onUserDeleted Cloud Function
     *         (see index.js) **
     * @param userId the id of the user
     */
    public static Task<Void> deleteUser(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .delete();
    }


    // HANDLE USER FRIEND REQUESTS

    /**
     * sendFriendRequest()
     * sends a friend request from this user to another user
     * @param userEmail the email of this user
     * @param friendEmail the email of the person they are sending the request to
     */
    public static Task<String> sendFriendRequest(String userId, String userEmail, String userName, String friendEmail) {
        final String id = userId;
        // store this user's summary data in a map
        final Map<String, Object> data = new HashMap<>();
        data.put(Const.FRIEND_ID_KEY, userId);
        data.put(Const.USER_EMAIL_KEY, userEmail);
        data.put(Const.USER_NAME_KEY, userName);

        // get the friend from the db based on the email that was passed in
        final FirebaseFirestore store = FirebaseFirestore.getInstance();
        return store.collection(Const.USERS_COLLECTION)
                .whereEqualTo(Const.USER_EMAIL_KEY, friendEmail)
                .get()
                .continueWith(new Continuation<QuerySnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot q = task.getResult();
                        if (q != null && q.size() > 0) {
                            DocumentSnapshot doc = q.getDocuments().get(0);
                            final String friendId = doc.getId();

                            // db operation on background thread
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(Const.TAG, "then: continue " + Thread.currentThread().getId());
                                    // check to see if this user has already sent them a request
                                    store.collection(Const.USERS_COLLECTION)
                                            .document(friendId)
                                            .collection(Const.USER_F_REQUESTS_COLLECTION)
                                            .document(id)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot doc = task.getResult();
                                                    // if they have not
                                                    if (doc == null || !doc.exists()) {
                                                        Log.d(Const.TAG, "access db on complete thread id: " + Thread.currentThread().getId());
                                                        // db operation on background thread
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Log.d(Const.TAG, "access db on complete thread id: " + Thread.currentThread().getId());
                                                                // add this user to the other user's friend
                                                                // requests collection
                                                                store.collection(Const.USERS_COLLECTION)
                                                                        .document(friendId)
                                                                        .collection(Const.USER_F_REQUESTS_COLLECTION)
                                                                        .document(id)
                                                                        .set(data);
                                                            }
                                                        }).start();
                                                    }
                                                }
                                            });
                                }
                            }).start();
                            return "a";
                        } else {
                            return "n";
                        }
                    }
                });
    }

    /**
     * acceptFriendRequest()
     * allows a user to accept a friend request and updates the db accordingly
     * @param userId the id of this user
     * @param userEmail the email of this user
     * @param userName the name of this user
     * @param friendId the id of the user that sent the friend request
     * @param friendEmail the email of the user that sent the request
     * @param friendName the name of the user that sent the request
     */
    public static void acceptFriendRequest(String userId, String userEmail, String userName,
                                           String friendId, String friendEmail, String friendName) {
        // add user data to a map
        Map<String, Object> thisMap = new HashMap<>();
        thisMap.put(Const.FRIEND_ID_KEY, userId);
        thisMap.put(Const.USER_EMAIL_KEY, userEmail);
        thisMap.put(Const.USER_NAME_KEY, userName);

        // add friend data to a map
        Map<String, Object> otherMap = new HashMap<>();
        otherMap.put(Const.FRIEND_ID_KEY, friendId);
        otherMap.put(Const.USER_EMAIL_KEY, friendEmail);
        otherMap.put(Const.USER_NAME_KEY, friendName);

        // get a reference to the db
        FirebaseFirestore store = FirebaseFirestore.getInstance();

        Log.d(Const.TAG, "acceptFriendRequest: access db " + Thread.currentThread().getId());

        // delete the friend request
        store.collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_F_REQUESTS_COLLECTION)
                .document(friendId)
                .delete();

        // add each user to the friends sub-collection of the other user
        store.collection(Const.USERS_COLLECTION)
                .document(friendId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .document(userId)
                .set(thisMap);
        store.collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .document(friendId)
                .set(otherMap);
    }

    /**
     * deleteRequest()
     * deletes a friend request from the db
     */
    public static void deleteRequest(String userId, String friendId) {
        Log.d(Const.TAG, "deleteRequest: " + Thread.currentThread().getId());
        FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_F_REQUESTS_COLLECTION)
                .document(friendId)
                .delete();
    }


    // USER GETTERS TO RETRIEVE DATA FROM THE DB

    /**
     * loadUserProfile()
     * loads the profile info of a user
     * @param userId the id of the user
     */
    public static Task<Map<String, Object>> loadUserProfile(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> then(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult() != null) {
                            return task.getResult().getData();
                        } else return new HashMap<>();
                    }
                });
    }

    /**
     * getUserToken()
     * gets the token of a particular user
     * @param userId the id of the user
     */
    public static Task<String> getUserToken(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot result = task.getResult();
                        if (result != null && result.contains(Const.USER_TOKEN_KEY)) {
                            return (String) result.get(Const.USER_TOKEN_KEY);
                        }
                        return "";
                    }
                });
    }

    /**
     * getFriendsList()
     * gets the friends list of a user
     * @param userId the id of the user
     */
    public static Task<QuerySnapshot> getFriendsList(String userId) {
        // get a list of the friends ids of a user
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .get();
    }

    /**
     * getFriendEmails()
     * gets a list of the emails of all of a user's friends
     * @param userId the id of this user
     */
    public static Task<QuerySnapshot> getFriendEmails(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .get();
    }

    /**
     * getFriendRequests()
     * gets a list of the emails of the users that sent this user a friend request
     * @param userId the id of the user
     */
    public static Task<QuerySnapshot> getFriendRequests(String userId) {
        // get a list of the friend requests of a user
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_F_REQUESTS_COLLECTION)
                .get();
    }

    /**
     * getUserDatesForCal()
     * gets the list of dates the user is available, updates them to make sure that none of them are
     * before the current day, converts them to Date objects that can be displayed by the
     * CalendarView in CalendarActivity
     * @param userId the id of the user
     */
    public static Task<DocumentSnapshot> getUserDates(String userId) {
        Log.d(Const.TAG, "getUserDatesForCal: " + Thread.currentThread().getId());
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .get();
    }

    /**
     * getUserName()
     * gets the name of the current user
     * @param userId the id of the current user
     */
    public static Task<String> getUserName(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc == null || !doc.contains(Const.USER_NAME_KEY) ||
                                !(doc.get(Const.USER_NAME_KEY) instanceof String))
                            return "";
                        return (String) doc.get(Const.USER_NAME_KEY);
                    }
                });
    }


    // TRIP SETTERS

    /**
     * addTrip()
     * adds a new trip to the database
     * @param data a map of the data and fields to be added
     */
    public static Task<String> addTrip(Map<String, Object> data) {
        // add the trip to the database
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .add(data)
                .continueWith(new Continuation<DocumentReference, String>() {
                    @Override
                    public String then(@NonNull Task<DocumentReference> task) {
                        DocumentReference doc = task.getResult();
                        if (doc != null) return doc.getId();
                        else return "";
                    }
                });
    }

    /**
     * updateTrip()
     * updates the non-sub-collection fields of a trip in the db
     * @param tripId the id of the trip
     * @param data a map containing the data and fields to be updated
     */
    public static Task<Void> updateTrip(String tripId, Map<String, Object> data) {
        Log.d(Const.TAG, "updateTrip: " + Thread.currentThread().getId());
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .update(data);
    }

    /**
     * addTripper()
     * adds a tripper to the trippers sub-collection of a trip
     * @param tripId the id of the trip
     * @param tripperId the id of the tripper to be added
     */
    public static Task<Void> addTripper(String tripId, String tripperId, String tripperEmail,
                                                                    String tripperName) {
        // add the tripper id to a map
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_ID_KEY, tripperId);
        data.put(Const.USER_EMAIL_KEY, tripperEmail);
        data.put(Const.USER_NAME_KEY, tripperName);

        // add a document to the trippers sub-collection of this trip
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_TRIPPERS_COLLECTION)
                .document(tripperId)
                .set(data);
    }

    /**
     * addTripComment()
     * adds a comment to the comments sub-collection of a trip
     * @param tripId the id of the trip
     * @param comment the comment to be added
     */
    public static Task<DocumentReference> addTripComment(String tripId, String comment, String userName,
                                                         String userId, long timestamp) {
        // add the comment to a map
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_COMMENT_KEY, comment);
        data.put(Const.USER_NAME_KEY, userName);
        data.put(Const.USER_ID_KEY, userId);
        data.put(Const.TRIP_TIMESTAMP_KEY, timestamp);


        // add a document to the comments sub-collection of this trip
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_COMMENTS_COLLECTION)
                .add(data);
    }

    /**
     * addTripPhoto()
     * adds a photo to storage and adds its path to the photo paths sub collection of this trip
     * @param tripId the id of the trip
     * @param path the path where the photo will be stored in the photo bucket
     */
    public static Task<DocumentReference> addTripPhoto(String tripId, String path,
                                                       long timestamp) {
        // create a map containing the path of the photo
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_PHOTO_KEY, path);
        data.put(Const.TRIP_TIMESTAMP_KEY, timestamp);

        // add the picture path to the photo paths sub-collection of this trip
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_PHOTO_PATHS_COLLECTION)
                .add(data);
    }

    /**
     * addTripLocation()
     * adds a location to the locations sub collection of this trip
     * @param tripId the id of this trip
     * @param location the Place object representing the location
     */
    public static void addTripLocation(String tripId, LatLng location, String name, long timeStamp) {
        // create a map to hold the data
        Map<String, Object> data = new HashMap<>();
        String coordinate = location.latitude + "," +
                location.longitude;
        data.put(Const.TRIP_LOCATION_KEY, coordinate);
        data.put(Const.TRIP_LOCATION_NAME_KEY, name);
        data.put(Const.TRIP_TIMESTAMP_KEY, timeStamp);

        // add the location to the locations sub-collection
        FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_LOCATIONS_COLLECTION)
                .add(data);
    }

    /**
     * deleteTrip()
     * deletes a trip from the database
     * NOTE: ** This function only deletes the document associated with a trip.
     *          It does NOT delete the trippers or photos or comments sub collections.
     *          It also does NOT remove the trips's photo album from the storage bucket.
     *          Finally, it does not delete the trip from the user_trips sub collection of
     *          all the users that went on the trip.
     *          These actions are performed by the onTripDeleted cloud function (see index.js)
     * @param tripId the id of the trip
     */
    public static Task<Void> deleteTrip(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .delete();
    }

    /**
     * deleteTripper()
     * removes the document corresponding to a tripper from the trippers sub collection of the trip
     * @param tripId the id of the trip
     * @param tripperId the id of the tripper
     */
    public static Task<Void> deleteTripper(String tripId, String tripperId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_TRIPPERS_COLLECTION)
                .document(tripperId)
                .delete();
    }

    /**
     * deleteTripPhoto()
     * deletes a trip photo from the storage bucket and deletes the info of the trip photo from
     * the trips sub-collection in the db
     * @param tripId the id of the trip
     * @param docId the id of the document that stores the photo's information
     */
    public static Task<Void> deleteTripPhoto(String tripId, String docId) {
        // delete the document corresponding to the photo from the database
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_PHOTO_PATHS_COLLECTION)
                .document(docId)
                .delete();
    }

    /**
     * deleteTripLocation()
     * @param tripId the id of the trip
     * @param docId the id of the document containing the location to be deleted
     */
    public static Task<Void> deleteTripLocation(String tripId, String docId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_LOCATIONS_COLLECTION)
                .document(docId)
                .delete();
    }


    // TRIP GETTERS

    /**
     * getTripSummaries()
     * returns a list of trip summaries of all the trips that this user has been on
     * @param userId the id of this user
     */
    public static Task<QuerySnapshot> getTripSummaries(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_TRIPS_COLLECTION)
                .get();
    }

    /**
     * getTripInfo
     * gets all of the info within the document of a particular trip
     * @param tripId the id of the trip
     */
    public static Task<Map<String, Object>> getTripInfo(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> then(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc == null || doc.getData() == null) return new HashMap<>();
                        return doc.getData();
                    }
                });
    }

    /**
     * getTrippers()
     * gets a list of all the trippers of a given trip
     * @param tripId the id of the trip
     */
    public static Task<QuerySnapshot> getTrippers(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_TRIPPERS_COLLECTION)
                .get();
    }

    /**
     * getTripComments()
     * gets all of the comments of a given trip
     * @param tripId the id of the trip
     */
    public static Task<QuerySnapshot> getTripComments(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_COMMENTS_COLLECTION)
                .get();
    }

    /**
     * getTripPhotos()
     * gets the paths and timestamps of all the photos associated with a given trip
     * @param tripId the id of the trip
     */
    public static Task<QuerySnapshot> getTripPhotos(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_PHOTO_PATHS_COLLECTION)
                .get();
    }

    /**
     * getTripLocations()
     * get all the locations from this trip
     * @param tripId the id of the trip
     */
    public static Task<QuerySnapshot> getTripLocations(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_LOCATIONS_COLLECTION)
                .get();
    }
}
