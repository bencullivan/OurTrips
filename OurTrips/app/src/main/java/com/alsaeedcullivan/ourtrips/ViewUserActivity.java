package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.glide.GlideApp;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

public class ViewUserActivity extends AppCompatActivity {

    private ImageView mProfileImageView;
    private TextView mUserName, mUserEmail, mUserBirthday, mUserAffiliation, mUserBio;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // set title
        setTitle("Tripper");

        // get reference to image view
        mProfileImageView = findViewById(R.id.img_user);

        // get references to text views
        mUserName = findViewById(R.id.user_name);
        mUserEmail = findViewById(R.id.user_email);
        mUserBirthday = findViewById(R.id.user_birthday);
        mUserAffiliation = findViewById(R.id.user_affiliation);
        mUserBio = findViewById(R.id.user_bio);

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(Const.USER_ID_KEY) != null) {
            loadProfile(intent.getStringExtra(Const.USER_ID_KEY));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * loadProfile()
     * loads the profile of the selected user
     * @param userId the id of the selected user
     */
    private void loadProfile(String userId) {
        mUserId = userId;
        new LoadProfileTask().execute();
    }

    /**
     * populateFields()
     * adds the user's data to the view
     * @param data a map containing the user's profile data
     */
    private void populateFields(Map<String, Object> data) {
        // set the profile pic
        String path = (String) data.get(Const.USER_PROFILE_PIC_KEY);
        if (path != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference(path);
            GlideApp.with(this).load(ref).into(mProfileImageView);
        }
        // set the name
        String name = (String) data.get(Const.USER_NAME_KEY);
        if (name != null) mUserName.setText(name);
        // set the email
        String email = (String) data.get(Const.USER_EMAIL_KEY);
        if (email != null) mUserEmail.setText(email);
        // set the affiliation
        String aff = (String) data.get(Const.USER_AFFILIATION_KEY);
        if (aff != null) mUserAffiliation.setText(aff);
        // set the bio
        String bio = (String) data.get(Const.USER_BIO_KEY);
        if (bio != null) mUserBio.setText(bio);
        // set the birthday
        String bDay = (String) data.get(Const.USER_BIRTHDAY_KEY);
        if (bDay != null) mUserBirthday.setText(bDay);
    }

    /**
     * LoadProfileTask
     * loads the profile of the user that was selected
     */
    private class LoadProfileTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUserId == null) return null;

            // load the profile
            AccessDB.loadUserProfile(mUserId).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                @Override
                public void onComplete(@NonNull Task<Map<String, Object>> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = task.getResult();
                        if (data != null) populateFields(data);
                    } else {
                        // tell the user that this tripper's profile could not be loaded
                        Toast t = Toast.makeText(ViewUserActivity.this, "This tripper's " +
                                "profile could not be loaded.", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });

            return null;
        }
    }
}
