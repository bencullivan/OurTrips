package com.alsaeedcullivan.ourtrips;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.Trip;
import com.alsaeedcullivan.ourtrips.models.User;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Test;
import org.junit.runner.RunWith;

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
        User user = new User();
        user.setUserId("testUserId");
        user.setName("testUserName");
        user.setAffiliation("student at dartmouth");
        user.setAge("19");
        user.setGender("Male");

        // create an instance of the class that accesses the database
        AccessDB testAccess = new AccessDB();

        // save the user to the database
        Task<Void> testTask = testAccess.addNewUser(user);

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "saveNewUserTest: is complete ");
    }

    @Test
    public void saveNewUserTrip() {

        // create an instance of the class that accesses the database
        AccessDB testAccess = new AccessDB();

        // save the trip to the user's sub-collection
        Task<DocumentReference> testTask = testAccess.addUserTrip("testTrip3");

        while (!testTask.isComplete()) { }
        Log.d(Const.TAG, "saveNewUserTrip: complete: " + testTask.isComplete());
        Log.d(Const.TAG, "saveNewUserTrip: successful: " + testTask.isSuccessful());
        Log.d(Const.TAG, "saveNewUserTrip: canceled: " + testTask.isCanceled());

    }

    @Test
    public void saveNewUserFriend() {

        // create an instance of the class that accesses the database
        AccessDB testAccess = new AccessDB();

        Task<DocumentReference> testTask = testAccess.addUserFriend("testFriendId");

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "saveNewUserFriend: complete");
    }

    @Test
    public void recursiveDeleteTest() {

        // create an instance of the class that accesses the database
        AccessDB testAccess = new AccessDB();

        String id = "testUserId";

        String path1 = Const.USERS_COLLECTION+"/"+id+"/"+Const.USER_TRIPS_COLLECTION;

        Task<String> testTask = testAccess.recursiveDelete(path1);

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "recursiveDelete: complete");
    }
}
