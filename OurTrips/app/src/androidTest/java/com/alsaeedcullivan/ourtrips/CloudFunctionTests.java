package com.alsaeedcullivan.ourtrips;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.alsaeedcullivan.ourtrips.cloud.CloudFunctions;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.HttpsCallableResult;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CloudFunctionTests {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.alsaeedcullivan.ourtrips", appContext.getPackageName());
    }

    @Test
    public void recursiveDeleteTest() {
        String id = "testUserId";

        String path1 = Const.USERS_COLLECTION+"/"+id+"/"+Const.USER_TRIPS_COLLECTION;

        Task<HttpsCallableResult> testTask = CloudFunctions.recursiveDelete(path1);

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "recursiveDelete: complete");
    }

    @Test
    public void matchDatesTest() {
        String user1 = "testUserId";
        String user2 = "test_user_id";

        Task<HttpsCallableResult> testTask = CloudFunctions.matchDates(user1, user2);

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "matchDatesTest: complete");
    }
}
