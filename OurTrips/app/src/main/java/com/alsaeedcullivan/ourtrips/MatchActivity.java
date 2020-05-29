package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MatchActivity extends AppCompatActivity {

    private static final String FRIENDS_KEY = "friends";
    private static final String DATES_KEY = "dates";
    private static final String NAME_KEY = "name";
    private static final String SELECTED_KEY = "selected";

    private FriendAdapter mAdapter;
    private List<Date> mUserDates = new ArrayList<>();
    private List<String> sDates = new ArrayList<>();
    private ArrayList<UserSummary> mFriends;
    private HashSet<UserSummary> mTrippers = new HashSet<>();
    private List<Date> mFriendDates = new ArrayList<>();
    private List<String> mFriendSDates = new ArrayList<>();
    private List<DocumentSnapshot> mFriendAddDocs = new ArrayList<>();
    private List<DocumentSnapshot> mFriendMatchDocs = new ArrayList<>();
    private List<DocumentSnapshot> mTripperDocs = new ArrayList<>();
    private long[] mMatched;
    private String mUserName;
    private String mTripId;
    private String mTripTitle;
    private String mTripStart;
    private FirebaseUser mUser;
    private ListView mListView;
    private UserSummary mSelected;
    private ProgressBar mSpinner;
    private TextView mLoading;
    private LinearLayout mLayout;
    private String mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mSource = intent.getStringExtra(Const.SOURCE_TAG);

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

        // instantiate the adapter
        mAdapter = new FriendAdapter(this, R.layout.activity_match, new ArrayList<UserSummary>());

        // assign the adapter to the list view
        mListView.setAdapter(mAdapter);

        // if a user has been selected, restore them
        if (savedInstanceState != null && savedInstanceState.getParcelable(SELECTED_KEY) != null) {
            mSelected = savedInstanceState.getParcelable(SELECTED_KEY);
        }

        // if this is to add a tripper
        if (mSource != null && mSource.equals(Const.TRIP_ACTIVITY_TAG) && intent
                .getStringExtra(Const.TRIP_ID_TAG) != null && intent
                .getStringExtra(Const.TRIP_START_TAG) != null && intent
                .getStringExtra(Const.TRIP_TITLE_TAG) != null) {

            setTitle("Add Tripper");
            TextView header = findViewById(R.id.match_text);
            header.setText(R.string.add_tripper_friend);
            // get the trip info
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);
            mTripTitle = intent.getStringExtra(Const.TRIP_TITLE_TAG);
            mTripStart = intent.getStringExtra(Const.TRIP_START_TAG);
            // set the on item click listener
            mListView.setOnItemClickListener(addTripperListener());

            // if the instance state has been saved
            if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(FRIENDS_KEY) != null) {
                // get the list of friends
                mFriends = savedInstanceState.getParcelableArrayList(FRIENDS_KEY);
                // add the list to the adapter
                if (mFriends != null) {
                    mAdapter.addAll(mFriends);
                    mAdapter.notifyDataSetChanged();
                }
                // show the list
                showList();
            }
            // else load the user's friends from the db
            else if (mUser != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // load this user's list of friends
                        Task<QuerySnapshot> friendTask = AccessDB.getFriendsList(mUser.getUid())
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot result = task.getResult();
                                        if (task.isSuccessful() && result != null && result
                                                .getDocuments().size() > 0) {
                                            // get the list of documents
                                            mFriendAddDocs = result.getDocuments();

//                                            // get the list of friends
//                                            mFriends = (ArrayList<UserSummary>) task.getResult();
                                        } else {
                                            Toast t = Toast.makeText(MatchActivity.this,
                                                    "Could not load your friends.",
                                                    Toast.LENGTH_SHORT);
                                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                                                    0, 0);
                                            t.show();
                                        }
                                    }
                                });
                        // load the list of trippers
                        Task<QuerySnapshot> tripperTask = AccessDB.getTrippers(mTripId)
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot result = task.getResult();
                                        if (task.isSuccessful() && result != null && result
                                                .getDocuments().size() > 0) {
                                            // get the list of documents
                                            mTripperDocs = result.getDocuments();
                                        }
