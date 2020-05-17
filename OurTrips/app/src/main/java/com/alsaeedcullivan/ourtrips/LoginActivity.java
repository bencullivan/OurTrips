package com.alsaeedcullivan.ourtrips;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginInActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText mUsernameEditText, mPasswordEditText;
    private Button signInButton, signUpButton;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // determine whether there is a verified user that is logged in
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // if there is a verified user logged in, go straight to main activity
        if (mUser != null && mUser.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        setContentView(R.layout.activity_login);

        // set-up activity title
        setTitle(getString(R.string.title_activity_login));

        // initialize the FireBaseAuth
        mAuth = FirebaseAuth.getInstance();

        // get references to the progress bar
        mProgressBar = findViewById(R.id.loading);

        // get references to the buttons
        signInButton = findViewById(R.id.sign_in);
        signUpButton = findViewById(R.id.sign_up);

        // get references to the text widgets
        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);

        // set the OnClickListener for the sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Const.TAG, "onClick: sign in");

                // get input email & password
                String inputEmail = mUsernameEditText.getText().toString();
                String inputPassword = mPasswordEditText.getText().toString();

                // perform error checking on entries //

                View focusView = null;

                // either fields is invalid
                if (!Utilities.isValidEmail(inputEmail)) {
                    mUsernameEditText.setError(getString(R.string.invalid_username));
                    focusView = mUsernameEditText;
                }
                if (!Utilities.isValidPassword(inputPassword)) {
                    mPasswordEditText.setError(getString(R.string.invalid_password));
                    focusView = mPasswordEditText;
                }

                // either fields is empty
                if (inputEmail.length() == 0) {
                    mUsernameEditText.setError(getString(R.string.required_field));
                    focusView = mUsernameEditText;
                }
                if (inputPassword.length() == 0) {
                    mPasswordEditText.setError(getString(R.string.required_field));
                    focusView = mPasswordEditText;
                }

                // -> something is invalid
                if (focusView != null) {
                    focusView.requestFocus();
                    return;
                }

                // valid entries //

                if (verifyCredentials(inputEmail, inputPassword)) {
                    // show progress bar & hide buttons
                    mProgressBar.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.GONE);

                    // let progress bar spin to emulate network query
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // proceed to main activity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }, 1000 + (int) (Math.random() * 1000));    // delay 1+ sec
                    // after delay, proceed to main activity
                }
                // input is valid but does not match the records
                else {
                    // show progress bar & hide buttons
                    mProgressBar.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.GONE);
                    signUpButton.setVisibility(View.GONE);

                    // let progress bar spin to emulate network query
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // inform user they are wrong
                            Toast message = Toast.makeText(LoginActivity.this, R.string.login_failed,
                                    Toast.LENGTH_SHORT);
                            message.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            message.show();

                            // hide progress bar & show buttons
                            mProgressBar.setVisibility(View.GONE);
                            signInButton.setVisibility(View.VISIBLE);
                            signUpButton.setVisibility(View.VISIBLE);
                        }
                    }, 1000 + (int) (Math.random() * 1000));    // delay 1+ sec
                    // after delay, toast user
                }
            }
        });

        // set the OnClickListener for the sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Const.TAG, "onClick: sign up");

                // proceed to RegisterActivity
                Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                intent.putExtra(Const.SOURCE_TAG, TAG);
                // add email to intent
                intent.putExtra(Const.USER_ID_KEY,mUsernameEditText.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set errors to null
        mUsernameEditText.setError(null);
        mPasswordEditText.setError(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // get input email & password
        String inputEmail = mUsernameEditText.getText().toString();
        String inputPassword = mPasswordEditText.getText().toString();
        // save input email & password
        if (!inputEmail.equals("")) outState.putString(Const.USER_ID_KEY,inputEmail);
        if (!inputPassword.equals("")) outState.putString(Const.USER_PASSWORD_KEY,inputPassword);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get input email & password if entered
        mUsernameEditText.setText(savedInstanceState.getString(Const.USER_ID_KEY));
        mPasswordEditText.setText(savedInstanceState.getString(Const.USER_PASSWORD_KEY));
    }

    // check if inputs match the records
    private boolean verifyCredentials(String email, String password) {
        //mAuth.verifyEmailandPasswordMatchRecords()
        return false;
    }
}
