package com.alsaeedcullivan.ourtrips.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alsaeedcullivan.ourtrips.FriendActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.RegisterActivity;

/**
 * custom implementation of DialogFragment
 */
public class CustomDialogFragment extends DialogFragment {

    // public constants
    public static final String TAG = "dialog_tag";
    public static final int PERMISSION_IMPORTANT_ID = 0;
    public static final int FRIEND_ID = 1;
    public static final int ACCEPT_ID = 2;

    // private constants
    private static final String KEY_ID = "key_id";
    private static final String PERMISSION_IMPORTANT_TITLE = "Permission Important";
    private static final String FRIEND_TITLE = "Enter a friend's email";
    private static final String REQUEST_TITLE = "Friend Request";


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
            case FRIEND_ID:
                return createFriendDialog();
            case ACCEPT_ID:
                return createAcceptDialog();
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

    // sends a friend request
    private AlertDialog createFriendDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder((getActivity()));
        // set appropriate title
        dialog.setTitle(FRIEND_TITLE);
        final View dialogView = View.inflate(getContext(), R.layout.text_input, null);
        dialog.setView(dialogView);
        // set button
        dialog.setPositiveButton("Send request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText email = dialogView.findViewById(R.id.dialog_text_input);
                // send the request
                if (getActivity() != null) ((FriendActivity) getActivity())
                        .sendRequest(email.getText().toString());
                dismiss();
            }
        });
        return dialog.create();
    }

    // allows user to accept friend request
    private AlertDialog createAcceptDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder((getActivity()));
        // set the title
        dialog.setTitle(REQUEST_TITLE);

        final String email;
        final int position;
        // set the message
        if (getActivity() != null) {
            FriendActivity activity = (FriendActivity) getActivity();
            email = activity.getSelectedEmail();
            position = activity.getSelectedIndex();
            dialog.setMessage(email + " has sent you a friend request!");
        } else {
            email = "";
            position = 0;
            dialog.setMessage("You have received a friend request");
        }
        // set buttons
        dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) {
                    ((FriendActivity) getActivity()).acceptRequest(email);
                    ((FriendActivity) getActivity()).removeRequest(position);
                }
            }
        }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) {
                    ((FriendActivity) getActivity()).removeRequest(position);
                }
            }
        });
        return dialog.create();
    }
}
