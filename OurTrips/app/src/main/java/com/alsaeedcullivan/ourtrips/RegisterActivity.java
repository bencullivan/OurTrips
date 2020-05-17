package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView mProfileImageView;
    private EditText mNameEditText, mEmailEditText, mPasswordEditText, mAffiliationEditText, mBirthdayEditText;
    private RadioGroup mInputGender;
    private RadioButton mFemaleRadioButton, mMaleRadioButton, mOtherRadioButton;
    private Button mChangePictureButton;

    private boolean mPermission;

    public String mSourceExtra;   // source activity intent extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize the FireBaseAuth
        mAuth = FirebaseAuth.getInstance();

        // get reference to the ImageView
        mProfileImageView = findViewById(R.id.img_profile);

        // get references to the EditTexts & Radios
        mNameEditText = findViewById(R.id.edit_name);
        mEmailEditText = findViewById(R.id.edit_email);
        mPasswordEditText = findViewById(R.id.edit_password);
        mAffiliationEditText = findViewById(R.id.edit_affiliation);
        mBirthdayEditText = findViewById(R.id.edit_birthday);
        mInputGender = findViewById(R.id.input_gender);
        mFemaleRadioButton = findViewById(R.id.edit_gender_female);
        mMaleRadioButton = findViewById(R.id.edit_gender_male);
        mOtherRadioButton = findViewById(R.id.edit_gender_other);

        // get reference to the change picture Button
        mChangePictureButton = findViewById(R.id.change_picture);

        // get source activity & load accordingly
        mSourceExtra = getIntent().getStringExtra(Const.SOURCE_TAG);
        if (mSourceExtra != null) {
            if (mSourceExtra.equalsIgnoreCase(MainActivity.TAG)) {// load profile
                loadProfile();
            } else {// load email from LoginActivity
                mEmailEditText.setText(getIntent().getStringExtra(Const.USER_ID_KEY));
            }
        }

        // request read/write permissions
        updatePermission();
        requestPermission();
    }

    // handle permissions //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Const.GALLERY_PERMISSION_REQUEST_CODE) {
            // check permissions status from dialog fragment & Manifest
            if (grantResults.length > 0 && (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) &&
                    (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                            shouldShowRequestPermissionRationale(Manifest.permission
                                    .WRITE_EXTERNAL_STORAGE))) {
                // keep change picture button disabled
                mChangePictureButton.setClickable(false);
            } else {
                // enable change picture button
                mChangePictureButton.setClickable(true);
            }
        }
    }

    private void requestPermission() {
        // request read/write permissions from user if not given
        if (!mPermission) {
            mChangePictureButton.setClickable(false);   // disable change picture button
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, Const.GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            // make sure change picture button is enabled
            mChangePictureButton.setClickable(true);
        }
    }

    private void updatePermission() {
        // update read & write permissions
        mPermission = (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED);
    }

    // handle menu //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // check source activity & set-up title & visible menu button accordingly
        if (mSourceExtra != null && mSourceExtra.equals(LoginActivity.TAG)) {
            menu.findItem(R.id.register_button).setVisible(true);
            menu.findItem(R.id.update_button).setVisible(false);
            // set up activity title
            setTitle(getString(R.string.title_activity_register));
        } else if (mSourceExtra != null && mSourceExtra.equals(MainActivity.TAG)) {
            menu.findItem(R.id.register_button).setVisible(false);
            menu.findItem(R.id.update_button).setVisible(true);
            // set up activity title
            setTitle(getString(R.string.title_activity_Edit));
            // disable editing email
            mEmailEditText.setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.register_button:
            case R.id.update_button:
                onRegisterClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // on click listeners //

    public void onChangeClicked(View view) {
        // if clicked, read/write permissions granted
        createPhotoPickerDialogFragment();
    }

    private void onRegisterClicked() {
    }

    // handle dialog fragments //

    private void createPhotoPickerDialogFragment() {
        // display photo picker dialog fragment
        DialogFragment dialog = CustomDialogFragment.newInstance(CustomDialogFragment
                .PICTURE_DIALOG_ID);
        dialog.show(getSupportFragmentManager(), Const.DIALOG_TAG);
    }

    private void createPermissionDialogFragment() {
        // display permission dialog fragment
        DialogFragment dialog = CustomDialogFragment.newInstance(CustomDialogFragment
                .GALLERY_PERMISSION_DIALOG_ID);
        dialog.show(getSupportFragmentManager(), Const.DIALOG_TAG);
    }

    // handle tasks //

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

    private void loadProfile() {
        // load data if it exists
    }
}