//                                        if (task.isSuccessful() && task.getResult() != null) {
//                                            mTrippers.clear();
//                                            mTrippers.addAll(task.getResult());
//                                        }
                                    }
                                });
                        // add the friends that are not part of the trip to the list of friends
                        Tasks.whenAll(friendTask, tripperTask).continueWith(new Continuation<Void, Object>() {
                            @Override
                            public Object then(@NonNull Task<Void> task) {
                                // display the friends that are not part of the trip
                                new FriendFilterTask().execute();
                                //new FriendTask().execute();
                                return null;
                            }
                        });
                    }
                }).start();
            }
        }
        // if this is to match dates
        else {
            setTitle("Match Dates");
            // set the on item click listener
            mListView.setOnItemClickListener(getMatchListener());

            // if the instance state has been saved
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
                    // get the list of friends
                    mFriends = savedInstanceState.getParcelableArrayList(FRIENDS_KEY);
                    if (mFriends != null) {
                        mAdapter.addAll(mFriends);
                        mAdapter.notifyDataSetChanged();
                    }
                    showList();
                }
            }
            // load the data
            else if (mUser != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // load this user's list of friends
                        AccessDB.getFriendsList(mUser.getUid())
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot result = task.getResult();
                                        if (task.isSuccessful() && result != null && result.getDocuments().size() > 0) {
                                            // get the list of documents and begin the async task
                                            mFriendMatchDocs = result.getDocuments();
                                            new FriendSetUpTask().execute();
//                                            // get the list of friends
//                                            mFriends = (ArrayList<UserSummary>) task.getResult();
//                                            // add them to the adapter
//                                            if (mFriends != null) {
//                                                mAdapter.addAll(mFriends);
//                                                mAdapter.notifyDataSetChanged();
//                                            }
                                        } else {
                                            Toast t = Toast.makeText(MatchActivity.this,
                                                    "Could not load your friends.",
                                                    Toast.LENGTH_SHORT);
                                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                                                    0, 0);
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
                        AccessDB.getUserDates(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                if (task.isSuccessful() && doc != null &&
                                        doc.contains(Const.DATE_LIST_KEY) &&
                                        doc.get(Const.DATE_LIST_KEY) instanceof  List) {
                                    // this will not produce an exception
                                    sDates = (List<String>) doc.get(Const.DATE_LIST_KEY);
                                    //convert the strings to Dates
                                    new AddTask().execute();
                                } else {
                                    Toast t = Toast.makeText(MatchActivity.this, "Could not load your dates.",
                                            Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    t.show();
                                    showList();
                                }
                            }
                        });
                    }
                }).start();
            }
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
                Log.d(Const.TAG, "onOptionsItemSelected: " + mSource);
                // if this was to add a tripper, Trip activity must be started
                if (mSource != null && mSource.equals(Const.TRIP_ACTIVITY_TAG) && mTripId != null) {
                    Intent intent = new Intent(MatchActivity.this, TripActivity.class);
                    intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                    startActivity(intent);
                }
                // finish this activity
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
        if (mSelected != null) outState.putParcelable(SELECTED_KEY, mSelected);
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
        match(mSelected.getUserId());
    }

    /**
     * onAddClicked()
     * adds the friend that was selected to the current trip
     */
    public void onAddClicked() {
        Log.d(Const.TAG, "onAddClicked: ");
        if (mSelected == null || mTripId == null || mSelected.getUserId() == null ||
                mSelected.getEmail() == null || mSelected.getName() == null ||
                mTripStart == null || mTripTitle == null) return;
        hideList();

        // run db operations in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                // add the friend to the trippers sub collection and add the trip to the friend's trips
                // sub-collection
                Task<Void> tripTask = AccessDB.addTripper(mTripId, mSelected.getUserId(),
                        mSelected.getEmail(), mSelected.getName())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast t = Toast.makeText(MatchActivity.this, mSelected.getName() +
                                            " could not be added to the trip.", Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    t.show();
                                    showList();
                                }
                            }
                        });
                Task<Void> friendTask = AccessDB.addUserTrip(mSelected.getUserId(), mTripId, mTripTitle, mTripStart)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast t = Toast.makeText(MatchActivity.this, mSelected.getName() +
                                            " could not be added to the trip.", Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    t.show();
                                    showList();
                                }
                            }
                        });
                // if both the tasks succeed, head back to trip activity
                Tasks.whenAll(tripTask, friendTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Const.TAG, "onSuccess: it worked hell yeah");
                        Intent intent = new Intent(MatchActivity.this, TripActivity.class);
                        intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }

    /**
     * match()
     * called when a user selects one of their friends and clicks "match"
     *
     * @param friendId  the id of the friend that they selected
     */
    private void match(String friendId) {
        final String fId = friendId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccessDB.getUserDates(fId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if (task.isSuccessful() && doc != null &&
                                doc.contains(Const.DATE_LIST_KEY) &&
                                doc.get(Const.DATE_LIST_KEY) instanceof List) {
                            // this will not produce an exception
                            mFriendSDates = (List<String>) doc.get(Const.DATE_LIST_KEY);
                            //convert the strings to Dates
                            new MatchTask().execute();
                        } else {
                            // unable to be matched
                            Toast t = Toast.makeText(MatchActivity.this, "You and " +
                                    mSelected.getName() + " were not able to be matched.", Toast.LENGTH_LONG);
                            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                            t.show();
                            showList();
                        }
                    }
                });
            }
        }).start();
