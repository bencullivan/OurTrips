package com.alsaeedcullivan.ourtrips;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.Trip;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.Task;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TripFirebaseTests {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.alsaeedcullivan.ourtrips", appContext.getPackageName());
    }

    @Test
    public void saveNewTrip() {

        // create a new trip object
        Trip trip = new Trip();
        trip.setTripId("test_trip_id");
        trip.setTitle("grand canyon");
        ArrayList<String> users = new ArrayList<>();
        users.add("user_1");
        users.add("user_2");
        trip.setUsersList(users);

        // create an instance of the class that accesses the database
        AccessDB testAccess = new AccessDB();

        Task<Void> testTask = testAccess.addTrip(trip);

        while (!testTask.isComplete()) { }

        Log.d(Const.TAG, "saveNewTrip: complete");
    }
}
