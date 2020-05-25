package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.adapters.FriendAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.models.UserSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private static final String FRIENDS_KEY = "friends";
    private static final String DATES_KEY = "dates";
    private static final String NAME_KEY = "name";

    private FriendAdapter mAdapter;
    private List<Date> mUserDates;
    private ArrayList<UserSummary> mFriends;
    private String mUserName;
    private FirebaseUser mUser;
    private ListView mListView;
    private UserSummary mSelected;
    private ProgressBar mSpinner;
    private TextView mLoading;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        setTitle("Match Dates");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get widget reference
        mSpinner = findViewById(R.id.match_spinner);
        mLoading = findViewById(R.id.match_loading);
        mLayout = findViewById(R.id.match_layout);

        // set initial visibility
        mLayout.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setText(R.string.loading_friends);
        mLoading.setVisibility(View.VISIBLE);

        // get the user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // get a reference to the ListView
        mListView = findViewById(R.id.match_list);

        if (savedInstanceState != null && savedInstanceState.getLongArray(DATES_KEY) != null &&
                savedInstanceState.getParcelableArrayList(FRIENDS_KEY) != null &&
                savedInstanceState.getString(NAME_KEY) != null) {
            long[] dates = savedInstanceState.getLongArray(DATES_KEY);
            if (dates != null) {
                mUserDates = new ArrayList<>();
                // get the dates
                for (long date : dates) {
                    mUserDates.add(new Date(date));
                }
                // get the name of this user
                mUserName = savedInstanceState.getString(NAME_KEY);
                // initialize the adapter
                mFriends = savedInstanceState.getParcelableArrayList(FRIENDS_KEY);
                mAdapter = new FriendAdapter(this, R.layout.activity_match, new ArrayList<UserSummary>());
                mAdapter.addAll(mFriends);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(getClickListener());
                showList();
            }
        }
        else if (mUser != null) {
            // instantiate the adapter
            mAdapter = new FriendAdapter(this, R.layout.activity_match, new ArrayList<UserSummary>());
            // set the adapter to the ListView
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(getClickListener());
            // load this user's list of friends
            Task<List<UserSummary>> friendTask = AccessDB.getFriendsList(mUser.getUid());
            friendTask.addOnCompleteListener(new OnCompleteListener<List<UserSummary>>() {
                @Override
                public void onComplete(@NonNull Task<List<UserSummary>> task) {
                    if (task.isSuccessful()) {
                        // get the list of friends
                        mFriends = (ArrayList<UserSummary>) task.getResult();
                        // add them to the adapter
                        if (mFriends != null) {
                            mAdapter.addAll(mFriends);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast t = Toast.makeText(MatchActivity.this, "Could not load your friends.",
                                Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });
            // get this user's name from the database
            AccessDB.getUserName(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        mUserName = task.getResult();
                    }
                }
            });
            // load this user's dates from the db
            AccessDB.getUserDatesForCal(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<List<Date>>() {
                @Override
                public void onComplete(@NonNull Task<List<Date>> task) {
                    if (task.isSuccessful()) {
                        mUserDates = task.getResult();
                        showList();
                    } else {
                        Toast t = Toast.makeText(MatchActivity.this, "Could not load your dates.",
                                Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.search_button:
                onSearchClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // make sure the list will be displayed if they hit the back button
        showList();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(Const.TAG, "onSaveInstanceState: " + mFriends);
        if (mFriends != null) outState.putParcelableArrayList(FRIENDS_KEY, mFriends);
        if (mUserDates != null) outState.putLongArray(DATES_KEY, toLongs(mUserDates));
        if (mUserName != null) outState.putString(NAME_KEY, mUserName);
    }

    /**
     * onSearchClicked()
     * called when a user wants to search for a friend
     */
    private void onSearchClicked() {
        // display the search dialog
        CustomDialogFragment.newInstance(CustomDialogFragment.SEARCH_FRIEND_ID)
                .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
    }

    /**
     * searchByName()
     * finds a user with the given name and brings them to the top of the list
     * @param name the name of the friend (not case sensitive)
     */
    public void searchByName(String name) {
        if (mFriends == null || mFriends.size() == 0) return;
        for (int i = 0; i < mFriends.size(); i++) {
            if (mFriends.get(i).getName().toLowerCase().equals(name.toLowerCase())) {
                // swap the person at index 0 with the found friend
                UserSummary found = mFriends.get(i);
                mFriends.set(i, mFriends.get(0));
                mFriends.set(0, found);
                Log.d(Const.TAG, "searchByName: " + mFriends);
                mAdapter.clear();
                mAdapter.addAll(mFriends);
                mAdapter.notifyDataSetChanged();
                Log.d(Const.TAG, "searchByName: " + mAdapter.getCount());
                return;
            }
        }
        Toast t = Toast.makeText(this, "There is no user with the name: " + name,
                Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();
    }

    /**
     * searchByEmail()
     * finds a user with the given email and brings them to the top of the list
     * @param email the email of the friend
     */
    public void searchByEmail(String email) {
        if (mFriends == null || mFriends.size() == 0) return;
        for (int i = 0; i < mFriends.size(); i++) {
            if (mFriends.get(i).getEmail().equals(email)) {
                // swap the person at index 0 with the found friend
                UserSummary found = mFriends.get(i);
                mFriends.set(i, mFriends.get(0));
                mFriends.set(0, found);
                Log.d(Const.TAG, "searchByEmail: " + mFriends);
                mAdapter.clear();
                mAdapter.addAll(mFriends);
                mAdapter.notifyDataSetChanged();
                Log.d(Const.TAG, "searchByEmail: " + mAdapter.getCount());
                return;
            }
        }
        Toast t = Toast.makeText(this, "There is no user with the email: " + email,
                Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();
    }

    /**
     * onMatchClicked()
     * called from a dialog when a user clicks "match"
     */
    public void onMatchClicked() {
        if (mSelected == null) return;
        hideList();
        match(mUserDates, mSelected.getUserId());
    }

    /**
     * match()
     * called when a user selects one of their friends and clicks "match"
     *
     * @param userDates the dates that this user is available
     * @param friendId  the id of the friend that they selected
     */
    private void match(List<Date> userDates, String friendId) {
        // perform a match to see which dates you are both available
        AccessDB.matchDates(userDates, friendId).addOnCompleteListener(new OnCompleteListener<long[]>() {
            @Override
            public void onComplete(@NonNull Task<long[]> task) {
                if (task.isSuccessful()) {
                    long[] matched = task.getResult();
                    if (matched != null && matched.length > 0) {
                        // start CalendarActivity and pass it an array of all the matched dates
                        Intent intent = new Intent(MatchActivity.this, CalendarActivity.class);
                        intent.putExtra(Const.SOURCE_TAG, Const.MATCH_TAG);
                        intent.putExtra(Const.MATCH_ARR_TAG, matched);
                        intent.putExtra(Const.SELECTED_FRIEND_TAG, mSelected);
                        intent.putExtra(Const.USER_NAME_TAG, mUserName);
                        startActivity(intent);
                    } else {
                        // the two users have no dates in common
                        Toast t = Toast.makeText(MatchActivity.this, "You and " +
                                mSelected.getName() + " have no dates in common.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                } else {
                    // unable to be matched
                    Toast t = Toast.makeText(MatchActivity.this, "You and " +
                            mSelected.getName() + " were not able to be matched.", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
            }
        });
    }

    // returns an onClickListener for the list view
    private AdapterView.OnItemClickListener getClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelected = mFriends.get(position);
                // display the match dialog
                CustomDialogFragment.newInstance(CustomDialogFragment.MATCH_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
            }
        };
    }

    // show the list view
    private void showList() {
        mSpinner.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLoading.setText(R.string.loading_friends);
        mLayout.setVisibility(View.VISIBLE);
    }

    // hide the list view and show the progress bar
    private void hideList() {
        mLayout.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setText(R.string.matching);
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * toLongs()
     * Converts a List of Date objects to an array of longs.
     * This will be used in conjunction with onSavedInstanceState because a Date object cannot
     * be added to a Bundle
     *
     * @param dates the List of Dates
     * @return an array of longs corresponding to the time of each date
     */
    private long[] toLongs(List<Date> dates) {
        long[] times = new long[dates.size()];
        for (int i = 0; i < dates.size(); i++) times[i] = dates.get(i).getTime();
        return times;
    }

    // getters

    public UserSummary getFriend() {
        return mSelected;
    }
}
