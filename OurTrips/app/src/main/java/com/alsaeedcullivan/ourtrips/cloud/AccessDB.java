package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.models.User;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.InputStream;
import java.text.ParseException;
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

    //SETTERS TO UPDATE THE DB

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
        // convert the parameters to final constants
        final List<Date> dDates = dates;
        final String id = userId;

        // run the conversion from dates to strings on a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // convert the list of dates to a list of strings in the desired format
                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy",
                        Locale.getDefault());
                List<String> sDates = new ArrayList<>();
                for (Date date : dDates) {
                    sDates.add(format.format(date));
                }

                // add the list of dates to the database
                FirebaseFirestore.getInstance()
                        .collection(Const.USERS_COLLECTION)
                        .document(id)
                        .update(Const.DATE_LIST_KEY, sDates);
            }
        }).start();
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
     * deleteRequest()
     * deletes a friend request from the db
     */
    public static void deleteRequest(String userId, String friendId) {
        FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_F_REQUESTS_COLLECTION)
                .document(friendId)
                .delete();
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

    /**
     * addTrip()
     * adds a new trip to the database
     * @param data a map of the data and fields to be added
     */
    public static Task<String> addTrip(Map<String, Object> data) {
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
    public static void addTripper(String tripId, String tripperId) {
        // add the tripper id to a map
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_TRIPPER_KEY, tripperId);
        // add a document to the trippers sub-collection of this trip
        FirebaseFirestore.getInstance()
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
     * @param docId the id of the document that will hold the comment
     */
    public static void addTripComment(String tripId, String comment, String docId) {
        // add the comment to a map
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_COMMENT_KEY, comment);
        // add a document to the comments sub-collection of this trip
        FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_COMMENTS_COLLECTION)
                .document(docId)
                .set(data);
    }

    /**
     * addTripPhoto()
     * adds a photo to storage and adds its path to the photo paths sub collection of this trip
     * @param tripId the id of the trip
     * @param path the path where the photo will be stored in the photo bucket
     * @param is the input stream that will be used to upload the photo to the storage bucket
     */
    public static void addTripPhoto(String tripId, String path, InputStream is) {
        // upload the picture to storage
        AccessBucket.uploadPicture(path, is);

        // create a map containing the path of the photo
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_PHOTO_KEY, path);

        // add the picture path to the photo paths sub-collection of this trip
        FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .collection(Const.TRIP_PHOTO_PATHS_COLLECTION)
                .document(path)
                .set(data);
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

    // GETTERS TO RETRIEVE DATA FROM THE DB

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
    public static Task<List<UserSummary>> getFriendsList(String userId) {
        // get a list of the friends ids of a user
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_FRIENDS_COLLECTION)
                .get()
                .continueWith(new Continuation<QuerySnapshot, List<UserSummary>>() {
                    @Override
                    public List<UserSummary> then(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot result = task.getResult();
                        if (result == null || result.getDocuments().size() == 0)
                            return new ArrayList<>();

                        // extract and return the ids of the documents
                        List<DocumentSnapshot> docList = result.getDocuments();
                        List<UserSummary> friends = new ArrayList<>();
                        for (DocumentSnapshot doc : docList) {
                            UserSummary u = new UserSummary();
                            u.setUserId(doc.getId());
                            u.setEmail((String)doc.get(Const.USER_EMAIL_KEY));
                            u.setName((String)doc.get(Const.USER_NAME_KEY));
                            friends.add(u);
                        }
                        return friends;
                    }
                });
    }

    /**
     * getFriendRequests()
     * gets a list of the emails of the users that sent this user a friend request
     * @param userId the id of the user
     */
    public static Task<List<UserSummary>> getFriendRequests(String userId) {
        // get a list of the friend requests of a user
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .collection(Const.USER_F_REQUESTS_COLLECTION)
                .get()
                .continueWith(new Continuation<QuerySnapshot, List<UserSummary>>() {
                    @Override
                    public List<UserSummary> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        QuerySnapshot result = task.getResult();
                        if (result == null || result.getDocuments().size() == 0)
                            return new ArrayList<>();

                        // extract and return a user summary for each document
                        List<DocumentSnapshot> docList = result.getDocuments();
                        List<UserSummary> friends = new ArrayList<>();
                        for (DocumentSnapshot doc : docList) {
                            UserSummary u = new UserSummary();
                            u.setUserId(doc.getId());
                            u.setEmail((String)doc.get(Const.USER_EMAIL_KEY));
                            u.setName((String)doc.get(Const.USER_NAME_KEY));
                            friends.add(u);
                        }
                        return friends;
                    }
                });
    }

    /**
     * getUserDatesForCal()
     * gets the list of dates the user is available, updates them to make sure that none of them are
     * before the current day, converts them to Date objects that can be displayed by the
     * CalendarView in CalendarActivity
     * @param userId the id of the user
     */
    public static Task<List<Date>> getUserDatesForCal(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, List<Date>>() {
                    @Override
                    public List<Date> then(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot result = task.getResult();
                        if (result == null || !result.contains(Const.DATE_LIST_KEY) ||
                            !(result.get(Const.DATE_LIST_KEY) instanceof List))
                            return new ArrayList<>();

                        // NOTE: if execution makes it past the above if statement this cast will
                        // not throw an exception
                        List<String> sDates = (List<String>) result.get(Const.DATE_LIST_KEY);
                        List<Date> realDates = new ArrayList<>();

                        if (sDates == null) return realDates;

                        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        try {
                            Date today = format.parse(format.format(new Date()));

                            // convert each string date into a date object
                            // make sure that none of these dates are before today
                            for (String date : sDates) {
                                Date d = format.parse(date);
                                if (d != null && d.compareTo(today) >= 0) {
                                    realDates.add(d);
                                }
                            }
                            return realDates;
                        } catch (ParseException e) {
                            return realDates;
                        }
                    }
                });
    }

    /**
     * getUserDatesForMatch()
     * retrieves the list of the dates the user is available
     * @param userId the id of the user
     */
    public static Task<List<String>> getUserDatesForMatch(String userId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(userId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, List<String>>() {
                    @Override
                    public List<String> then(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot result = task.getResult();
                        if (result == null || !result.contains(Const.DATE_LIST_KEY)
                            || result.get(Const.DATE_LIST_KEY) == null) return new ArrayList<>();

                        // NOTE: if execution makes it past the above if statement
                        // this cast will not throw an exception
                        return (List<String>) result.get(Const.DATE_LIST_KEY);
                    }
                });
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


    // HANDLE FRIEND REQUESTS

    /**
     * sendFriendRequest()
     * sends a friend request from this user to another user
     * @param userEmail the email of this user
     * @param friendEmail the email of the person they are sending the request to
     */
    public static void sendFriendRequest(String userId, String userEmail, String userName, String friendEmail) {
        final String id = userId;
        // store this user's summary data in a map
        final Map<String, Object> data = new HashMap<>();
        data.put(Const.FRIEND_ID_KEY, userId);
        data.put(Const.USER_EMAIL_KEY, userEmail);
        data.put(Const.USER_NAME_KEY, userName);
        // get the friend from the db based on the email that was passed in
        final FirebaseFirestore store = FirebaseFirestore.getInstance();
        store.collection(Const.USERS_COLLECTION)
                .whereEqualTo(Const.USER_EMAIL_KEY, friendEmail)
                .get()
                .continueWith(new Continuation<QuerySnapshot, Object>() {
                    @Override
                    public Object then(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot q = task.getResult();
                        if (q != null && q.size() > 0) {
                            DocumentSnapshot doc = q.getDocuments().get(0);
                            String friendId = doc.getId();
                            store.collection(Const.USERS_COLLECTION)
                                    .document(friendId)
                                    .collection(Const.USER_F_REQUESTS_COLLECTION)
                                    .document(id)
                                    .set(data);
                        }
                        return q;
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


    // DATE MATCHING

    /**
     * matchDates()
     * takes in the user's list of dates and the id of a friend they want to match with
     * returns a list of dates that they are both available that can then be displayed by a calendar
     * @param dates the dates this user is available
     * @param friendId the id of the friend they want to match with
     */
    public static Task<long[]> matchDates(List<Date> dates, String friendId) {
        final List<Date> userDates = dates;
        return FirebaseFirestore.getInstance()
                .collection(Const.USERS_COLLECTION)
                .document(friendId)
                .get()
                .continueWith(new Continuation<DocumentSnapshot, long[]>() {
                    @Override
                    public long[] then(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc == null || !doc.contains(Const.DATE_LIST_KEY) ||
                                !(doc.get(Const.DATE_LIST_KEY) instanceof List))
                                        return new long[0];

                        // if execution makes it this far, this cast will not throw an exception
                        List<String> stringDates = (List<String>) doc.get(Const.DATE_LIST_KEY);
                        // list of Dates to store the converted friend dates
                        List<Date> friendDates = new ArrayList<>();

                        if (stringDates == null) return new long[0];
                        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

                        try {
                            // convert the strings to dates
                            for (String date : stringDates) {
                                friendDates.add(format.parse(date));
                            }
                            // initialize a list to hold the matched dates
                            List<Date> matched = new ArrayList<>();

                            Log.d(Const.TAG, "then: match: userDates: " + userDates);
                            Log.d(Const.TAG, "then: match: friend: " + friendDates);

                            int a = 0;
                            int b = 0;
                            while (a < userDates.size() && b < friendDates.size()) {
                                int result = userDates.get(a).compareTo(friendDates.get(b));
                                if (result < 0) a++;
                                else if (result > 0 ) b++;
                                else {
                                    matched.add(userDates.get(a));
                                    a++;
                                    b++;
                                }
                            }
                            // return an array of the times of the matched dates
                            long[] out = new long[matched.size()];
                            for (int i = 0; i < matched.size(); i++) out[i] = matched.get(i).getTime();
                            return out;
                        } catch (ParseException e) {
                            return new long[0];
                        }
                    }
                });
    }

}
