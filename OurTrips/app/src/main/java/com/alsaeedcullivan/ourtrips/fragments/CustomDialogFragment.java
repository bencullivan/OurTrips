package com.alsaeedcullivan.ourtrips.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * custom implementation of DialogFragment
 */
public class CustomDialogFragment extends DialogFragment {

    // public constants
    public static final int PICTURE_DIALOG_ID = 0;

    // private constants
    private static final String KEY_ID = "key_id";

    /**
     * called in order to create a new instance that has an id associated with a bundle
     * id allows the onCreateDialog method to determine the purpose of the dialog and which
     * type of dialog to return
     */
    public static CustomDialogFragment newInstance(int id) {
        // create a bundle
        Bundle args = new Bundle();
        // add the id to the bundle
        args.putInt(KEY_ID, id);
        // create a new instance
        CustomDialogFragment fragment = new CustomDialogFragment();
        // add the bundle
        fragment.setArguments(args);
        // return the new instance
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int id = -1;
        // get the id that was passed in when the dialog was created
        if (getArguments() != null) id = getArguments().getInt(KEY_ID);

        // based on the id return the appropriate type of dialog
        switch (id) {

        }

        // if a dialog has not been returned, return an alert dialog
        return new AlertDialog.Builder(getActivity()).create();
    }
}
