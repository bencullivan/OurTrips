package com.alsaeedcullivan.ourtrips.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.RegisterActivity;

/**
 * custom implementation of DialogFragment
 */
public class CustomDialogFragment extends DialogFragment {

    // public constants
    public static final int PERMISSION_IMPORTANT_ID = 0;

    // private constants
    private static final String KEY_ID = "key_id";
    private static final String PERMISSION_IMPORTANT_TITLE = "Permission Important";

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
            // permission is important
            case PERMISSION_IMPORTANT_ID:
                return createImportantDialog();

        }

        // if a dialog has not been returned, return an alert dialog
        return new AlertDialog.Builder(getActivity()).create();
    }

    // camera permission is important
    private AlertDialog createImportantDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder((getActivity()));
        // set appropriate title
        dialog.setTitle(PERMISSION_IMPORTANT_TITLE);
        // set message
        dialog.setMessage(R.string.message_important_access);
        dialog.setPositiveButton(R.string.prompt_grantAccess, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ((RegisterActivity) requireActivity()).requestPermission();
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.prompt_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog.create();
    }
}
