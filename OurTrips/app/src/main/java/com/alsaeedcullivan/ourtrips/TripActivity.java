package com.alsaeedcullivan.ourtrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.alsaeedcullivan.ourtrips.adapters.PageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

// this is the activity where the user will be able to create, view and update trips
public class TripActivity extends AppCompatActivity {

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private BottomNavigationView mNavigation;
    private ViewPager mViewPager;
    private PageAdapter pageAdapter;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        // set-up title
        mActionBar = getSupportActionBar();
        Objects.requireNonNull(mActionBar).setTitle(R.string.title_activity_trip);

        // get an instance of FirebaseStorage with the default bucket
        mStorage = FirebaseStorage.getInstance();
        // get a StorageReference
        mStorageReference = mStorage.getReference();

        // set up navigation bar
        mNavigation = findViewById(R.id.navigation_trip);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // set up pages
        mViewPager = findViewById(R.id.main_view_pager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // hook viewpager to adapter
        mViewPager.setAdapter(pageAdapter);

        // set on page change callback for view pager
        mViewPager.addOnPageChangeListener(createPageChangeListener());

    }

    private ViewPager.OnPageChangeListener createPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mActionBar.setTitle(R.string.title_fragment_summary);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(true);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(false);
                        break;
                    case 1:
                        mActionBar.setTitle(R.string.title_fragment_plan);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(true);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(false);
                        break;
                    case 2:
                        mActionBar.setTitle(R.string.title_fragment_media);
                        mNavigation.getMenu().findItem(R.id.summary).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.plan).setChecked(false);
                        mNavigation.getMenu().findItem(R.id.media).setChecked(true);
                        mNavigation.getMenu().findItem(R.id.trippers).setChecked(false);
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
            public void onPageScrollStateChanged(int state) {

            }
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
}
