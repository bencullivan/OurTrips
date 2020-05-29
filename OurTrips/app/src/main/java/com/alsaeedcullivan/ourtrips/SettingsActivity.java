package com.alsaeedcullivan.ourtrips;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // boiler plate for Settings Activity
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // set on click listener edit profile button
            PreferenceScreen editProfile = getPreferenceManager()
                    .findPreference("edit_profile_settings");
            Objects.requireNonNull(editProfile)
                    .setOnPreferenceClickListener(createEditProfileListener());

            // set on click listener on sign out button
            PreferenceScreen logout = getPreferenceManager()
                    .findPreference("sign_out");
            Objects.requireNonNull(logout)
                    .setOnPreferenceClickListener(createSignOutListener());
        }

        // clicking edit profile takes the user to register activity
        private Preference.OnPreferenceClickListener createEditProfileListener() {
            return new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // clicking edit profile takes the user to register activity
                    // proceed to register activity
                    if (getActivity() == null) return false;
                    Intent editIntent = new Intent(getActivity(), RegisterActivity.class);
                    editIntent.putExtra(Const.SOURCE_TAG, Const.SETTINGS_TAG);
                    startActivity(editIntent);
                    return false;
                }
            };
        }

        // clicking sign out takes user back to Login Activity
        private Preference.OnPreferenceClickListener createSignOutListener() {
            return new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    if (getActivity() == null) return false;

                    // log the user out of firebase
                    FirebaseAuth.getInstance().signOut();

                    // send user back to login
                    requireContext().startActivity(new Intent(getActivity(),
                            LoginActivity.class).putExtra(Const.SOURCE_TAG, Const.SETTINGS_TAG));
                    // finish this activity and others in stack -> user logged out
                    getActivity().finishAffinity();
                    return false;
                }
            };
        }
    }
}