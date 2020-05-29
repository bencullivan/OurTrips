package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private static final String TRIP_LIST_KEY = "list";
    private static final String DELETE_KEY = "del";
    private static final String POSITION_KEY = "pos";

    private TSAdapter mAdapter;
    private ArrayList<TripSummary> mTrips;
    private List<DocumentSnapshot> mSumDocs = new ArrayList<>();
    private FirebaseUser mUser;
    private ListView mListView;
    private ProgressBar mSpinner;
    private TextView mLoading;
    private LinearLayout mLayout;
    private String mTripId;
    private String deleteId;
    private int mPosition = -1;
    private int counter = 0;

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
        showSpinner();

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(Const.TRIP_ID_TAG) != null)
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(DELETE_KEY) != null) {
                deleteId = savedInstanceState.getString(DELETE_KEY);
                mPosition = savedInstanceState.getInt(POSITION_KEY);
            } else mPosition = -1;
        }

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
            mListView.setOnItemLongClickListener(getLongListener());
            showList();
        }
        else if (mUser != null) {
            // initialize the adapter
            mAdapter = new TSAdapter(this, R.layout.activity_main, new ArrayList<TripSummary>());
            // set the adapter to the list view
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(getItemListener());
            mListView.setOnItemLongClickListener(getLongListener());
            // db operation in background
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // get the list of trip summaries of this user from the db
                    Task<QuerySnapshot> task = AccessDB.getTripSummaries(mUser.getUid());
                    task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            QuerySnapshot result = task.getResult();
                            if (task.isSuccessful() && result != null && result.getDocuments().size() > 0) {
                                // get the documents and start the async task
                                mSumDocs = result.getDocuments();
                                new SumTask().execute();
                            } else {
                                Toast t = Toast.makeText(MainActivity.this, "Could not load your trips.",
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTrips != null && mTrips.size() > 0) outState.putParcelableArrayList(TRIP_LIST_KEY, mTrips);
        if (deleteId != null) {
            outState.putString(DELETE_KEY, deleteId);
            outState.putInt(POSITION_KEY, mPosition);
        }
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

    /**
     * removeTrip()
     * removes this user from the selected trip
     */
    public void removeTrip() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (deleteId == null || user == null) return;
        Log.d(Const.TAG, "removeTrip: " + deleteId + " " + mPosition);

        showSpinner();

        // db operations in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                // remove this user from the trip's trippers sub-collection
                Task<Void> tripTask = AccessDB.deleteTripper(deleteId, user.getUid())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Log.d(Const.TAG, "onComplete: fail");
                                    showList();
                                }
                            }
                        });
                // remove this trip from the user's trips sub-collection
                Task<Void> userTask = AccessDB.removeUserTrip(user.getUid(), deleteId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Log.d(Const.TAG, "onComplete: fail 2");
                                    showList();
                                }
                            }
                        });
                Log.d(Const.TAG, "run: thread " + Thread.currentThread().getId());
                // when both are finished redisplay the list without the item that was just deleted
                Tasks.whenAll(tripTask, userTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(Const.TAG, "onSuccess: updated trip sums");
                        Log.d(Const.TAG, "when all: thread " + Thread.currentThread().getId());
                        if (mPosition < 0 || mPosition >= mTrips.size() || mAdapter == null) return;
                        mTrips.remove(mPosition);
                        mAdapter.clear();
                        mAdapter.addAll(mTrips);
                        mAdapter.notifyDataSetChanged();
                        showList();
                    }
                });
            }
        }).start();
    }

    // listeners
    private AdapterView.OnItemClickListener getItemListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // proceed to trip activity with the selected trip
                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                intent.putExtra(Const.TRIP_ID_TAG, mTrips.get(position).getId());
                startActivity(intent);
                finish();
            }
        };
    }
    private AdapterView.OnItemLongClickListener getLongListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteId = mTrips.get(position).getId();
                mPosition = position;
                CustomDialogFragment.newInstance(CustomDialogFragment.REMOVE_USER_TRIP_ID)
                        .show(getSupportFragmentManager(), CustomDialogFragment.TAG);
                return true;
            }
        };
    }

    // shows the list view
    private void showList() {
        mSpinner.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mLayout.setVisibility(View.VISIBLE);
    }

    // hides the list view
    private void showSpinner() {
        mLayout.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * SortTask
     * task to sort the trips by date
     */
    private class SortTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTrips != null && mTrips.size() > 0) {
                // remove the trip that was deleted if it has not already been removed
                if (mTripId != null) {
                    for (int i = 0; i < mTrips.size(); i++) {
                        if (mTrips.get(i).getId() != null && mTrips.get(i).getId().equals(mTripId)) {
                            mTrips.remove(i);
                            break;
                        }
                    }
                }
                mTrips.sort(new TripDateComparator());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mAdapter == null) return;
            if (mTrips != null) {
                mAdapter.addAll(mTrips);
                mAdapter.notifyDataSetChanged();
            }
            showList();
        }
    }


    /**
     *SumTask
     * gets a list of trip summaries from a list of document snapshots
     */
    private class SumTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mSumDocs == null || mSumDocs.size() == 0) return null;

            // instantiate the list of trips
            mTrips = new ArrayList<>();

            Log.d(Const.TAG, "main   doInBackground: " + Thread.currentThread().getId());

            // add a trip summary corresponding to each document snapshot to the list of trip summaries
            for (DocumentSnapshot doc : mSumDocs) {
                TripSummary trip = new TripSummary();
                trip.setId(doc.getId());
                trip.setTitle((String)doc.get(Const.TRIP_TITLE_KEY));
                trip.setDate((String)doc.get(Const.TRIP_START_DATE_KEY));
                mTrips.add(trip);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // sort the trips and add them to the list view
            if (mTrips != null) new SortTask().execute();
            else showList();
        }
    }
}
