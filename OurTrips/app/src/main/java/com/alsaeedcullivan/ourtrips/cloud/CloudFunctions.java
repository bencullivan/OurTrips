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

    /**
     * matchDates()
     * this calls the matchDates cloud function (see index.js) which takes two user Ids and
     * determines which dates both users are available
     * @param list1 - the list of dates that the first user is available
     * @param list2 - the list of dates that the second user is available
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

}
