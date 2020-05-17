package com.alsaeedcullivan.ourtrips;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.cloud.CloudFunctions;
import com.alsaeedcullivan.ourtrips.models.Trip;
import com.alsaeedcullivan.ourtrips.models.User;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.TestVars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.functions.HttpsCallableResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserFirebaseTests {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.alsaeedcullivan.ourtrips", appContext.getPackageName());
    }

    @Test
    public void saveNewUserTest() {
        // create a new User object
        String id = "test_user_id_1";
        Map<String, Object> data = new HashMap<>();
        data.put(Const.USER_ID_KEY, id);
        data.put(Const.USER_NAME_KEY, "test_user_name_1");
        data.put(Const.USER_GENDER_KEY, "Male");
        data.put(Const.USER_BIRTHDAY_KEY, "02-05-2001");
        data.put(Const.DATE_LIST_KEY, new ArrayList<String>());
        data.put(Const.USER_AFFILIATION_KEY, "dartmouth student");

        // save the user to the database
        Task<Void> testTask = AccessDB.addNewUser(id, data);
        testTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(Const.TAG, "onComplete: success add user");
                } else Log.d(Const.TAG, "onComplete: fail add user");
            }
        });

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "saveNewUserTest: is complete ");
    }

    @Test
    public void saveNewUserTrip() {

        // save the trip to the user's sub-collection
        Task<Void> testTask = AccessDB.addUserTrip("test_user_id_1", "testTrip3");

        while (!testTask.isComplete()) { }
        Log.d(Const.TAG, "saveNewUserTrip: complete: " + testTask.isComplete());
        Log.d(Const.TAG, "saveNewUserTrip: successful: " + testTask.isSuccessful());
        Log.d(Const.TAG, "saveNewUserTrip: canceled: " + testTask.isCanceled());

    }

    @Test
    public void saveNewUserFriend() {

        Task<Void> testTask = AccessDB.addUserFriend("test_user_id_1", "test_user_id_5");

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "saveNewUserFriend: complete");
    }
}
