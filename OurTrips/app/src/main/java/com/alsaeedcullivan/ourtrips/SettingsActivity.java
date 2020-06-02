package com.alsaeedcullivan.ourtrips;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private String mEmail;
    private String mPassword;
    private List<DocumentSnapshot> mFriendsList;
    private List<DocumentSnapshot> mTripsList;
    private String mPath;
    LinearLayout mTop;
    LinearLayout mBottom;
    TextView mBar;
    TextView mLoading;
    ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // widget references
        mTop = findViewById(R.id.top_settings_layout);
        mBottom = findViewById(R.id.settings_lower);
        mBar = findViewById(R.id.bar_settings);
        mLoading = findViewById(R.id.settings_loading);
        mSpinner = findViewById(R.id.settings_spinner);

        hideSpinner();

        // set on click listeners
        Button editProfile = findViewById(R.id.edit_profile_settings);
        Button deleteProfile = findViewById(R.id.delete_profile_settings);
        Button signOut = findViewById(R.id.sign_out_settings);
        Button web = findViewById(R.id.web_settings);
        editProfile.setOnClickListener(createEditProfileListener());
        deleteProfile.setOnClickListener(createDeleteListener());
        signOut.setOnClickListener(createSignOutListener());
        web.setOnClickListener(createWebListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    // visibility
    private void showSpinner() {
        mTop.setVisibility(View.GONE);
        mBottom.setVisibility(View.GONE);
        mBar.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }
    private void hideSpinner() {
        mSpinner.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mTop.setVisibility(View.VISIBLE);
        mBottom.setVisibility(View.VISIBLE);
        mBar.setVisibility(View.VISIBLE);
    }

    // listeners
    private View.OnClickListener createEditProfileListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(SettingsActivity.this, RegisterActivity.class);
                editIntent.putExtra(Const.SOURCE_TAG, Const.SETTINGS_TAG);
                startActivity(editIntent);
            }
        };
    }

    private View.OnClickListener createSignOutListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // log the user out of firebase
                FirebaseAuth.getInstance().signOut();

                // send user back to login
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.putExtra(Const.SOURCE_TAG, Const.SETTINGS_TAG);
                startActivity(intent);
                // finish this activity and others in stack -> user logged out
                finishAffinity();
            }
        };
    }

    private View.OnClickListener createDeleteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment.newInstance(CustomDialogFragment.DELETE_PROFILE_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }

    private View.OnClickListener createWebListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://omaralsaeed8.wixsite.com/ourtrips")));
            }
        };
    }

    /**
     * createAuthentication
     * creates a dialog that will ask the user to input their credentials
     */
    public void createAuthentication() {
        CustomDialogFragment.newInstance(CustomDialogFragment.AUTHENTICATE_ID)
                .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
    }

    /**
     * authenticate()
     * re-authenticates the user
     */
    public void authenticate(String email, String password) {
        // if they are missing info, tell them
        if (email == null || password == null || email.replaceAll("\\s", "").equals("")
                || password.replaceAll("\\s", "").equals("")) {
            Toast t = Toast.makeText(this, "In order to delete your profile you must " +
                    "enter your email and password.", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
            return;
        }

        // get the current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) return;

        // if the email is wrong, tell them
        if (!email.equals(mUser.getEmail())) {
            Toast t = Toast.makeText(this, "The email you enter must be the one you used to " +
                    "register your account.", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            t.show();
            return;
        }
        mEmail = email;
        mPassword = password;

        showSpinner();

        // re-authenticate the user
        new AuthenticateTask().execute();
    }


    // ASYNC TASKS

    /**
     * AuthenticateTask
     * re-authenticates the user
     */
    private class AuthenticateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mEmail == null || mPassword == null) return null;

            // get an auth credential from the email and password that were provided
            AuthCredential cred = EmailAuthProvider.getCredential(mEmail, mPassword);

            // re-authenticate the user
            mUser.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // delete the user
                        new GetFriendsTask().execute();
                    } else {
                        hideSpinner();
                        Toast t = Toast.makeText(SettingsActivity.this, "The credentials " +
                                "you gave could not be confirmed", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });

            return null;
        }
    }

    // ASYNC TASKS FOR USER DELETION

    private class GetFriendsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            AccessDB.getFriendsList(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mFriendsList = task.getResult().getDocuments();
                        if (mFriendsList.size() == 0) new GetTripsTask().execute();
                        else new DeleteFromFriendsTask().execute();
                    } else new GetTripsTask().execute();
                }
            });

            return null;
        }
    }

    private class DeleteFromFriendsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mFriendsList == null || mFriendsList.size() == 0) return null;

            List<Task<Void>> taskList = new ArrayList<>();

            for (DocumentSnapshot doc : mFriendsList) {
                Task<Void> t = FirebaseFirestore.getInstance()
                        .collection(Const.USERS_COLLECTION)
                        .document(doc.getId())
                        .collection(Const.USER_FRIENDS_COLLECTION)
                        .document(mUser.getUid())
                        .delete();
                taskList.add(t);
            }

            Tasks.whenAll(taskList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(Const.TAG, "onComplete: done deleting from friends");
                    new GetTripsTask().execute();
                }
            });

            return null;
        }
    }

    private class GetTripsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            AccessDB.getTripSummaries(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mTripsList = task.getResult().getDocuments();
                        if (mTripsList.size() == 0) new GetPicTask().execute();
                        else new DeleteFromTripsTask().execute();
                    } else new GetPicTask().execute();
                }
            });

            return null;
        }
    }

    private class DeleteFromTripsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mTripsList == null || mTripsList.size() == 0) return null;

            List<Task<Void>> taskList = new ArrayList<>();

            for (DocumentSnapshot doc : mTripsList) {
                Task<Void> t = FirebaseFirestore.getInstance()
                        .collection(Const.TRIPS_COLLECTION)
                        .document(doc.getId())
                        .collection(Const.TRIP_TRIPPERS_COLLECTION)
                        .document(mUser.getUid())
                        .delete();
                taskList.add(t);
            }

            Tasks.whenAll(taskList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(Const.TAG, "onComplete: done removing from trips");
                    new GetPicTask().execute();
                }
            });

            return null;
        }
    }

    private class GetPicTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            AccessDB.loadUserProfile(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                @Override
                public void onComplete(@NonNull Task<Map<String, Object>> task) {
                    if (task.isSuccessful() && task.getResult() != null &&
                            task.getResult().get(Const.USER_PROFILE_PIC_KEY) != null) {
                        mPath = (String) task.getResult().get(Const.USER_PROFILE_PIC_KEY);
                        new DeletePhotoTask().execute();
                    } else new DeleteUserTask().execute();
                }
            });

            return null;
        }
    }

    private class DeletePhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mPath == null) return null;

            AccessBucket.deleteFromStorage(mPath).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(Const.TAG, "onComplete: profile pic deleted");
                    new DeleteUserTask().execute();
                }
            });

            return null;
        }
    }

    /**
     * DeleteUserTask
     * permanently deletes this user from the db
     */
    private class DeleteUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            // delete the user from the database
            AccessDB.deleteUser(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        new DeleteFromAuthTask().execute();
                    } else {
                        hideSpinner();
                        Toast t = Toast.makeText(SettingsActivity.this, "We were " +
                                "unable to delete your profile", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();

                    }
                }
            });

            return null;
        }
    }

    /**
     * DeleteFromAuthTask
     * permanently deletes this user from firebase auth
     */
    private class DeleteFromAuthTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            // delete the user from authentication
            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(Const.TAG, "onComplete: done deleting user from both");
                    // send user back to login
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.putExtra(Const.SOURCE_TAG, Const.SETTINGS_TAG);
                    startActivity(intent);
                    // at this point the async task is finished
                    // finish this activity and others in stack -> user logged out because profile was deleted
                    finishAffinity();
                }
            });

            return null;
        }
    }
}