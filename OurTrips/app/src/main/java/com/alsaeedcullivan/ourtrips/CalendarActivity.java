package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

/**
 * This library uses the CalendarPickerView from the Times Square open source library.
 * We claim no rights over the library or its content and have not altered it in any way.
 * We are simply implementing it to use its CalendarPickerView.
 * The Times Square library is licenced under the Apache License, Version 2.0
 * The library can be found on github at https://github.com/square/android-times-square
 */
public class CalendarActivity extends AppCompatActivity {

    FirebaseUser mUser;
    TextView mHeader;
    CalendarPickerView mCalView;
    ProgressBar mSpinner;
    TextView mLoading;
    List<Date> mDateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // set the title and the back button
        setTitle(getString(R.string.title_activity_calendar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get a references to the widgets
        mHeader = findViewById(R.id.calendar_text);
        mCalView = findViewById(R.id.calendar_view);
        mSpinner = findViewById(R.id.spinner);
        mLoading = findViewById(R.id.loading_text);

        // set initial visibility
        mHeader.setVisibility(View.GONE);
        mCalView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);

        // set up the CalendarPickerView
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        mCalView.init(today, nextYear.getTime()).inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        // get the current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            // get the list of dates the user is available
            Task<List<Date>> datesTask = AccessDB.getUserDatesForCal(mUser.getUid());
            datesTask.addOnCompleteListener(new OnCompleteListener<List<Date>>() {
                @Override
                public void onComplete(@NonNull Task<List<Date>> task) {
                    if (task.isSuccessful()) {
                        // get the list of dates that were retrieved from the database
                        mDateList = task.getResult();

                        // if there is a list of dates that the user has already selected,
                        // then display them
                        // select the dates in reverse order so that the view focuses on the one
                        // that is soonest
                        if (mDateList != null && mDateList.size() > 0) {
                            for (int i = mDateList.size() - 1; i >= 0; i--) {
                                mCalView.selectDate(mDateList.get(i));
                            }
                        }

                        // make the calendar visible
                        makeCalAppear();
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
        if (mUser != null && mDateList != null) {
            // update the user's available dates
            AccessDB.setUserDatesFromCal(mUser.getUid(), mDateList);
        }

        // finish the activity
        finish();
    }

    /**
     * makeCalAppear()
     * makes the calendar visible and the progress bar invisible
     */
    private void makeCalAppear() {
        mLoading.setVisibility(View.GONE);
        mSpinner.setVisibility(View.GONE);
        mHeader.setVisibility(View.VISIBLE);
        mCalView.setVisibility(View.VISIBLE);
    }
}
