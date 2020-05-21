package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class of static methods to handle calls to the HTTPS onCall cloud functions
 */
public class CloudFunctions {


    // DATE MATCHING ALGORITHM

    /**
     * matchDates()
     * this calls the matchDates cloud function (see index.js) which takes two user Ids and
     * determines which dates both users are available
     * @param list1 the list of dates that the first user is available
     * @param list2 the list of dates that the second user is available
     */
    public static Task<List<String>> matchDates(List<String> list1, List<String> list2) {
        // create a data object to hold the user ids
        Map<String, Object> data = new HashMap<>();
        data.put(Const.LIST_1_KEY, list1);
        data.put(Const.LIST_2_KEY, list2);

        // call the matchDates cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_MATCH_DATES)
                .call(data).continueWith(new Continuation<HttpsCallableResult, List<String>>() {
                    @Override
                    public List<String> then(@NonNull Task<HttpsCallableResult> task) {
                        if (task.getResult() == null || task.getResult().getData() == null ||
                                !(task.getResult().getData() instanceof List)) {
                            return new ArrayList<>();
                        }

                        // NOTE: if execution made it past the above if statement this cast
                        // will not throw an exception
                        List<String> datesList = (List<String>) task.getResult().getData();
                        Log.d(Const.TAG, "then: " + datesList);
                        return datesList;
                    }
                });
    }


    // FIREBASE CLOUD MESSAGING

    /**
     * collectionUpdated()
     * called when a user updates a sub-collection of a trip
     * sends a cloud message to all other trippers informing them that the trip has been updated
     * @param topic the topic of the trip
     * @param type the sub-collection that was updated
     * @param notification the notification that will be sent out
     */
    public static Task<HttpsCallableResult> collectionUpdated(String topic, String type, String notification) {
        // create a map to hold the data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_TOPIC_KEY, topic);
        data.put(Const.TRIP_COLL_UPDATE_TYPE_KEY, type);
        data.put(Const.TRIP_NOTIFY_KEY, notification);

        // call the cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_COLLECTION_UPDATED)
                .call(data);
    }

    /**
     * tripInfoUpdated()
     * sends a cloud message to all trippers that the trip's info has been updated
     * @param topic the topic of the trip
     * @param other not sure yet
     */
    public static Task<HttpsCallableResult> tripInfoUpdated(String topic, String other) {
        //TODO: determine what will get passed in

        Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_TOPIC_KEY, topic);

        // call the cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_TRIP_INFO_UPDATED)
                .call(data);
    }


    // SUBSCRIPTION MANAGEMENT

    /**
     * subscribeToTopics()
     * subscribes a user to all the topics associated with their trips
     * @param userId the id of the user
     * @param token the new token
     */
    public static Task<HttpsCallableResult> subscribeToTopics(String userId, String token) {
        // create a map to hold the data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_ID_KEY, userId);
        data.put(Const.USER_TOKEN_KEY, token);

        // call the cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_SUBSCRIBE_TOPICS)
                .call(data);
    }

    /**
     * unsubscribeFromTopics()
     * Unsubscribe a user from all their topics
     * @param userId the id of the user
     * @param token the old token
     */
    public static Task<HttpsCallableResult> unsubscribeFromTopics(String userId, String token) {
        // create a map to hold the data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_ID_KEY, userId);
        data.put(Const.USER_TOKEN_KEY, token);

        // call the cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_UNSUBSCRIBE_TOPICS)
                .call(data);
    }

    /**
     * subscribeToTopic()
     * subscribes a user to a single topic
     * @param userId the id of the user
     * @param topic the topic they want to be subscribed to
     */
    public static Task<HttpsCallableResult> subscribeToTopic(String userId, String topic) {
        // create a map to hold the data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_ID_KEY, userId);
        data.put(Const.TRIP_TOPIC_KEY, topic);

        // call the cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_SUBSCRIBE_TOPIC)
                .call(data);
    }

    /**
     * unsubscribeFromTopic()
     * Unsubscribe a user from a single topic
     * @param userId the id of the user
     * @param topic the topic they want to be unsubscribed from
     */
    public static Task<HttpsCallableResult> unsubscribeFromTopic(String userId, String topic) {
        // create a map to hold the data
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_ID_KEY, userId);
        data.put(Const.TRIP_TOPIC_KEY, topic);

        // call the cloud function
        return FirebaseFunctions.getInstance().getHttpsCallable(Const.FUNC_UNSUBSCRIBE_TOPIC)
                .call(data);
    }
}
