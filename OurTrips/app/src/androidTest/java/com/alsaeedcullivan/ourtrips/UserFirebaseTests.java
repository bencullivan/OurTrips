package com.alsaeedcullivan.ourtrips;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    public void saveNewUserFriend() {

        Task<Void> testTask = AccessDB.addUserFriend("test_user_id_5", "test_user_id_1");

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "saveNewUserFriend: complete");
    }

    @Test
    public void deleteUserTest() {

        Task<Void> task = AccessDB.deleteUser("cFnEWhp1axaB6Rn9AMIzxeemIMk1");

        while (!task.isComplete()) { }

        Log.d(Const.TAG, "deleteUserTest: complete");
    }

    @Test
    public void addDateObject() throws ParseException {
        Map<String, Object> data = new HashMap<>();
        SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
        Date d = f.parse("05-20-2020");
        data.put("date", d);
        Log.d(Const.TAG, "addDateObject: " + d);
        Task<Void> task = AccessDB.addNewUser("date_test", data);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) Log.d(Const.TAG, "onComplete: success");
                else Log.d(Const.TAG, "onComplete: fail");
            }
        });

        while (!task.isComplete()) { }


    }
}
