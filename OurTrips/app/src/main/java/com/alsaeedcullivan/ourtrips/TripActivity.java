package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alsaeedcullivan.ourtrips.adapters.PageAdapter;
import com.alsaeedcullivan.ourtrips.cloud.AccessDB;
import com.alsaeedcullivan.ourtrips.models.Place;
import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

// this is the activity where the user will be able to create, view and update trips
public class TripActivity extends AppCompatActivity {

    private BottomNavigationView mNavigation;
    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private ProgressBar mSpinner;
    private TextView mLoadingText;

    // trip data
    private String mTripId, mTripTitle, mStartDate, mEndDate, mOverview;
    private ArrayList<Place> mLocationList;

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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
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

    private void loadTrip() {
        AccessDB.getTripInfo(mTripId).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult();
                    if (data != null) populateFields(data);
                } else {
                    //TODO toast user
                }
            }
        });
    }

    private void populateFields(Map<String, Object> data) {
        // get the title
        String title = (String) data.get(Const.TRIP_TITLE_KEY);
        if (title != null) {
            mTripTitle = title;

        }
        // get the start date
        String start = (String) data.get(Const.TRIP_START_DATE_KEY);
        if (start != null) {
            mStartDate = start;

        }
        // get the end date
        String end = (String) data.get(Const.TRIP_END_DATE_KEY);
        if (end != null) {
            mEndDate = end;

        }
        // get the overview
        String over = (String) data.get(Const.TRIP_OVERVIEW_KEY);
        if (over != null) {
            mOverview = over;
        }
    }
}
