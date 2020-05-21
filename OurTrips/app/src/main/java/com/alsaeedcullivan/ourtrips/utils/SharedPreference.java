package com.alsaeedcullivan.ourtrips.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alsaeedcullivan.ourtrips.R;

/**
 * Class to handle the shared preferences of a user
 */
public class SharedPreference {

    private SharedPreferences mPrefs;

    public SharedPreference(Context c) {
        mPrefs = c.getSharedPreferences(c.getString(R.string.preferences), Context.MODE_PRIVATE);
    }

    public void setRegistered(boolean registered) {
        mPrefs.edit().putBoolean(Const.REGISTER_PROPERTY, registered).apply();
    }

    public boolean getRegistered() {
        return mPrefs.getBoolean(Const.REGISTER_PROPERTY, false);
    }
}
