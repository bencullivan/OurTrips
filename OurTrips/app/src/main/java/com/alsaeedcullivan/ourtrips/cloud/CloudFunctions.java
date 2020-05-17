package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudFunctions {

    private static List<String> resultDates;

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
    public static Task<List<String>> matchDates(List<String> list1, List<String> list2) {
        // get a reference to the Firebase cloud functions
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        // create a data object to hold the user ids
        Map<String, Object> data = new HashMap<>();
        data.put(Const.LIST_1_KEY, list1);
        data.put(Const.LIST_2_KEY, list2);

        // call the matchDates cloud function
        Task<List<String>> task = functions.getHttpsCallable(Const.FUNC_MATCH_DATES)
                .call(data).continueWith(new Continuation<HttpsCallableResult, List<String>>() {
                    @Override
                    public List<String> then(@NonNull Task<HttpsCallableResult> task) {
                        if (task.getResult() == null || task.getResult().getData() == null ||
                                !(task.getResult().getData() instanceof List)) {
                            return new ArrayList<>();
                        }
                        //NOTE:
                        // the data will always be a list of strings, ignore the "unchecked cast"
                        List<String> datesList = (List<String>) task.getResult().getData();
                        Log.d(Const.TAG, "then: " + datesList);
                        return datesList;
                    }
                });

        // add listeners
        task.addOnSuccessListener(new OnSuccessListener<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                Log.d(Const.TAG, "onSuccess: " + strings);
                Log.d(Const.TAG, "onSuccess: ");
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
