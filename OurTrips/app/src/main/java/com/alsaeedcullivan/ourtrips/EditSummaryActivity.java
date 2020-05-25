package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.utils.Const;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditSummaryActivity extends AppCompatActivity {

    // saved instance state keys
    private static final String END_KEY = "end";
    private static final String START_KEY = "start";

    // widgets
    private EditText mTitle, mOver;
    private TextView mStart, mEnd;
    private Button mSChange, mEChange;

    private SimpleDateFormat mFormat;
    private Date mStartDate, mEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);

        // get widget references
        mTitle = findViewById(R.id.edit_title_sum);
        mOver = findViewById(R.id.edit_over_sum);
        mStart = findViewById(R.id.sum_start_date_text);
        mEnd = findViewById(R.id.sum_end_date_text);
        mSChange = findViewById(R.id.select_start_date);
        mEChange = findViewById(R.id.select_end_date);

        // initialize the simple date format
        mFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        // set listeners
        mSChange.setOnClickListener(createSelectListener());
        mEChange.setOnClickListener(createSelectListener());

        // get the data from the intent and populate the fields
        Intent intent = getIntent();
        if (intent != null) {
            mTitle.setText(intent.getStringExtra(Const.TRIP_TITLE_TAG));
            mOver.setText(intent.getStringExtra(Const.TRIP_OVER_TAG));
            if (savedInstanceState != null && savedInstanceState.getString(END_KEY) != null &&
                    savedInstanceState.getString(START_KEY) != null) {
                try {
                    String start = savedInstanceState.getString(START_KEY);
                    if (start != null) mStartDate = mFormat.parse(start);
                    start = "Start Date: " + start;
                    mStart.setText(start);
                    String end = savedInstanceState.getString(END_KEY);
                    if (end != null) mEndDate = mFormat.parse(end);
                    end = "End Date: " + end;
                    mEnd.setText(end);
                } catch (ParseException e){
                    finish();
                }
            } else {
                String start = intent.getStringExtra(Const.TRIP_START_TAG);
                if (start != null) {
                    try {
                        mStartDate = mFormat.parse(start);
                        String startText = "Start Date: " + start;
                        mStart.setText(startText);
                    } catch (ParseException e) {
                        finish();
                    }
                }
                String end = intent.getStringExtra(Const.TRIP_END_TAG);
                if (end != null) {
                    try {
                        mEndDate = mFormat.parse(end);
                        String endText = "End Date: " + end;
                        mEnd.setText(endText);
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

    /**
     * updateStartDate()
     * updates the start date of the trip
     * @param date the new start date
     */
    public void updateStartDate(Date date) {
        mStartDate = date;
        String start = "Start Date: " + mFormat.format(mStartDate);
        mStart.setText(start);
    }

    /**
     * updateEndDate()
     * updates the end date of the trip
     * @param date the new end date
     */
    public void updateEndDate(Date date) {
        mEndDate = date;
        String end = "End Date: " + mFormat.format(mEndDate);
        mEnd.setText(end);
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
