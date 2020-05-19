package com.alsaeedcullivan.ourtrips;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = "SettingsActivity";

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
            // set on click listener on sign out button
            PreferenceScreen logout = getPreferenceManager()
                    .findPreference(getString(R.string.sign_out));
            Objects.requireNonNull(logout)
                    .setOnPreferenceClickListener(createSignOutListener());
        }

        // clicking sign out takes user back to Login Activity
        private Preference.OnPreferenceClickListener createSignOutListener() {
            return new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // TODO: update user has logged out in FirBase

                    // send user back to login
                    requireContext().startActivity(new Intent(getActivity(),
                            LoginActivity.class).putExtra(Const.SOURCE_TAG, SettingsActivity.TAG));
                    // finish this activity and others in stack -> user logged out
                    requireActivity().finishAffinity();
                    return false;
                }
            };
        }


    }
}