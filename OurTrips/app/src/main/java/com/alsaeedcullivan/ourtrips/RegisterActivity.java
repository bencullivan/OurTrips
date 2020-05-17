package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // on click listeners //

    public void onChangeClicked(View view) {
        // check for read/write permissions //
        createPhotoPickerDialogFragment();
    }

    // handle dialog fragment //

    private void createPhotoPickerDialogFragment() {
        // display photo picker dialog fragment
        DialogFragment dialog = CustomDialogFragment.newInstance(CustomDialogFragment
                .PICTURE_DIALOG_ID);
        dialog.show(getSupportFragmentManager(), Const.DIALOG_TAG);
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Const.TAG, "onComplete: successfully registered!");
                        } else {
                            Log.d(Const.TAG, "onComplete: email was not valid");
                        }
                    }
                });
    }
}
