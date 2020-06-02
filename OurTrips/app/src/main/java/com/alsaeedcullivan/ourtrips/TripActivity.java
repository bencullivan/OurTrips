package com.alsaeedcullivan.ourtrips;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alsaeedcullivan.ourtrips.adapters.PageAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// this is the activity where the user will be able to create, view and update trips
public class TripActivity extends AppCompatActivity {

    // instance state keys
    private static final String ID_KEY = "id";
    private static final String TITLE_KEY = "title";
    private static final String START_KEY = "start";
    private static final String END_KEY = "end";
    private static final String OVER_KEY = "overview";

    // widgets
    private BottomNavigationView mNavigation;
    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private ProgressBar mSpinner;
    private TextView mLoadingText;
    private List<DocumentSnapshot> mTrippersList = new ArrayList<>();
    private List<DocumentSnapshot> mPicsList = new ArrayList<>();

    // trip data
    private String mTripId, mTripTitle, mStartDate, mEndDate, mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        Log.d(Const.TAG, "onCreate: trip activity");

        // get the trip id
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(Const.TRIP_ID_TAG) != null)
            mTripId = intent.getStringExtra(Const.TRIP_ID_TAG);
        else finish();

        // set-up title
        mActionBar = getSupportActionBar();
        Objects.requireNonNull(mActionBar).setTitle(R.string.title_activity_trip);
        Objects.requireNonNull(mActionBar).setDisplayHomeAsUpEnabled(true);

        // set up navigation bar
        mNavigation = findViewById(R.id.navigation_trip);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // set up pages
        mViewPager = findViewById(R.id.main_view_pager);
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // hook viewpager to adapter
        mViewPager.setAdapter(pageAdapter);

        // set on page change callback for view pager
        mViewPager.addOnPageChangeListener(createPageChangeListener());

        // get references to the progress bar and text view
        mSpinner = findViewById(R.id.trip_progress);
        mLoadingText = findViewById(R.id.trip_progress_text);

        // set initial visibility
        mViewPager.setVisibility(View.GONE);
        mNavigation.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoadingText.setVisibility(View.VISIBLE);

        // if the trip data has already been loaded
        if (savedInstanceState != null && savedInstanceState.getString(ID_KEY) != null &&
                savedInstanceState.getString(TITLE_KEY) != null && savedInstanceState
                .getString(START_KEY) != null && savedInstanceState.getString(END_KEY) != null) {
            Log.d(Const.TAG, "onCreate: save instance state trip activity");
            mTripId = savedInstanceState.getString(ID_KEY);
            mTripTitle = savedInstanceState.getString(TITLE_KEY);
            mStartDate = savedInstanceState.getString(START_KEY);
            mEndDate = savedInstanceState.getString(END_KEY);
            mOverview = savedInstanceState.getString(OVER_KEY);
            // display the fragments
            showFrags();
        }
        // the trip data has not already been loaded, load it from the database
        else {
            if (mTripId == null) {
                finish();
                return;
            }
            new LoadTripTask().execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(TripActivity.this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTripId != null) outState.putString(ID_KEY, mTripId);
        if (mTripTitle != null) outState.putString(TITLE_KEY, mTripTitle);
        if (mStartDate != null) outState.putString(START_KEY, mStartDate);
        if (mEndDate != null) outState.putString(END_KEY, mEndDate);
        if (mOverview != null) outState.putString(OVER_KEY, mOverview);
    }

    private ViewPager.OnPageChangeListener createPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mActionBar.setTitle(R.string.title_fragment_summary);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(true);
                        break;
                    case 1:
                        mActionBar.setTitle(R.string.title_fragment_plan);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(true);
                        break;
                    case 2:
                        mActionBar.setTitle(R.string.title_fragment_media);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(true);
                        break;
                    case 3:
                        mActionBar.setTitle(R.string.title_fragment_trippers);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        };
    }

    // on click listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.summary:
                    // set summary fragment
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.plan:
                    // set plan fragment
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.media:
                    // set media fragment
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.trippers:
                    // set trippers fragment
                    mViewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    /**
     * deleteTrip()
     * deletes this trip from the db and from all the user's trips sub-collections
     */
    public void deleteTrip() {
        if (mTripId == null) return;
        mLoadingText.setText(R.string.deleting_trip);
        hideFrags();
        new GetTrippersTask().execute();
    }

    // visibility
    private void showFrags() {
        mLoadingText.setText(R.string.loading_trip);
        mSpinner.setVisibility(View.GONE);
        mLoadingText.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mNavigation.setVisibility(View.VISIBLE);
    }
    private void hideFrags() {
        mViewPager.setVisibility(View.GONE);
        mNavigation.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mLoadingText.setVisibility(View.VISIBLE);
    }

    // GETTERS

    public String getTripId() {
        return mTripId;
    }

    public String getTripTitle() {
        if (mTripTitle != null) return mTripTitle;
        else return "";
    }

    public String getStartDate() {
        if (mStartDate != null) return mStartDate;
        else return "";
    }

    public String getEndDate() {
        if (mEndDate != null) return mEndDate;
        else return "";
    }

    public String getOverview() {
        if (mOverview != null) return mOverview;
        else return "";
    }


    // ASYNC TASKS

    /**
     * LoadTripTask
     * loads this trip's info from the db
     * updates the instance variables on the UI thread in onComplete()
     */
    private class LoadTripTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null) return null;

            Log.d(Const.TAG, "doInBackground: " + mTripId);
            // load the trip info
            AccessDB.getTripInfo(mTripId).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                @Override
                public void onComplete(@NonNull Task<Map<String, Object>> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = task.getResult();
                        if (data != null) {
                            // get the title
                            String title = (String) data.get(Const.TRIP_TITLE_KEY);
                            if (title != null) mTripTitle = title;
                            // get the start date
                            String start = (String) data.get(Const.TRIP_START_DATE_KEY);
                            if (start != null) mStartDate = start;
                            // get the end date
                            String end = (String) data.get(Const.TRIP_END_DATE_KEY);
                            if (end != null) mEndDate = end;
                            // get the overview
                            String over = (String) data.get(Const.TRIP_OVERVIEW_KEY);
                            if (over != null) mOverview = over;
                        }
                        showFrags();
                    } else {
                        Toast t = Toast.makeText(TripActivity.this, "The trip info could " +
                                "not be loaded", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                        finish();
                    }
                }
            });

            return null;
        }
    }


    // ASYNC TASKS FOR TRIP DELETION

    private class GetTrippersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null) return null;

            AccessDB.getTrippers(mTripId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mTrippersList = task.getResult().getDocuments();
                        if (mTrippersList.size() == 0) new GetPicsTask().execute();
                        else new DeleteFromTrippersTask().execute();
                    } else new GetPicsTask().execute();
                }
            });


            return null;
        }
    }

    private class DeleteFromTrippersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null || mTrippersList == null || mTrippersList.size() == 0) return null;

            List<Task<Void>> taskList = new ArrayList<>();
            for (DocumentSnapshot doc : mTrippersList) {
                Task<Void> t = FirebaseFirestore.getInstance()
                        .collection(Const.USERS_COLLECTION)
                        .document(doc.getId())
                        .collection(Const.USER_TRIPS_COLLECTION)
                        .document(mTripId)
                        .delete();
                taskList.add(t);
            }
            Tasks.whenAll(taskList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(Const.TAG, "onComplete: done deleting from trippers");
                    new GetPicsTask().execute();
                }
            });
            return null;
        }
    }

    private class GetPicsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null) return null;

            AccessDB.getTripPhotos(mTripId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mPicsList = task.getResult().getDocuments();
                        if (mPicsList.size() == 0) new DeleteTripTask().execute();
                        else new DeletePicsTask().execute();
                    } else new DeleteTripTask().execute();
                }
            });


            return null;
        }
    }

    private class DeletePicsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null || mPicsList == null || mPicsList.size() == 0) return null;

            List<Task<Void>> taskList = new ArrayList<>();

            for (DocumentSnapshot doc : mPicsList) {
                String path = (String) doc.get(Const.TRIP_PHOTO_KEY);
                if (path != null) {
                    Task<Void> t = FirebaseStorage.getInstance().getReference().child(path).delete();
                    taskList.add(t);
                }
            }

            Tasks.whenAll(taskList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(Const.TAG, "onComplete: done deleting photos");
                    new DeleteTripTask().execute();
                }
            });

            return null;
        }
    }

    private class DeleteTripTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mTripId == null) return null;

            // remove the document corresponding to this trip from the db
            AccessDB.deleteTrip(mTripId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(TripActivity.this, MainActivity.class);
                        intent.putExtra(Const.TRIP_ID_TAG, mTripId);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        showFrags();
                        Log.d(Const.TAG, "onComplete: fuck, it failed to delete");
                    }
                }
            });

            return null;
        }
    }
}
