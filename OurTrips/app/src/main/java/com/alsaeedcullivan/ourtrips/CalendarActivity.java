package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * This library uses the CalendarPickerView from the Times Square open source library.
 * We claim no rights over the library or its content and have not altered it in any way.
 * We are simply implementing it to use its CalendarPickerView.
 * The Times Square library is licenced under the Apache License, Version 2.0
 * The library can be found on github at https://github.com/square/android-times-square
 */
public class CalendarActivity extends AppCompatActivity {

    // savedInstanceState keys
    public static final String DATE_LIST_KEY = "date_list_key";
    public static final String MATCHED_KEY = "matched_dates";
    public static final String SOURCE_KEY = "source";
    public static final String RECENT_KEY = "recent";
    public static final String FRIEND_KEY = "friend";

    private FirebaseUser mUser;
    private TextView mHeader;
    private CalendarPickerView mCalView;
    private ProgressBar mSpinner;
    private TextView mLoading;
    private Date mRecent;
    private long[] mMatched;
    private HashSet<Date> mSet;
    private UserSummary mFriend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // set the back button and title
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_calendar));

        // get references to the widgets
        mHeader = findViewById(R.id.calendar_text);
        mCalView = findViewById(R.id.calendar_view);
        mSpinner = findViewById(R.id.spinner);
        mLoading = findViewById(R.id.loading_text);

        // set initial visibility
        mHeader.setVisibility(View.GONE);
        mCalView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);

        // get the current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // set up the CalendarPickerView
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        mCalView.init(today, nextYear.getTime()).inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        // get the source
        Intent intent = getIntent();
        String source = intent.getStringExtra(Const.SOURCE_TAG);


        // SET UP THE CALENDAR

        // MATCH MODE
        if (source != null && source.equals(Const.MATCH_TAG) && savedInstanceState != null &&
                savedInstanceState.getLongArray(MATCHED_KEY) != null && savedInstanceState
                .getParcelable(FRIEND_KEY) != null) {
            mHeader.setText(R.string.click_matched);
            mMatched = savedInstanceState.getLongArray(MATCHED_KEY);
            mFriend = savedInstanceState.getParcelable(FRIEND_KEY);
            mSet = new HashSet<>();
            if (mMatched != null && mMatched.length > 0) {
                // select all of the matched dates
                for (int i = mMatched.length - 1; i >= 0; i--) {
                    Date d = new Date(mMatched[i]);
                    mCalView.selectDate(d);
                    mSet.add(d);
                }
                mCalView.setDateSelectableFilter(nonSelectable());
                mCalView.setOnInvalidDateSelectedListener(proceedToRequest());
                makeCalAppear();
            }
        }
        else if (source != null && source.equals(Const.MATCH_TAG)) {
            mHeader.setText(R.string.click_matched);
            mMatched = intent.getLongArrayExtra(Const.MATCH_ARR_TAG);
            mFriend = intent.getParcelableExtra(Const.SELECTED_FRIEND_TAG);
            mSet = new HashSet<>();
            if (mMatched != null && mMatched.length > 0) {
                // select all of the matched dates
                for (int i = mMatched.length - 1; i >= 0; i--) {
                    Date d = new Date(mMatched[i]);
                    mCalView.selectDate(d);
                    mSet.add(d);
                }
                mCalView.setDateSelectableFilter(nonSelectable());
                mCalView.setOnInvalidDateSelectedListener(proceedToRequest());
                makeCalAppear();
            }
        }
        // SELECT MODE
        // if there was already a list of selected dates in select mode no need to load from the database
        else if (savedInstanceState != null && savedInstanceState.getLongArray(DATE_LIST_KEY) != null) {
            long[] times = savedInstanceState.getLongArray(DATE_LIST_KEY);
            if (times != null) {
                // select the dates that were saved in savedInstanceState
                for (int i = times.length - 1; i >= 0; i--) {
                    mCalView.selectDate(new Date(times[i]));
                }
                // make sure it focuses in on the date they most recently pressed
                if (savedInstanceState.getLong(RECENT_KEY) != 0) {
                    mRecent = new Date(savedInstanceState.getLong(RECENT_KEY));
                    mCalView.scrollToDate(mRecent);
                }
                mCalView.setOnInvalidDateSelectedListener(doNothing());
                mCalView.setOnDateSelectedListener(maintainRecent());
                // show the calendar
                makeCalAppear();
            }
        }
        // get the list of dates from the db that the user is available
        else if (mUser != null) {
            Task<List<Date>> datesTask = AccessDB.getUserDatesForCal(mUser.getUid());
            datesTask.addOnCompleteListener(new OnCompleteListener<List<Date>>() {
                @Override
                public void onComplete(@NonNull Task<List<Date>> task) {
                    if (task.isSuccessful()) {
                        // get the list of dates that were retrieved from the database
                        List<Date> dates = task.getResult();
                        // if there is a list of dates that the user has already selected,
                        // then display them
                        // select the dates in reverse order so that the view focuses on the one
                        // that is soonest
                        if (dates != null && dates.size() > 0) {
                            for (int i = dates.size() - 1; i >= 0; i--) {
                                mCalView.selectDate(dates.get(i));
                            }
                        }
                        mCalView.setOnInvalidDateSelectedListener(doNothing());
                        mCalView.setOnDateSelectedListener(maintainRecent());
                        // display the calendar
                        makeCalAppear();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        String extra = getIntent().getStringExtra(Const.SOURCE_TAG);
        if (extra != null && extra.equals(Const.MATCH_TAG)) {
            menu.findItem(R.id.cal_save_button).setVisible(false);
        }
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMatched != null) outState.putLongArray(MATCHED_KEY, mMatched);
        else outState.putLongArray(DATE_LIST_KEY, toLongs(mCalView.getSelectedDates()));
        if (mRecent != null) outState.putLong(RECENT_KEY, mRecent.getTime());
        if (mFriend != null) outState.putParcelable(FRIEND_KEY, mFriend);
    }

    /**
     * onSaveClicked()
     * adds the dates the user has selected to the database when the user clicks "save" in the
     * options menu
     */
    private void onSaveClicked() {
        // get the list of dates that were selected
        List<Date> dates = mCalView.getSelectedDates();
        // check to make sure there is a user that has has added dates
        if (mUser != null && dates != null) {
            // update the user's available dates
            AccessDB.setUserDatesFromCal(mUser.getUid(), dates);
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

    /**
     * proceedToRequest()
     * @return a listener that takes the user to request trip activity if they click on one of the
     * matched dates
     */
    private CalendarPickerView.OnInvalidDateSelectedListener proceedToRequest() {
        return new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                if (mSet.contains(date)) {
                    // proceed to request trip activity
                    Intent intent = new Intent(CalendarActivity.this,
                            RequestTripActivity.class);
                    intent.putExtra(Const.SELECTED_DATE_TAG, date.getTime());
                    intent.putExtra(Const.SELECTED_FRIEND_TAG, mFriend);
                    startActivity(intent);
                }
            }
        };
    }

    /**
     * nonSelectable()
     * @return a listener that prevents the user from being able to select any date
     */
    public CalendarPickerView.DateSelectableFilter nonSelectable() {
        return new CalendarPickerView.DateSelectableFilter() {
            @Override
            public boolean isDateSelectable(Date date) {
                return false;
            }
        };
    }

    /**
     * doNothing()
     * @return a listener that does nothing when the user selects an invalid date
     */
    public CalendarPickerView.OnInvalidDateSelectedListener doNothing() {
        return new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                // do nothing, this prevents the default toast message from being displayed
            }
        };
    }

    /**
     * maintainRecent()
     * @return a listener that keeps track of which date the user most recently selected
     */
    private CalendarPickerView.OnDateSelectedListener maintainRecent() {
        return new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                mRecent = date;
            }
            @Override
            public void onDateUnselected(Date date) {
                mRecent = date;
            }
        };
    }

    /**
     * toLongs()
     * Converts a List of Date objects to an array of longs.
     * This will be used in conjunction with onSavedInstanceState because a Date object cannot
     * be added to a Bundle
     *
     * @param dates the List of Dates
     * @return an array of longs corresponding to the time of each date
     */
    private long[] toLongs(List<Date> dates) {
        long[] times = new long[dates.size()];
        for (int i = 0; i < dates.size(); i++) times[i] = dates.get(i).getTime();
        return times;
    }
}
