package com.alsaeedcullivan.ourtrips;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.cloud.CloudFunctions;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.TestVars;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void topicTest() {

        String user = "Z0oThyxHsIhuJe3bLVmX2Nynh973";
        String topic = "test_topic_blah";

        Task<Void> task = FirebaseMessaging.getInstance().subscribeToTopic("test_topic");

        //String token = FirebaseInstanceId.getInstance().getToken();

        //Log.d(Const.TAG, "topicTest: " + token);
        //Task <HttpsCallableResult> task = CloudFunctions.subscribeToTopic(user, topic);

        while (!task.isComplete()) { }

        Log.d(Const.TAG, "topicTest: done");
    }

    @Test
    public void updateTest() {

        String topic = "test_topic_wtf";
        String type = "test";
        String notification = "fuck";

        Task<HttpsCallableResult> task = CloudFunctions.collectionUpdated(topic, type, notification);

        while (!task.isComplete()) { }

        Log.d(Const.TAG, "updateTest: done");
        Log.d(Const.TAG, "updateTest: ajdsnadsfjnaps;dnsd");

    }

}
