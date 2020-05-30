package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.cloud.AccessBucket;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.glide.GlideApp;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.alsaeedcullivan.ourtrips.utils.SharedPreference;
import com.alsaeedcullivan.ourtrips.utils.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * This activity uses the Android Image Cropper open source library
 * Android Image Cropper is licensed under the Apache License, Version 2.0
 * Android Image Cropper can be found on github at https://github.com/ArthurHub/Android-Image-Cropper
 * The library has been in no way modified, we merely implement it in this activity for photo cropping
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String URI_KEY = "uri_key";
    private static final String GLIDE_KEY = "glide_key";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // widgets
    private ImageView mProfileImageView;
    private EditText mNameEditText, mEmailEditText, mAffiliationEditText, mBirthdayEditText, mBioEditText;
    private RadioGroup mInputGender;
    private RadioButton mFemaleRadioButton, mMaleRadioButton, mOtherRadioButton;

    private Uri mProfileUri;
    private String mGlidePath;
    private String mOldPath;
    private HashMap<String, Object> mData;
    private InputStream mIs;
    private String mNewPath;

    private boolean mPermission;
    private boolean mRegistered;

    public String mSourceExtra;   // source activity intent extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize the FireBaseAuth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // get reference to the ImageView
        mProfileImageView = findViewById(R.id.img_profile);

        // get references to the EditTexts & Radios
        mNameEditText = findViewById(R.id.edit_name);
        mEmailEditText = findViewById(R.id.edit_email);
        mAffiliationEditText = findViewById(R.id.edit_affiliation);
        mBirthdayEditText = findViewById(R.id.edit_birthday);
        mInputGender = findViewById(R.id.input_gender);
        mFemaleRadioButton = findViewById(R.id.edit_gender_female);
        mMaleRadioButton = findViewById(R.id.edit_gender_male);
        mOtherRadioButton = findViewById(R.id.edit_gender_other);
        mBioEditText = findViewById(R.id.edit_bio);

        // display the user's email as uneditable
        mEmailEditText.setText(mUser.getEmail());
        mEmailEditText.setEnabled(false);

        // get source activity
        mSourceExtra = getIntent().getStringExtra(Const.SOURCE_TAG);

        // if there is a picture uri, (new pic) restore it and set the pic
        if (savedInstanceState != null && savedInstanceState.getParcelable(URI_KEY) != null) {
            Log.d(Const.TAG, "onCreate: new pic");
            mProfileUri = savedInstanceState.getParcelable(URI_KEY);
            setPic(mProfileUri);
        } else if (mSourceExtra != null) {
            // if the profile has already been loaded
            if (mSourceExtra.equalsIgnoreCase(Const.SETTINGS_TAG) && savedInstanceState != null &&
                    savedInstanceState.getString(GLIDE_KEY) != null) {
                Log.d(Const.TAG, "onCreate: file glide");
                mNameEditText.setEnabled(false);
                // load the profile pic
                mGlidePath = savedInstanceState.getString(GLIDE_KEY);
                StorageReference ref = FirebaseStorage.getInstance().getReference(mGlidePath);
                Log.d(Const.TAG, "onCreate: " + ref);
                GlideApp.with(this).load(ref).into(mProfileImageView);
            }
            // the profile needs to be loaded
            else if (mSourceExtra.equalsIgnoreCase(Const.SETTINGS_TAG)) {
                Log.d(Const.TAG, "onCreate: load");
                mNameEditText.setEnabled(false);
                new LoadProfileTask().execute();
            }
        }

        // request read/write/camera permissions
        updatePermission();
        requestPermission();
    }

    // handle lifecycle //

    @Override
    protected void onResume() {
        super.onResume();
        // update permissions
        updatePermission();
        // get the user that is signed in or null if there is no user signed in
        mUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save picture uri
        if (mProfileUri != null) outState.putParcelable(URI_KEY, mProfileUri);
        if (mGlidePath != null) outState.putString(GLIDE_KEY, mGlidePath);
    }

    // handle permissions //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Const.GALLERY_PERMISSION_REQUEST_CODE) {
            // check permissions status from dialog fragment & Manifest
            if (grantResults.length == 3 && (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] !=
                    PackageManager.PERMISSION_GRANTED) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            shouldShowRequestPermissionRationale(Manifest.permission
                                    .WRITE_EXTERNAL_STORAGE)) {
                // permission is important
                createImportantDialogFragment();
            } else {
                updatePermission();
            }
        }
    }

    // handle menu //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        // enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // check source activity & set-up title & visible menu button accordingly
        if (mSourceExtra != null && mSourceExtra.equals(Const.VERIFY_TAG)) {
            menu.findItem(R.id.register_button).setVisible(true);
            menu.findItem(R.id.update_button).setVisible(false);
            // set up activity title
            setTitle(getString(R.string.title_activity_register));
            // user has not previously registered
            mRegistered = false;
        } else if (mSourceExtra != null && mSourceExtra.equals(Const.SETTINGS_TAG)) {
            menu.findItem(R.id.register_button).setVisible(false);
            menu.findItem(R.id.update_button).setVisible(true);
            // set up activity title
            setTitle(getString(R.string.title_activity_edit));
            // disable editing email
            mEmailEditText.setEnabled(false);
            // user is registered
            mRegistered = true;
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
        // if the user has given permission
        // select the pic
        if (mPermission) selectPic();
        else createSettingsDialogFragment();
    }

    // handles Register & Update Profile clicks
    public void onRegisterClicked() {
        removeErrors();

        // get name, gender, email, affiliation & birthday
        String inputName = mNameEditText.getText().toString();
        String inputEmail = mEmailEditText.getText().toString();
        String inputAffiliation = mAffiliationEditText.getText().toString();
        String inputBirthday = mBirthdayEditText.getText().toString();
        int inputGender = -1;
        if (mMaleRadioButton.isChecked()) inputGender = 1;
        else if (mFemaleRadioButton.isChecked()) inputGender = 0;
        else if (mOtherRadioButton.isChecked()) inputGender = 2;

        // issues -> false --> NOT save & inform user
        boolean goodProfile = true;
        // focus view on incorrect field
        View focusView = null;

        // ensure email validity //
        if (!Utilities.isValidEmail(inputEmail)) {
            mEmailEditText.setError(getString(R.string.invalid_username));
            focusView = mEmailEditText;
            goodProfile = false;
        }

        // ensure name, gender, email & birthday NOT empty
        if (inputName.length() == 0) {
            mEmailEditText.setError(getString(R.string.required_field));
            focusView = mEmailEditText;
            goodProfile = false;
        }
        if (inputEmail.length() == 0) {
            mEmailEditText.setError(getString(R.string.required_field));
            focusView = mEmailEditText;
            goodProfile = false;
        }
        if (inputBirthday.length() == 0) {
            mBirthdayEditText.setError(getString(R.string.required_field));
            focusView = mBirthdayEditText;
            goodProfile = false;
        }
        if (goodProfile && inputGender == -1) {
            Toast message = Toast.makeText(this, R.string.required_gender,
                    Toast.LENGTH_SHORT);
            message.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            message.show();
            focusView = mInputGender;
            goodProfile = false;
        }

        // field has error -> focus
        if (focusView != null) focusView.requestFocus();

        // no errors triggered -> save profile
        if (goodProfile) {// save || update profile

            // create || update profile
            if (mRegistered) updateProfile();
            else createUser();
        }
    }

    // handle setting profile pic //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the activity was an image crop
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // get the result of the Crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                // get the Uri of the cropped pic
                if (result != null){
                    mProfileUri = result.getUri();
                    setPic(mProfileUri);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // get the error
                Exception error = null;
                if (result != null) error = result.getError();
                if (error != null)
                    Log.d(Const.TAG, "onActivityResult: " + Log.getStackTraceString(error));
            }
        }
    }

    //  ******************************* private helper methods ******************************* //

    // handle picture //

    /**
     * selectPic()
     * allows the user to select a picture from their gallery and crop it
     */
    private void selectPic() {
        // start picker to get the image for cropping and then use the result
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .start(this);
    }

    /**
     * setPic()
     * set a picture on ImageView
     */
    private void setPic(Uri uri) {
        try {
            // open an InputStream from the Uri
            InputStream is = getContentResolver().openInputStream(uri);
            Drawable profilePic = Drawable.createFromStream(is, uri.toString());
            mProfileImageView.setImageDrawable(profilePic);
            mGlidePath = null;
        } catch (IOException e) {
            Log.d(Const.TAG, Log.getStackTraceString(e));
        }
    }

    // handle permissions //

    // public: accessed from CustomDialogFragment
    public void requestPermission() {
        // request read/write permissions from user if not given
        if (!mPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    Const.GALLERY_PERMISSION_REQUEST_CODE);
        }
    }

    // updates user permissions
    private void updatePermission() {
        // update camera and read & write permissions
        mPermission = (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) &&
                        checkSelfPermission(Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED;
    }

    // takes the user to device settings for this app
    public void goToSettings() {
        getApplicationContext().startActivity(new Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", this.getPackageName(), null)));
    }

    // handle dialog fragments //

    private void createImportantDialogFragment() {
        // display dialog fragment
        DialogFragment dialog = CustomDialogFragment.newInstance(CustomDialogFragment
                .PERMISSION_IMPORTANT_ID);
        dialog.show(getSupportFragmentManager(), Const.DIALOG_TAG);
    }

    private void createSettingsDialogFragment() {
        // display dialog fragment
        DialogFragment dialog = CustomDialogFragment.newInstance(CustomDialogFragment
                .SETTINGS_DIALOG_ID);
        dialog.show(getSupportFragmentManager(), Const.DIALOG_TAG);
    }

    // handle profile tasks //

    /**
     * removeErrors()
     * method to remover errors from required text widgets: name, email & birthday
     */
    private void removeErrors() {
        mNameEditText.setError(null);
        mEmailEditText.setError(null);
        mBirthdayEditText.setError(null);
    }

    /**
     * createUser()
     * method to add this user to FireStore
     */
    private void createUser() {
        // create a map with the user's information
        mData = new HashMap<>();
        mData.put(Const.USER_ID_KEY, mUser.getUid());
        mData.put(Const.USER_EMAIL_KEY, mUser.getEmail());
        mData.put(Const.USER_NAME_KEY, mNameEditText.getText().toString());
        mData.put(Const.USER_BIRTHDAY_KEY, mBirthdayEditText.getText().toString());
        String gender;
        int checked = mInputGender.getCheckedRadioButtonId();
        if (checked == R.id.edit_gender_female) gender = "f";
        else if (checked == R.id.edit_gender_male) gender = "m";
        else gender = "o";
        mData.put(Const.USER_GENDER_KEY, gender);
        mData.put(Const.USER_AFFILIATION_KEY, mAffiliationEditText.getText().toString());
        mData.put(Const.USER_BIO_KEY, mBioEditText.getText().toString());
        mData.put(Const.DATE_LIST_KEY, new ArrayList<String>());

        // if they have set a profile pic
        if (mProfileUri != null) {
            // establish the path where the profile pic will be stored in the bucket
            // include timestamp (so glide does not load the incorrect image)
            mNewPath = Const.PROFILE_PIC_PATH + "/" + mUser.getUid() + "/" + Const.PROFILE_PIC_NAME
                    + new Date().getTime() + Const.PIC_JPG;
            try {
                // open an input stream from the photo Uri and upload to the bucket
                mIs = getContentResolver().openInputStream(mProfileUri);
                // update the photo in the storage bucket
                new ProfilePicTask().execute();
                // add the storage path to the data object
                mData.put(Const.USER_PROFILE_PIC_KEY, mNewPath);
            } catch (IOException e) {
                Log.d(Const.TAG, Log.getStackTraceString(e));
            }
        }
        // give this new user an entry in the db
        new NewUserTask().execute();
    }

    /**
     * updateProfile()
     * method to update a user's profile info in the FireStore database
     */
    private void updateProfile() {
        // create a map with the new profile info
        mData = new HashMap<>();
        mData.put(Const.USER_NAME_KEY, mNameEditText.getText().toString());
        mData.put(Const.USER_BIRTHDAY_KEY, mBirthdayEditText.getText().toString());
        String gender;
        int checked = mInputGender.getCheckedRadioButtonId();
        if (checked == R.id.edit_gender_female) gender = "f";
        else if (checked == R.id.edit_gender_male) gender = "m";
        else gender = "o";
        mData.put(Const.USER_GENDER_KEY, gender);
        mData.put(Const.USER_AFFILIATION_KEY, mAffiliationEditText.getText().toString());
        mData.put(Const.USER_BIO_KEY, mBioEditText.getText().toString());

        // if they have set a profile pic
        if (mProfileUri != null) {
            // establish the path where the profile pic will be stored in the bucket
            // include timestamp (so glide does not load the incorrect image)
            mNewPath = Const.PROFILE_PIC_PATH + "/" + mUser.getUid() + "/" + Const.PROFILE_PIC_NAME +
                    new Date().getTime() + Const.PIC_JPG;
            try {
                // open an input stream from the photo Uri and upload to the bucket
                mIs = getContentResolver().openInputStream(mProfileUri);
                // update the photo in the storage bucket
                new ProfilePicTask().execute();
                // add the storage path to the data object
                mData.put(Const.USER_PROFILE_PIC_KEY, mNewPath);
            } catch (IOException e) {
                Log.d(Const.TAG, Log.getStackTraceString(e));
            }
        }
        // update this user's data in the db
        new UpdateUserTask().execute();
    }

    /**
     * populateFields()
     * helper method to fill in the widgets with the user's profile info
     *
     * @param data the object containing all the user's profile data
     */
    private void populateFields(Map<String, Object> data) {
        if (data == null) return;

        // set the profile pic
        mGlidePath = (String) data.get(Const.USER_PROFILE_PIC_KEY);
        mOldPath = mGlidePath;
        if (mGlidePath != null && mGlidePath.length() > 0) {
            // load the profile picture
            GlideApp.with(this)
                    .load(FirebaseStorage.getInstance().getReference(mGlidePath))
                    .into(mProfileImageView);
        }

        // set the name
        String name = (String) data.get(Const.USER_NAME_KEY);
        if (name != null) mNameEditText.setText(name);

        // set the affiliation
        String aff = (String) data.get(Const.USER_AFFILIATION_KEY);
        if (aff != null) mAffiliationEditText.setText(aff);

        // set the birthday
        String bDay = (String) data.get(Const.USER_BIRTHDAY_KEY);
        if (bDay != null) mBirthdayEditText.setText(bDay);

        // set the bio
        String bio = (String) data.get(Const.USER_BIO_KEY);
        if (bio != null) mBioEditText.setText(bio);

        // set the gender
        String gender = (String) data.get(Const.USER_GENDER_KEY);
        if (gender != null) {
            switch (gender) {
                case "f":
                    mInputGender.check(R.id.edit_gender_female);
                    break;
                case "m":
                    mInputGender.check(R.id.edit_gender_male);
                    break;
                default:
                    mInputGender.check(R.id.edit_gender_other);
            }
        }
    }

    /**
     * LoadProfileTask
     * loads a user's profile from the db
     */
    private class LoadProfileTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null) return null;

            // load the data if it exists
            AccessDB.loadUserProfile(mUser.getUid())
                    .addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                        @Override
                        public void onComplete(@NonNull Task<Map<String, Object>> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                populateFields(task.getResult());
                            }
                        }
                    });

            return null;
        }
    }

    /**
     * NewUserTask
     * adds a new user to the db
     */
    private class NewUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mData == null) return null;

            // add the user's data to the database
            AccessDB.addNewUser(mUser.getUid(), mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // set the user as registered
                        new SharedPreference(getApplicationContext()).setRegistered(true);

                        // Inform the user that they were registered successfully
                        Toast t = Toast.makeText(RegisterActivity.this,
                                R.string.string_register_success, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();

                        // send the user to MainActivity
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra(Const.SOURCE_TAG, Const.REGISTER_TAG);
                        startActivity(intent);
                        // at this point the async task has finished executing and it is okay to
                        // finish the activity
                        finish();
                    } else {
                        // inform that their data could not be added
                        Toast t = Toast.makeText(RegisterActivity.this,
                                R.string.string_register_failure, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });

            return null;
        }
    }

    /**
     * UpdateUserTask
     * updates a user in the db
     */
    private class UpdateUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mUser == null || mData == null) return null;

            // update the profile in the database
            AccessDB.updateUserProfile(mUser.getUid(), mData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // inform the user that their profile has been updated
                        Toast t = Toast.makeText(RegisterActivity.this,
                                R.string.string_update_profile, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                        // finish the activity
                        finish();
                    } else {
                        // inform that their data could not be updated
                        Toast t = Toast.makeText(RegisterActivity.this,
                                R.string.update_profile_fail, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });

            return null;
        }
    }

    /**
     * ProfilePicTask
     * uploads a profile pic to the storage bucket and deletes the old one if it exists
     */
    private class ProfilePicTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mIs == null || mNewPath == null) return null;

            // add the new picture to the storage bucket
            AccessBucket.uploadPicture(mNewPath, mIs);
            // if there is an old picture, delete it from the storage bucket
            if (mOldPath != null) AccessBucket.deleteFromStorage(mOldPath);

            return null;
        }
    }
}
