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

import com.alsaeedcullivan.ourtrips.adapters.TSAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.comparators.TripDateComparator;
import com.alsaeedcullivan.ourtrips.fragments.CustomDialogFragment;
import com.alsaeedcullivan.ourtrips.models.TripSummary;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TRIP_LIST_KEY = "trip_list";

    private TSAdapter mAdapter;
    private ArrayList<TripSummary> mTrips;
    private FirebaseUser mUser;
    private ListView mListView;
    private TripSummary mSelected;
    private ProgressBar mSpinner;
    private TextView mLoading;
    private LinearLayout mLayout;
    private boolean mSorted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set title
        setTitle(R.string.title_activity_main);
        Log.d(Const.TAG, "onCreate: main");
        // get references to the widgets
        mLayout = findViewById(R.id.main_list_layout);
        mSpinner = findViewById(R.id.main_spinner);
        mLoading = findViewById(R.id.main_loading);

        // get a reference to the list view
        mListView = findViewById(R.id.main_list);

        // set initial visibility
        mLayout.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);

        // get the user
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(TRIP_LIST_KEY) != null) {
            // get the list
            mTrips = savedInstanceState.getParcelableArrayList(TRIP_LIST_KEY);
            // create the adapter
            mAdapter = new TSAdapter(this, R.layout.activity_main, new ArrayList<TripSummary>());
            mAdapter.addAll(mTrips);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(getItemListener());
            showList();
        }
        else if (mUser != null) {
            // initialize the adapter
            mAdapter = new TSAdapter(this, R.layout.activity_main, new ArrayList<TripSummary>());
            // set the adapter to the list view
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(getItemListener());
            // get the list of trip summaries of this user from the db
            Task<List<TripSummary>> task = AccessDB.getTripSummaries(mUser.getUid());
            task.addOnCompleteListener(new OnCompleteListener<List<TripSummary>>() {
                @Override
                public void onComplete(@NonNull Task<List<TripSummary>> task) {
                    if (task.isSuccessful()) {
                        Log.d(Const.TAG, "onComplete: loaded summaries");
                        // get the list of trip summaries
                        mTrips = (ArrayList<TripSummary>) task.getResult();
                        Log.d(Const.TAG, "onComplete: " + mTrips);
                        // add them to the adapter
                        if (mTrips != null) {
                            new SortTask().execute();
                        }
                    } else {
                        Toast t = Toast.makeText(MainActivity.this, "Could not load your trips.",
                                Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTrips != null && mTrips.size() > 0) outState.putParcelableArrayList(TRIP_LIST_KEY, mTrips);
    }

    // handle menu //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_dates:
                // proceed to calendar activity
                Intent datesIntent = new Intent(MainActivity.this, CalendarActivity.class);
                datesIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(datesIntent);
                break;
            case R.id.friends:
                // proceed to FriendActivity
                Intent friendIntent = new Intent(MainActivity.this, FriendActivity.class);
                friendIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(friendIntent);
                break;
            case R.id.settings:
                // proceed to settings activity
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                settingsIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(settingsIntent);
                break;
            case R.id.match_dates:
                Intent matchIntent = new Intent(MainActivity.this, MatchActivity.class);
                matchIntent.putExtra(Const.SOURCE_TAG, Const.MAIN_TAG);
                startActivity(matchIntent);
                break;
            case R.id.search_trips:
                searchTrips();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * searchTrips
     * allows the user to search for a trip by name
     */
    private void searchTrips() {
        CustomDialogFragment.newInstance(CustomDialogFragment.SEARCH_TRIP_ID)
                .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
    }

    /**
     * search()
     * allows the user to search for a specific trip based on the title
     * @param title the title of the trip they are searching for
     */
    public void search(String title) {
        if (mTrips == null || mTrips.size() <= 1) return;
        for (int i = 0; i < mTrips.size(); i++) {
            if (title.equalsIgnoreCase(mTrips.get(i).getTitle())) {
                TripSummary t = mTrips.get(i);
                mTrips.set(i, mTrips.get(0));
                mTrips.set(0, t);
                mAdapter.clear();
                mAdapter.addAll(mTrips);
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
        Toast t = Toast.makeText(this, "There is no trip with the title: " + title,
                Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();
    }

    // gets a click listener for the list view
    private AdapterView.OnItemClickListener getItemListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // proceed to trip activity with the selected trip
                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                intent.putExtra(Const.TRIP_ID_TAG, mTrips.get(position).getId());
                startActivity(intent);
            }
        };
    }

    // shows the list view
    private void showList() {
        mSpinner.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLayout.setVisibility(View.VISIBLE);
    }

    /**
     * task to sort the trips by date
     */
    private class SortTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTrips != null && mTrips.size() > 0) {
                mTrips.sort(new TripDateComparator());
                mAdapter.addAll(mTrips);
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showList();
        }
    }
}
