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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

public class SettingsActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        // re-authenticate the user
        new AuthenticateTask().execute();
    }

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
                        new DeleteUserTask().execute();
                    } else {
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

    /**
     * DeleteUserTask
     * permanently deletes this user
     */
    private class DeleteUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            // delete the user from the database
            Task<Void> dataTask = AccessDB.deleteUser(mUser.getUid());
            // delete the user from authentication
            Task<Void> authTask = mUser.delete();
            // when both are finished, return to login
            Tasks.whenAll(dataTask, authTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // send user back to login
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.putExtra(Const.SOURCE_TAG, Const.SETTINGS_TAG);
                    startActivity(intent);
                    Log.d(Const.TAG, "onComplete: done deleting user from auth");
                    // at this point the async task is finished
                    // finish this activity and others in stack -> user logged out because profile was deleted
                    finishAffinity();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(Const.TAG, "onPostExecute: done settings delete async");
        }
    }
}