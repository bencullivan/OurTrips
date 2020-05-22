package com.alsaeedcullivan.ourtrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestTripActivity extends AppCompatActivity {

    private SimpleDateFormat mFormat;
    private UserSummary mFriend;

    // widgets
    private EditText mTitle;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mFriendInfo;
    private Button mSelectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_trip);

        // set up the back button and the title
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Arrange a trip!");

        // get widget references
        mTitle = findViewById(R.id.edit_title);
        mStartDate = findViewById(R.id.start_date_text);
        mEndDate = findViewById(R.id.end_date_text);
        mSelectButton = findViewById(R.id.select_end_date);
        mFriendInfo = findViewById(R.id.friend_info_text);

        // initialize the simple date format
        mFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        // get the intent
        Intent intent = getIntent();

        if (intent != null) {
            long time = intent.getLongExtra(Const.SELECTED_DATE_TAG, -1);
            mFriend = intent.getParcelableExtra(Const.SELECTED_FRIEND_TAG);
            if (time != -1 && mFriend != null) {
                // update the text
                String start = mStartDate.getText().toString();
                start += " " + mFormat.format(new Date(time));
                mStartDate.setText(start);
                String name = mFriendInfo.getText().toString();
                name += " " + mFriend.getName();
                mFriendInfo.setText(name);
            }
        }
    }


}
