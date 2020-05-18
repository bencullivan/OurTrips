package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// activity for email verification
public class VerifyActivity extends AppCompatActivity {

    // text widgets
    private EditText mEmailText;
    private EditText mPasswordText;

    private String mEmail = "";
    private String mPassword = "";

    public String mEmailExtra;   // input email intent extra
    public String mPasswordExtra;   // input password intent extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // set-up activity title
        setTitle(getString(R.string.title_activity_verify));

        // enable back button
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to the text widgets
        mEmailText = findViewById(R.id.verify_username);
        mPasswordText = findViewById(R.id.verify_password);

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

        // get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        // if there is a current user, reload them to check if they have clicked the link in the
        // email that was sent to them
        if (user != null) user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // if they have verified their account, send them to RegisterActivity
                if (user.isEmailVerified()) {
                    Intent intent = new Intent(VerifyActivity.this,
                            RegisterActivity.class);
                    intent.putExtra(Const.SOURCE_TAG, Const.VERIFY_KEY);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    // on click listener //

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
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        // create a new user with the input credentials
        auth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // send a verification email to the user
                            if (auth.getCurrentUser() != null)
                                auth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // inform the user that an email has been sent
                                        Toast t = Toast.makeText(VerifyActivity.this,
                                                "A verification email has been sent to " +
                                                        mEmail, Toast.LENGTH_SHORT);
                                        t.setGravity(Gravity.TOP |
                                                Gravity.CENTER_HORIZONTAL, 0, 0);
                                        t.show();
                                    } else {
                                        // inform the user that an email was not sent
                                        Toast t = Toast.makeText(VerifyActivity.this,
                                                "We were not able to send a verification email" +
                                                        " to " + mEmail + ", are you sure this is " +
                                                        "your correct email address?",
                                                        Toast.LENGTH_SHORT);
                                        t.setGravity(Gravity.TOP |
                                                Gravity.CENTER_HORIZONTAL, 0, 0);
                                        t.show();
                                    }
                                }
                            });
                        } else {
                            // inform the user that the email was not valid
                            mEmailText.setError(getString(R.string.email_not_valid));
                            mEmailText.requestFocus();
                        }
                    }
                });
    }

    private void processIntent() {
        // get input email & password & load accordingly
        mEmailExtra = getIntent().getStringExtra(Const.USER_EMAIL_KEY);
        mPasswordExtra = getIntent().getStringExtra(Const.USER_PASSWORD_KEY);
        if (mEmailExtra != null) mEmailText.setText(mEmailExtra);
        if (mPasswordExtra != null) mPasswordText.setText(mEmailExtra);
    }
}
