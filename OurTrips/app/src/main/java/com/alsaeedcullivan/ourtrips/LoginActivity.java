package com.alsaeedcullivan.ourtrips;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "test_tag";

    private FirebaseAuth mAuth;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // create a progress bar
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // create the login button
        final Button loginButton = findViewById(R.id.login);

        // initialize the FireBaseAuth
        mAuth = FirebaseAuth.getInstance();

        // get references to the text widgets
        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);

        // set the OnClickListener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "onClick: ");
                createUser(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());
            }
        });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: successfully registered!");
                } else {
                    Log.d(TAG, "onComplete: email was not valid");
                }
            }
        });
    }
}
