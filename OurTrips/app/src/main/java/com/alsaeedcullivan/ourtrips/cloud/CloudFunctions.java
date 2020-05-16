package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class CloudFunctions {

    /**
     * recursiveDelete()
     * this calls the recursiveDelete cloud function (see index.js) which deletes an entire
     * collection at a given path in the database
     * this will be invoked when a user deletes their account to delete their entire collection of
     * references to trips as well as their entire collection of references to friends
     */
    public static Task<HttpsCallableResult> recursiveDelete(String path) {
        // get a reference to the firebase cloud functions
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        // call the recursiveDelete cloud function
        Task<HttpsCallableResult> task = functions.getHttpsCallable(Const.FUNC_RECURSIVE_DELETE)
                .call(path);

        // add listeners
        task.addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.d(Const.TAG, "onSuccess: yay");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Const.TAG, "onFailure: ");
                Log.d(Const.TAG, Log.getStackTraceString(e));
            }
        });

        // return the task
        return task;
    }

    /**
     * matchDates()
     * this calls the matchDates cloud function (see index.js) which takes two user Ids and
     * determines which dates both users are available
     */
    public static Task<HttpsCallableResult> matchDates(String user1, String user2) {
        // get a reference to the Firebase cloud functions
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        // create a data object to hold the user ids
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_1_KEY, user1);
        data.put(Const.USER_2_KEY, user2);

        // call the matchDates cloud function
        Task<HttpsCallableResult> task = functions.getHttpsCallable(Const.FUNC_MATCH_DATES)
                .call(data);

        // add listeners
        task.addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                Log.d(Const.TAG, "onSuccess: yay");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Const.TAG, "onFailure: ");
                Log.d(Const.TAG, Log.getStackTraceString(e));
            }
        });

        // return the task
        return task;
    }
}
