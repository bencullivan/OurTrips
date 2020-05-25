package com.alsaeedcullivan.ourtrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class ViewUserActivity extends AppCompatActivity {

    private ImageView mProfileImageView;
    private TextView mUserName, mUserEmail, mUserBirthday, mUserAffiliation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // get reference to image view
        mProfileImageView = findViewById(R.id.img_user);

        // get references to text views
        mUserName = findViewById(R.id.user_name);
        mUserEmail = findViewById(R.id.user_email);
        mUserBirthday = findViewById(R.id.user_birthday);
        mUserAffiliation = findViewById(R.id.user_affiliation);
    }
}
