package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class RequestTripActivity extends AppCompatActivity {

    private final String END_KEY = "end_date";

    private SimpleDateFormat mFormat;
    private UserSummary mFriend;
    private String mUserName;
    private Date mStart;
    private Date mEnd;
    private HashMap<String, Object> mData;
    private FirebaseUser mUser;
    private String mTripId;
    private String mTripTitle;

    // widgets
    private EditText mTitle;
    private TextView mEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_trip);

        // set up the back button and the title
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Arrange a trip!");

        // get widget references
        mTitle = findViewById(R.id.edit_title);
        mEndDate = findViewById(R.id.end_date_text);
        TextView startDate = findViewById(R.id.start_date_text);
        Button selectButton = findViewById(R.id.select_end_date);
        TextView friendInfo = findViewById(R.id.friend_info_text);

        // set on click listener for the select end date button
        selectButton.setOnClickListener(createSelectListener());

        // initialize the simple date format
        mFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        // if the end date has already been selected, display it
        if (savedInstanceState != null && savedInstanceState.getString(END_KEY) != null) {
            try {
                mEnd = mFormat.parse(Objects.requireNonNull(savedInstanceState.getString(END_KEY)));
            } catch (ParseException e) {
                mEnd = new Date();
            }
            String end = "End Date: " + mFormat.format(Objects.requireNonNull(mEnd));
            mEndDate.setText(end);
        }

        // get the intent
        Intent intent = getIntent();

        if (intent != null) {
            long time = intent.getLongExtra(Const.SELECTED_DATE_TAG, -1);
            mFriend = intent.getParcelableExtra(Const.SELECTED_FRIEND_TAG);
            mUserName = intent.getStringExtra(Const.USER_NAME_TAG);
            if (time != -1 && mFriend != null) {
                // update the text
                mStart = new Date(time);
                String start = "Start Date: " + mFormat.format(mStart);
                startDate.setText(start);
                String name = friendInfo.getText().toString();
                name += " " + mFriend.getName();
                friendInfo.setText(name);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.req_trip_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_trip_req_button:
                onSaveClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEnd != null) outState.putString(END_KEY, mFormat.format(mEnd));
    }

    /**
     * onSaveClicked()
     * called when the user presses save in the menu
     * saves the trip to the db and adds it to the trips sub-collections of the user and their friend
     */
    private void onSaveClicked() {
        // make sure the user selected an end date
        if (mEnd == null) {
            Toast t = Toast.makeText(this, "You must select an end date.",
                    Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
            return;
        }
        // make sure the user set a title
        else if (mTitle.getText().toString().replaceAll("\\s","").equals("")) {
            Log.d(Const.TAG, "onSaveClicked: no title");
            Toast t = Toast.makeText(this, "You must set a title.", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
            return;
        }

        // add the initial trip data to a map
        mData = new HashMap<>();
        mData.put(Const.TRIP_START_DATE_KEY, mFormat.format(mStart));
        mData.put(Const.TRIP_END_DATE_KEY, mFormat.format(mEnd));
        mData.put(Const.TRIP_TITLE_KEY, mTitle.getText().toString());

        // add this trip to the db
        new AddTripTask().execute();
    }

    /**
     * updateEndDate()
     * updates the end date of the trip
     * @param date the date that was selected
     */
    public void updateEndDate(Date date) {
        mEnd = date;
        String end = "End Date: " + mFormat.format(mEnd);
        mEndDate.setText(end);
    }

    /**
     * endBeforeStart()
     * tells the user that the end date cannot be before the start date
     */
    public void endBeforeStart() {
        Toast t = Toast.makeText(this, "The end date cannot be before the start date.",
                Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();
    }

    // get an on click listener for the select date button
    private View.OnClickListener createSelectListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new date picker dialog
                CustomDialogFragment.newInstance(CustomDialogFragment.SELECT_END_DATE_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }

    // getters

    public Date getDate() {
        // return the appropriate date
        if (mEnd == null) return mStart;
        else return mEnd;
    }

    public Date getStart() {
        return mStart;
    }


    // ASYNC TASKS

    /**
     * AddTripTask
     * adds this trip to the db
     */
    private class AddTripTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mData == null) return null;

            // add the trip to the db
            AccessDB.addTrip(mData).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        // get the id of the new trip
                        mTripId = task.getResult();
                        // get the current user
                        mUser = FirebaseAuth.getInstance().getCurrentUser();
                        // get the trip title
                        mTripTitle = mTitle.getText().toString();
                        if (mTripId == null || mTripId.equals("") || mFriend == null || mUser == null) {
                            Log.d(Const.TAG, "onComplete: friend or trip fail");
                            return;
                        }
                        // update the sub collections
                        new SubCollectionTask().execute();

                        Log.d(Const.TAG, "onComplete: done yay");
                        Intent intent = new Intent(RequestTripActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        // toast the user
                        Toast t = Toast.makeText(RequestTripActivity.this, "This trip could"
                                + " not be added.", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });

            return null;
        }
    }

    /**
     * SubCollectionTask
     * updates the user trips and trip trippers sub collections
     */
    private class SubCollectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mFriend == null || mTripId == null || mTripTitle == null ||
                    mFormat == null || mStart == null || mUserName == null) return null;

            // update the sub collections
            AccessDB.addUserTrip(mUser.getUid(), mTripId, mTripTitle, mFormat.format(mStart));
            AccessDB.addUserTrip(mFriend.getUserId(), mTripId, mTripTitle, mFormat.format(mStart));
            AccessDB.addTripper(mTripId, mUser.getUid(), mUser.getEmail(), mUserName);
            AccessDB.addTripper(mTripId, mFriend.getUserId(), mFriend.getEmail(), mFriend.getName());

            return null;
        }
    }

}
