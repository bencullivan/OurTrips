package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    FirebaseUser mUser;
    CalendarPickerView mCalView;
    List<Date> mDateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalView = findViewById(R.id.calendar_view);

        // get the current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null) {
            // get the list of dates the user is available
            Task<List<Date>> datesTask = AccessDB.getUserDatesForCal(mUser.getUid());
            datesTask.addOnCompleteListener(new OnCompleteListener<List<Date>>() {
                @Override
                public void onComplete(@NonNull Task<List<Date>> task) {
                    if (task.isSuccessful()) {
                        // highlight the cells corresponding to the available dates
                        mDateList = task.getResult();
                        if (mDateList != null) {
                            // set up the CalendarPickerView
                            Calendar nextYear = Calendar.getInstance();
                            nextYear.add(Calendar.YEAR, 1);
                            Date today = new Date();
                            mCalView.init(today, nextYear.getTime())
                                    .withSelectedDates(mDateList)
                                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
                        }
                    }
                }
            });
        }
    }
}