//        // perform a match to see which dates you are both available
//        AccessDB.matchDates(userDates, friendId).addOnCompleteListener(new OnCompleteListener<long[]>() {
//            @Override
//            public void onComplete(@NonNull Task<long[]> task) {
//                if (task.isSuccessful()) {
//                    long[] matched = task.getResult();
//                    if (matched != null && matched.length > 0) {
//                        // start CalendarActivity and pass it an array of all the matched dates
//                        Intent intent = new Intent(MatchActivity.this, CalendarActivity.class);
//                        intent.putExtra(Const.SOURCE_TAG, Const.MATCH_TAG);
//                        intent.putExtra(Const.MATCH_ARR_TAG, matched);
//                        intent.putExtra(Const.SELECTED_FRIEND_TAG, mSelected);
//                        intent.putExtra(Const.USER_NAME_TAG, mUserName);
//                        startActivity(intent);
//                    } else {
//                        // the two users have no dates in common
//                        Toast t = Toast.makeText(MatchActivity.this, "You and " +
//                                mSelected.getName() + " have no dates in common.", Toast.LENGTH_LONG);
//                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                        t.show();
//                        showList();
//                    }
//                } else {
//                    // unable to be matched
//                    Toast t = Toast.makeText(MatchActivity.this, "You and " +
//                            mSelected.getName() + " were not able to be matched.", Toast.LENGTH_LONG);
//                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                    t.show();
//                    showList();
//                }
//            }
//        });
    }

    // returns an onClickListener for the list view
    private AdapterView.OnItemClickListener getMatchListener() {
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
    private AdapterView.OnItemClickListener addTripperListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Const.TAG, "onItemClick: " + mFriends.get(position).getUserId());
                mSelected = mFriends.get(position);
                // display the add tripper dialog
                CustomDialogFragment.newInstance(CustomDialogFragment.ADD_TRIPPER_ID)
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


    /**
     * AddTask
     * converts a list of strings to a list of dates
     */
    private class AddTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            if (sDates == null || sDates.size() == 0) return null;
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            try {
                Date today = format.parse(format.format(new Date()));

                // convert each string date into a date object
                // make sure that none of these dates are before today
                for (String date : sDates) {
                    Date d = format.parse(date);
                    if (d != null && d.compareTo(today) >= 0) {
                        mUserDates.add(d);
                    }
                }
            } catch (ParseException e) {
                Log.d(Const.TAG, "doInBackground: " + Log.getStackTraceString(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // show the list
            showList();
        }
    }

    /**
     * MatchTask
     * matches dates between this user and a friend
     */
    private class MatchTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mFriendSDates == null || mFriendSDates.size() == 0) {
                publishProgress("None");
                return null;
            }

            // get a date format
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            try {
                // convert the strings to dates
                for (String date : mFriendSDates) {
                    mFriendDates.add(format.parse(date));
                }
            } catch (ParseException e) {
                Log.d(Const.TAG, "doInBackground: " + Log.getStackTraceString(e));
            }

            // if there are no friend dates, return
            if (mFriendDates.size() == 0 || mUserDates == null || mUserDates.size() == 0) {
                publishProgress("None");
                return null;
            }

            // initialize a list to hold the matched dates
            List<Date> matched = new ArrayList<>();

            Log.d(Const.TAG, "then: match: userDates: " + mUserDates);
            Log.d(Const.TAG, "then: match: friend: " + mFriendDates);

            int a = 0;
            int b = 0;
            while (a < mUserDates.size() && b < mFriendDates.size()) {
                int result = mUserDates.get(a).compareTo(mFriendDates.get(b));
                if (result < 0) a++;
                else if (result > 0 ) b++;
                else {
                    matched.add(mUserDates.get(a));
                    a++;
                    b++;
                }
            }
            // create an array of the times of the matched dates
            mMatched = new long[matched.size()];
            for (int i = 0; i < matched.size(); i++) mMatched[i] = matched.get(i).getTime();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values != null && mSelected != null && values.length > 0 && values[0].equals("None")) {
                // the two users have no dates in common
                Toast t = Toast.makeText(MatchActivity.this, "You and " +
                        mSelected.getName() + " have no dates in common.", Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                t.show();
                showList();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mMatched != null && mMatched.length > 0) {
                // start CalendarActivity and pass it an array of all the matched dates
                Intent intent = new Intent(MatchActivity.this, CalendarActivity.class);
                intent.putExtra(Const.SOURCE_TAG, Const.MATCH_TAG);
                intent.putExtra(Const.MATCH_ARR_TAG, mMatched);
                intent.putExtra(Const.SELECTED_FRIEND_TAG, mSelected);
                intent.putExtra(Const.USER_NAME_TAG, mUserName);
                startActivity(intent);
            } else {
                // the two users have no dates in common
                Toast t = Toast.makeText(MatchActivity.this, "You and " +
                        mSelected.getName() + " have no dates in common.", Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                t.show();
                showList();
            }
        }
    }


    /**
     * FriendTask
     * adds friends that are not part of the trip to the list view
     */
    private class FriendTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // add the friends to a list that are not part of the trip
            if (mFriends == null || mTrippers == null) return null;
            ArrayList<UserSummary> temp = new ArrayList<>();
            for (UserSummary user : mFriends) {
                if (!mTrippers.contains(user)) temp.add(user);
            }
            mFriends = temp;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mFriends == null) {
                showList();
                return;
            }
            // add the friends to the list view and show it
            mAdapter.clear();
            mAdapter.addAll(mFriends);
            mAdapter.notifyDataSetChanged();
            // show the list
            showList();
        }
    }


    /**
     * FriendSetUpTask
     * sets up the list of friends and adds it to the list view
     */
    private class FriendSetUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mFriendMatchDocs == null || mFriendMatchDocs.size() == 0) return null;

            mFriends = new ArrayList<>();

            for (DocumentSnapshot doc : mFriendMatchDocs) {
                UserSummary u = new UserSummary();
                u.setUserId(doc.getId());
                String email = (String)doc.get(Const.USER_EMAIL_KEY);
                if (email != null) u.setEmail(email);
                String name = (String)doc.get(Const.USER_NAME_KEY);
                if (name != null) u.setName(name);
                mFriends.add(u);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // update the list view
            if (mFriends != null && mAdapter != null) {
                mAdapter.clear();
                mAdapter.addAll(mFriends);
                mAdapter.notifyDataSetChanged();
            }

        }
    }


    /**
     * FriendFilterTask
     * creates objects for the documents and adds them to the friends list and trippers set
     */
    private class FriendFilterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mFriendAddDocs == null || mFriendAddDocs.size() == 0) return null;

            mFriends = new ArrayList<>();

            // add all of the friends to the friends list
            for (DocumentSnapshot doc : mFriendAddDocs) {
                UserSummary u = new UserSummary();
                u.setUserId(doc.getId());
                String email = (String)doc.get(Const.USER_EMAIL_KEY);
                if (email != null) u.setEmail(email);
                String name = (String)doc.get(Const.USER_NAME_KEY);
                if (name != null) u.setName(name);
                mFriends.add(u);
            }

            // add all of the trippers to the trippers set
            if (mTripperDocs != null && mTripperDocs.size() > 0) {
                List<UserSummary> trippers = new ArrayList<>();
                // extract a user summary from each document and return the list of trippers
                for (DocumentSnapshot doc : mTripperDocs) {
                    UserSummary tripper = new UserSummary();
                    tripper.setUserId(doc.getId());
                    String email = (String)doc.get(Const.USER_EMAIL_KEY);
                    if (email != null) tripper.setEmail(email);
                    String name = (String)doc.get(Const.USER_NAME_KEY);
                    if (name != null) tripper.setName(name);
                    trippers.add(tripper);
                }
                // add all the trippers to the trippers set
                mTrippers.clear();
                mTrippers.addAll(trippers);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // start the task that will update the list view
            new FriendTask().execute();
        }
    }
}
