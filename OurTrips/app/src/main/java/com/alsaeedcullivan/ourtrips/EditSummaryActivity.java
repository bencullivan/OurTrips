package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditSummaryActivity extends AppCompatActivity {

    // saved instance state keys
    private static final String END_KEY = "end";
    private static final String START_KEY = "start";

    // widgets
    private TextView mTitle;
    private EditText mOver;
    private TextView mEndTextView;

    private SimpleDateFormat mFormat;
    private Date mStartDate, mEndDate;
    private String mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);

        // back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get widget references
        mTitle = findViewById(R.id.title_sum);
        mOver = findViewById(R.id.edit_over_sum);
        TextView startTextView = findViewById(R.id.start_date_sum);
        mEndTextView = findViewById(R.id.end_date_sum);
        Button endChange = findViewById(R.id.select_end_date);

        // initialize the simple date format
        mFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        // set listener
        endChange.setOnClickListener(createEndSelectListener());

        // get the data from the intent and populate the fields
        Intent intent = getIntent();
        if (intent != null) {
            mTitle.setText(intent.getStringExtra(Const.TRIP_TITLE_TAG));
            mOver.setText(intent.getStringExtra(Const.TRIP_OVER_TAG));
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);
            if (savedInstanceState != null && savedInstanceState.getString(END_KEY) != null &&
                    savedInstanceState.getString(START_KEY) != null) {
                try {
                    String start = savedInstanceState.getString(START_KEY);
                    if (start != null) mStartDate = mFormat.parse(start);
                    start = "Start Date: " + start;
                    startTextView.setText(start);
                    String end = savedInstanceState.getString(END_KEY);
                    if (end != null) mEndDate = mFormat.parse(end);
                    end = "End Date: " + end;
                    mEndTextView.setText(end);
                } catch (ParseException e){
                    finish();
                }
            } else {
                String start = intent.getStringExtra(Const.TRIP_START_TAG);
                if (start != null) {
                    try {
                        mStartDate = mFormat.parse(start);
                        String startText = "Start Date: " + start;
                        startTextView.setText(startText);
                    } catch (ParseException e) {
                        finish();
                    }
                }
                String end = intent.getStringExtra(Const.TRIP_END_TAG);
                if (end != null) {
                    try {
                        mEndDate = mFormat.parse(end);
                        String endText = "End Date: " + end;
                        mEndTextView.setText(endText);
                    } catch (ParseException e) {
                        finish();
                    }
                }
            }
        } else finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEndDate != null) outState.putString(END_KEY, mFormat.format(mEndDate));
        if (mStartDate != null) outState.putString(START_KEY, mFormat.format(mStartDate));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_summary_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mTripId == null) {
                    finish();
                    return true;
                }
                Log.d(Const.TAG, "onOptionsItemSelected: home");
                Intent intent = new Intent(EditSummaryActivity.this, TripActivity.class);
                intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                startActivity(intent);
                finish();
                return true;
            case R.id.save_summary_button:
                saveSummary();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * saveSummary()
     * saves this data to the db
     */
    private void saveSummary() {
        if (mStartDate == null || mEndDate == null) {
            Toast t = Toast.makeText(this, "You must select a start and end date",
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

        // create a map to hold the data
        final Map<String, Object> data = new HashMap<>();
        data.put(Const.TRIP_TITLE_KEY, mTitle.getText().toString());
        data.put(Const.TRIP_OVERVIEW_KEY, mOver.getText().toString());
        data.put(Const.TRIP_START_DATE_KEY, mFormat.format(mStartDate));
        data.put(Const.TRIP_END_DATE_KEY, mFormat.format(mEndDate));

        // update the trip in the db
        // run on background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccessDB.updateTrip(mTripId, data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // toast the user
                            Toast t = Toast.makeText(EditSummaryActivity.this, "Trip updated successfully!",
                                    Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                            // send the user to trip activity
                            Intent intent = new Intent(EditSummaryActivity.this, TripActivity.class);
                            intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                            startActivity(intent);
                            // finish this activity
                            finish();
                        } else {
                            // toast the user
                            Toast t = Toast.makeText(EditSummaryActivity.this, "This trip could"
                                    + " not be updated.", Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                        }
                    }
                });
            }
        }).start();
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

    // get an on click listener for the change end date button
    private View.OnClickListener createEndSelectListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new date picker dialog
                CustomDialogFragment.newInstance(CustomDialogFragment.SUMMARY_END_DATE_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }

    /**
     * updateEndDate()
     * updates the end date of the trip
     * @param date the new end date
     */
    public void updateEndDate(Date date) {
        mEndDate = date;
        String end = "End Date: " + mFormat.format(mEndDate);
        mEndTextView.setText(end);
    }


    // GETTERS

    public Date getStartDate() {
        if (mStartDate != null) return mStartDate;
        else return new Date();
    }

    public Date getEndDate() {
        if (mEndDate != null) return mEndDate;
        else return new Date();
    }
}
