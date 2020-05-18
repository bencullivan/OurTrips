package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import com.alsaeedcullivan.ourtrips.utils.Const;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class to handle interactions with the database
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
     * @param userId - the id of the user
     * @param dates - a list of Date objects, these will be converted to Strings in the desired
     *              format on a background thread before they are added to the database
     */
    public static void setUserDatesFromCal(String userId, List<Date> dates) {
        // convert the parameters to final constants
        final List<Date> dDates = dates;
        final String id = userId;
        Log.d(Const.TAG, "setUserDatesFromCal: " + Thread.currentThread().getId());

        // run the conversion from dates to strings on a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(Const.TAG, "run: " + Thread.currentThread().getId());

                // convert the list of dates to a list of strings in the desired format
                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                List<String> sDates = new ArrayList<>();
                for (Date date : dDates) {
                    sDates.add(format.format(date));
                }

                if (sDates.size() > 0) {
                    // add the list of dates to the database
                    FirebaseFirestore.getInstance()
                            .collection(Const.USERS_COLLECTION)
                            .document(id)
                            .update(Const.DATE_LIST_KEY, sDates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) Log.d(Const.TAG, "onComplete: successfully added date list");
                                    else Log.d(Const.TAG, "onComplete: fuck this shit im out");
                                }
                            });
                }
            }
        });
    }

    /**
     * loadUserProfile()
     * loads the profile info of a user
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
     *         Finally, it also does NOT delete a user's profile photo from the storage bucket.
     *         These four actions are performed by the onUserDeleted Cloud Function
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
     * NOTE: ** This function only deletes the document associated with a trip.
     *          It does NOT delete the trippers or photos or comments sub collections.
     *          It also does NOT remove the trips's photo album from the storage bucket.
     *          Finally, it does not delete the trip from the user_trips sub collection of
     *          all the users that went on the trip.
     *          These actions are performed by the onTripDeleted cloud function (see index.js)
     */
    public Task<Void> deleteTrip(String tripId) {
        return FirebaseFirestore.getInstance()
                .collection(Const.TRIPS_COLLECTION)
                .document(tripId)
                .delete();
    }

    // GETTERS TO RETRIEVE DATA FROM THE DB

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
                        QuerySnapshot result = task.getResult();
                        if (result == null || result.getDocuments().size() == 0)
                            return new ArrayList<>();

                        // extract and return the ids of the documents
                        List<DocumentSnapshot> docList = result.getDocuments();
                        List<String> friendIds = new ArrayList<>();
                        for (DocumentSnapshot doc : docList) {
                            friendIds.add(doc.getId());
                        }
                        return friendIds;
                    }
                });
    }

    /**
     * getUserDatesForCal()
     * gets the list of dates the user is available, updates them to make sure that none of them are
     * before the current day, converts them to Date objects that can be displayed by the
     * CalendarView in CalendarActivity
     * @param userId - the id of the user
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

}
