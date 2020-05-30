package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.SharedPreference;
import com.alsaeedcullivan.ourtrips.utils.Utilities;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// activity for email verification
public class VerifyActivity extends AppCompatActivity {

    // text widgets
    private EditText mEmailText;
    private EditText mPasswordText;

    private String mEmail = "";
    private String mPassword = "";
    FirebaseUser mUser;
    FirebaseAuth mAuth;

    public String mEmailExtra;   // input email intent extra
    public String mPasswordExtra;   // input password intent extrad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Log.d(Const.TAG, "onCreate: verify");

        // set-up activity title
        setTitle(getString(R.string.title_activity_verify));

        // enable back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to the text widgets
        mEmailText = findViewById(R.id.verify_username);
        mPasswordText = findViewById(R.id.verify_password);

        mAuth = FirebaseAuth.getInstance();

        // extract input email & password, if entered in LoginActivity
        processIntent();
    }

    // handle lifecycle //

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Const.USER_EMAIL_KEY, mEmail);
        outState.putString(Const.USER_PASSWORD_KEY, mPassword);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the email and password if they have been saved
        if (savedInstanceState.getString(Const.USER_EMAIL_KEY) != null)
            mEmail = savedInstanceState.getString(Const.USER_EMAIL_KEY);
        else mEmail = "";
        if (savedInstanceState.getString(Const.USER_PASSWORD_KEY) != null)
            mPassword = savedInstanceState.getString(Const.USER_PASSWORD_KEY);
        else mPassword = "";
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(Const.TAG, "onResume: verify ");
        // get the current user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // if there is a current user, reload them to check if they have clicked the link in the
        // email that was sent to them
        if (mUser != null) new ReloadTask().execute();
    }

    /**
     * onVerifyClicked()
     * called when the verify button is clicked, it saves a user's email and password and sends
     * them a verification email to verify their email address
     */
    public void onVerifyClicked(View view) {

        // get the email and password input
        mEmail = mEmailText.getText().toString();
        mPassword = mPasswordText.getText().toString();

        // if the password is not valid, inform the user and do not attempt to register them
        if (!Utilities.isValidPassword(mPassword)) {
            mPasswordText.setError(getString(R.string.invalid_password));
            mPasswordText.requestFocus();
            return;
        }

        // get a reference to the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // attempt to sign the user in
        new SignInTask().execute();
    }

    /**
     * processIntent()
     * Retrieves email and password info from an intent
     */
    private void processIntent() {
        // get input email & password & load accordingly
        mEmailExtra = getIntent().getStringExtra(Const.USER_EMAIL_KEY);
        mPasswordExtra = getIntent().getStringExtra(Const.USER_PASSWORD_KEY);
        if (mEmailExtra != null) mEmailText.setText(mEmailExtra);
        if (mPasswordExtra != null) mPasswordText.setText(mPasswordExtra);
    }

    /**
     * ReloadTask
     * reloads the user to check if they are verified
     */
    private class ReloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            // reload the user to see if they are verified
            mUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // if they have verified their account, send them to RegisterActivity if they
                    // have not registered yet
                    if (mUser.isEmailVerified()) {
                        new CheckRegisterTask().execute();
                    }
                }
            });

            return null;
        }
    }

    /**
     * SignInTask
     * attempts to sign the user in
     */
    private class SignInTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mAuth == null) return null;

            // try to sign the user in, if there is no account, create one
            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (mUser == null || !mUser.isEmailVerified()) {
                            // inform the user they are not verified
                            Toast t = Toast.makeText(VerifyActivity.this, "Please follow " +
                                            "the link that we sent to you in order to verify your account",
                                    Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                            return;
                        }
                        // check if the user is registered
                        new CheckRegisterTask().execute();
                    } else {
                        // attempt to create a new user
                        new CreateUserTask().execute();
                    }
                }
            });

            return null;
        }
    }

    /**
     * CheckRegisterTask
     * checks to see if this user is registered
     */
    private class CheckRegisterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            FirebaseFirestore.getInstance()
                    .collection(Const.USERS_COLLECTION)
                    .document(mUser.getUid())
                    .get()
                    .continueWith(new Continuation<DocumentSnapshot, Object>() {
                        @Override
                        public Object then(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();

                            // if they are registered
                            if (doc != null && doc.exists()) {
                                // inform the user that they have already registered
                                Toast t = Toast.makeText(VerifyActivity.this,
                                        "You are already verified and registered.",
                                        Toast.LENGTH_SHORT);
                                t.setGravity(Gravity.TOP | Gravity
                                                .CENTER_HORIZONTAL, 0,
                                        0);
                                t.show();
                            }
                            // they are not registered
                            else {
                                // allow them to proceed to register activity
                                // if they are verified
                                Intent intent = new Intent(VerifyActivity.this,
                                        RegisterActivity.class);
                                intent.putExtra(Const.SOURCE_TAG, Const.VERIFY_TAG);
                                startActivity(intent);
                                finish();
                            }
                            return null;
                        }
                    });

            return null;
        }
    }

    /**
     * CreateUserTask
     * creates a user with the credentials that were input
     */
    private class CreateUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mAuth == null || mEmail == null || mPassword == null) return null;

            // create a new user with the input credentials
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // send a verification email
                        new VerificationTask().execute();
                    } else {
                        // inform the user that the email was not valid
                        mEmailText.setError(getString(R.string.email_not_valid));
                        mEmailText.requestFocus();
                    }
                }
            });

            return null;
        }
    }

    /**
     * VerificationTask
     * sends a verification email to the user
     */
    private class VerificationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mAuth == null || mAuth.getCurrentUser() == null) return null;

            // send the verification email
            mAuth.getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // set that the user has not registered
                        new SharedPreference(getApplicationContext()).setRegistered(false);
                        // inform the user that an email has been sent
                        Toast t = Toast.makeText(VerifyActivity.this, "A verification " +
                                "email has been sent to " + mEmail, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    } else {
                        // inform the user that an email was not sent
                        Toast t = Toast.makeText(VerifyActivity.this, "We were not " +
                                        "able to send a verification email to " + mEmail + ", are " +
                                        "you sure this is your correct email address?", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });

            return null;
        }
    }
}
