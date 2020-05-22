package com.alsaeedcullivan.ourtrips.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alsaeedcullivan.ourtrips.FriendActivity;
import com.alsaeedcullivan.ourtrips.MatchActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.RegisterActivity;
import com.alsaeedcullivan.ourtrips.RequestTripActivity;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;

import java.util.Calendar;
import java.util.Date;

/**
 * custom implementation of DialogFragment
 */
public class CustomDialogFragment extends DialogFragment {

    // public constants
    public static final String TAG = "dialog_tag";
    public static final int PERMISSION_IMPORTANT_ID = 0;
    public static final int FRIEND_ID = 1;
    public static final int ACCEPT_ID = 2;
    public static final int MATCH_ID = 3;
    public static final int SEARCH_FRIEND_ID = 4;
    public static final int SETTINGS_DIALOG_ID = 5;
    public static final int SELECT_END_DATE_ID = 6;

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
            case MATCH_ID:
                return createMatchDialog();
            case SEARCH_FRIEND_ID:
                return searchFriendDialog();
            case SETTINGS_DIALOG_ID:
                return createSettingsDialog();
            case SELECT_END_DATE_ID:
                return createDateDialog();
        }

        // if a dialog has not been returned, return an alert dialog
        return new AlertDialog.Builder(getActivity()).create();
    }

    // permission is important
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
                if (getActivity() != null) ((RegisterActivity) getActivity()).requestPermission();
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

        final UserSummary friend;
        final int position;
        // set the message
        if (getActivity() != null) {
            FriendActivity activity = (FriendActivity) getActivity();
            friend = activity.getSelectedFriend();
            position = activity.getSelectedIndex();
            dialog.setMessage(friend.getName() + " has sent you a friend request!");
        } else {
            friend = new UserSummary();
            position = 0;
            dialog.setMessage("You have received a friend request");
        }
        // set buttons
        dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) {
                    ((FriendActivity) getActivity()).acceptRequest(friend.getUserId(),
                            friend.getEmail(), friend.getName());
                    ((FriendActivity) getActivity()).removeRequest(position);
                }
            }
        }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) {
                    ((FriendActivity) getActivity()).declineRequest(friend.getUserId());
                    ((FriendActivity) getActivity()).removeRequest(position);
                }
            }
        });
        return dialog.create();
    }

    // allows the user to match dates with a friend
    private AlertDialog createMatchDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder((getActivity()));
        // set the title
        dialog.setTitle("Find Match");

        if (getActivity() != null) {
            MatchActivity activity = (MatchActivity) getActivity();
            UserSummary friend = activity.getFriend();
            // set the message
            dialog.setMessage("Find out which dates you and " + friend.getName() + " are both " +
                    "available to go on a trip.");
            // set the buttons
            dialog.setPositiveButton("Match", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() != null) {
                        ((MatchActivity) getActivity()).onMatchClicked();
                        dismiss();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
        }
        return dialog.create();
    }

    // allows the user to search for a friend
    private AlertDialog searchFriendDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder((getActivity()));
        // set the title
        dialog.setTitle(getString(R.string.search_for_a_friend));
        final View dialogView = View.inflate(getContext(), R.layout.text_input, null);
        dialog.setView(dialogView);

        dialog.setPositiveButton("Search by email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = dialogView.findViewById(R.id.dialog_text_input);
                if (getActivity() != null) {
                    // perform email search
                    ((MatchActivity) getActivity()).searchByEmail(input.getText().toString());
                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                dismiss();
            }
        }).setNegativeButton("Search by name", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = dialogView.findViewById(R.id.dialog_text_input);
                if (getActivity() != null) {
                    // perform name search
                    ((MatchActivity) getActivity()).searchByName(input.getText().toString());
                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                dismiss();
            }
        });
        return dialog.create();
    }

    // creates a dialog that takes the user to settings where they can update their permissions
    private AlertDialog createSettingsDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder((getActivity()));
        // set the title
        dialog.setTitle(R.string.permission_denied);
        // set the message
        dialog.setMessage(R.string.permission_settings);
        dialog.setPositiveButton(R.string.go_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) ((RegisterActivity) getActivity()).goToSettings();
                dismiss();
            }
        }).setNegativeButton(R.string.prompt_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog.create();
    }

    // creates a dialog that allows the user to select the end date of the trip
    private DatePickerDialog createDateDialog() {
        // get calendar instance
        final Calendar calendar = Calendar.getInstance();
        if (getActivity() != null) {
            Date start = ((RequestTripActivity) getActivity()).getDate();
            calendar.setTime(start);
        }
        // return the date picker dialog
        return new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // update the calendar
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (getActivity() != null) {
                    if (calendar.getTime().compareTo(((RequestTripActivity) getActivity()).getStart()) < 0) {
                        dismiss();
                        ((RequestTripActivity) getActivity()).endBeforeStart();
                    } else ((RequestTripActivity) getActivity()).updateEndDate(calendar.getTime());
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }
}
