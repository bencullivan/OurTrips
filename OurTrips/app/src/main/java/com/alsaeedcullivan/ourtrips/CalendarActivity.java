package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.utils.Const;
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

        // set the title and the back button
        setTitle(getString(R.string.title_activity_calendar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get a reference to the calendar view
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cal_save_button:
                onSaveClicked();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onSaveClicked()
     * adds the dates the user has selected to the database when the user clicks "save" in the
     * options menu
     */
    private void onSaveClicked() {
        // get the list of dates that were selected
        mDateList = mCalView.getSelectedDates();

        // check to make sure there is a user that has has added dates
        if (mUser != null && mDateList != null && mDateList.size() > 0) {
            Log.d(Const.TAG, "onSaveClicked: " + Thread.currentThread().getId());
            // update the user's available dates
            AccessDB.setUserDatesFromCal(mUser.getUid(), mDateList);
        }

    }
}
