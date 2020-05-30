package com.alsaeedcullivan.ourtrips.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alsaeedcullivan.ourtrips.EditSummaryActivity;
import com.alsaeedcullivan.ourtrips.FriendActivity;
import com.alsaeedcullivan.ourtrips.MainActivity;
import com.alsaeedcullivan.ourtrips.MapsActivity;
import com.alsaeedcullivan.ourtrips.MatchOrAddActivity;
import com.alsaeedcullivan.ourtrips.R;
import com.alsaeedcullivan.ourtrips.RegisterActivity;
import com.alsaeedcullivan.ourtrips.RequestTripActivity;
import com.alsaeedcullivan.ourtrips.SettingsActivity;
import com.alsaeedcullivan.ourtrips.TripActivity;
import com.alsaeedcullivan.ourtrips.ViewPictureActivity;
import com.alsaeedcullivan.ourtrips.models.UserSummary;

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
    public static final int SEARCH_TRIP_ID = 7;
    public static final int SUMMARY_END_DATE_ID = 10;
    public static final int LOCATION_SETTINGS_ID = 11;
    public static final int LOCATION_REQUIRED_ID = 12;
    public static final int ADD_LOCATION_ID = 13;
    public static final int DELETE_TRIP_ID = 14;
    public static final int ADD_TRIPPER_ID = 15;
    public static final int REMOVE_USER_TRIP_ID = 16;
    public static final int DELETE_PROFILE_ID = 17;
    public static final int AUTHENTICATE_ID = 18;
    public static final int RECOGNIZE_LOC_ID = 19;

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
        if (getActivity() == null) return new AlertDialog.Builder(getContext()).create();

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
            case SEARCH_TRIP_ID:
                return searchTripDialog();
            case SUMMARY_END_DATE_ID:
                return createSummaryEndDateDialog();
            case LOCATION_SETTINGS_ID:
                return createLocationSettingsDialog();
            case LOCATION_REQUIRED_ID:
                return createLocationRequiredDialog();
            case ADD_LOCATION_ID:
                return addLocationDialog();
            case DELETE_TRIP_ID:
                return deleteTripDialog();
            case ADD_TRIPPER_ID:
                return addTripperDialog();
            case REMOVE_USER_TRIP_ID:
                return removeUserTripDialog();
            case DELETE_PROFILE_ID:
                return deleteProfileDialog();
            case AUTHENTICATE_ID:
                return authenticateDialog();
            case RECOGNIZE_LOC_ID:
                return recognizeLocationDialog();
        }

        // if a dialog has not been returned, return an alert dialog
        return new AlertDialog.Builder(getActivity()).create();
    }

    // permission is important
    private AlertDialog createImportantDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title
        dialog.setTitle("Find Match");

        if (getActivity() != null) {
            MatchOrAddActivity activity = (MatchOrAddActivity) getActivity();
            UserSummary friend = activity.getFriend();
            if (friend == null) return dialog.create();
            // set the message
            dialog.setMessage("Find out which dates you and " + friend.getName() + " are both " +
                    "available to go on a trip.");
            // set the buttons
            dialog.setPositiveButton("Match", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() == null) return;
                    ((MatchOrAddActivity) getActivity()).onMatchClicked();
                    dismiss();
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
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
                    ((MatchOrAddActivity) getActivity()).searchByEmail(input.getText().toString());
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
                    ((MatchOrAddActivity) getActivity()).searchByName(input.getText().toString());
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
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

    // returns a dialog that allows the user to search for a trip based on the title
    private AlertDialog searchTripDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set title
        dialog.setTitle(R.string.enter_trip_title);
        final View dialogView = View.inflate(getContext(), R.layout.text_input, null);
        dialog.setView(dialogView);

        dialog.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = dialogView.findViewById(R.id.dialog_text_input);
                if (getActivity() != null) {
                    // perform search
                    ((MainActivity) getActivity()).search(input.getText().toString());
                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        return dialog.create();
    }

    // creates a dialog that allows the user to select the end date of the trip
    private DatePickerDialog createSummaryEndDateDialog() {
        // get calendar instance
        final Calendar calendar = Calendar.getInstance();
        if (getActivity() != null) {
            Date start = ((EditSummaryActivity) getActivity()).getEndDate();
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
                    if (calendar.getTime().compareTo(((EditSummaryActivity) getActivity()).getStartDate()) < 0) {
                        dismiss();
                        ((EditSummaryActivity) getActivity()).endBeforeStart();
                    } else ((EditSummaryActivity) getActivity()).updateEndDate(calendar.getTime());
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    // creates a dialog that offers to take the user to settings
    private AlertDialog createLocationSettingsDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title
        dialog.setTitle(R.string.permission_denied);
        // set the message
        dialog.setMessage(R.string.location_settings);
        dialog.setPositiveButton(R.string.go_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) ((MapsActivity) getActivity()).goToSettings();
                dismiss();
            }
        }).setNegativeButton(R.string.prompt_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) getActivity().finish();
                dialog.dismiss();
            }
        });
        return dialog.create();
    }

    // creates a dialog that tells the user that location is required
    private AlertDialog createLocationRequiredDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set title
        dialog.setTitle(R.string.permission_required);
        // set the message
        dialog.setMessage(R.string.enable_location);
        dialog.setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) ((MapsActivity) getActivity()).checkPermission();
                dismiss();
            }
        }).setNegativeButton(R.string.prompt_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() != null) getActivity().finish();
                dialog.dismiss();
            }
        });
        return dialog.create();
    }

    // creates a dialog that allows the user to add a location
    private AlertDialog addLocationDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title
        dialog.setTitle(R.string.enter_name_location);
        final View dialogView = View.inflate(getContext(), R.layout.text_input, null);
        dialog.setView(dialogView);

        dialog.setPositiveButton("Use Current Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = dialogView.findViewById(R.id.dialog_text_input);
                if (getActivity() != null) {
                    // save the location name
                    ((MapsActivity) getActivity()).setLocationName(input.getText().toString());
                    // put the map in add mode
                    ((MapsActivity) getActivity()).useCurrent();
                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        dialog.setNegativeButton("Select Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = dialogView.findViewById(R.id.dialog_text_input);
                if (getActivity() != null) {
                    // save the location name
                    ((MapsActivity) getActivity()).setLocationName(input.getText().toString());
                    // put the map in add mode
                    ((MapsActivity) getActivity()).initiateSelect();
                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        return dialog.create();
    }

    // asks the user if they are sure they want to delete this trip
    private AlertDialog deleteTripDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title and message
        dialog.setTitle(R.string.are_you_sure);
        dialog.setMessage(R.string.non_reversible);
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() == null) return;
                ((TripActivity)getActivity()).deleteTrip();
                dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return dialog.create();
    }

    // asks the user if they want to add this friend as a tripper
    private AlertDialog addTripperDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title and message
        dialog.setTitle("Add Tripper");
        dialog.setMessage("Click \"Add\" to add this friend to the trip");
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() == null) return;
                ((MatchOrAddActivity) getActivity()).onAddClicked();
                dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return dialog.create();
    }

    // asks the user if they want to remove the trip they just clicked
    private AlertDialog removeUserTripDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title and message
        dialog.setTitle(R.string.are_you_sure);
        dialog.setMessage(R.string.remove_self_from_trip);
        // set buttons
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() == null) return;
                ((MainActivity) getActivity()).removeTrip();
                dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return dialog.create();
    }

    // asks the user if they are sure they want to delete their profile
    private AlertDialog deleteProfileDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set the title and message
        dialog.setTitle(R.string.are_you_sure);
        dialog.setMessage(R.string.are_you_sure_profile);
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() == null) return;
                ((SettingsActivity) getActivity()).createAuthentication();
                dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return dialog.create();
    }

    // prompts the user to enter their email and password
    private AlertDialog authenticateDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        // set title and view
        dialog.setTitle(R.string.enter_creds);
        final View dialogView = View.inflate(getContext(), R.layout.double_input, null);
        dialog.setView(dialogView);

        dialog.setPositiveButton("Delete Profile Permanently", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input1 = dialogView.findViewById(R.id.dialog_text_input1);
                EditText input2 = dialogView.findViewById(R.id.dialog_text_input2);
                if (getActivity() != null) {
                    ((SettingsActivity) getActivity()).authenticate(input1.getText().toString(),
                            input2.getText().toString());

                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(input2.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        return dialog.create();
    }

    // returns a dialog that informs the user of the location that was recognized
    private AlertDialog recognizeLocationDialog() {
        // create alert dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogInput);
        if (getActivity() == null) return dialog.create();

        // set the title and set up the view with the data of the location that was recognized
        dialog.setTitle("Recognized Location");
        View dialogView = View.inflate(getContext(), R.layout.recognize_dialog, null);
        dialog.setView(dialogView);
        TextView name = dialogView.findViewById(R.id.recognized_name);
        TextView confidence = dialogView.findViewById(R.id.recognized_confidence);
        String locName = ((ViewPictureActivity)getActivity()).getLocationName();
        if (locName == null) locName = "";
        Float floatConfidence = ((ViewPictureActivity)getActivity()).getLocationConfidence();
        if (floatConfidence == null) floatConfidence = (float) 0.0;
        String locConfidence = "Confidence: " + floatConfidence;
        name.setText(locName);
        confidence.setText(locConfidence);

        // set listeners
        dialog.setPositiveButton("Add to map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() == null) return;



                dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return dialog.create();
    }
}
