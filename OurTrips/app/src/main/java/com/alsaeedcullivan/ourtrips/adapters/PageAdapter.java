package com.alsaeedcullivan.ourtrips.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.alsaeedcullivan.ourtrips.fragments.MediaFragment;
import com.alsaeedcullivan.ourtrips.fragments.PlanFragment;
import com.alsaeedcullivan.ourtrips.fragments.SummaryFragment;
import com.alsaeedcullivan.ourtrips.fragments.TrippersFragment;
import com.alsaeedcullivan.ourtrips.models.TripSummary;

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // return new instance of Summary | Plan | Media | Trippers based on position
        Fragment fragment = null;
        if (position == 0) fragment = SummaryFragment.newInstance();
        else if (position == 1) fragment = PlanFragment.newInstance();
        else if (position == 2) fragment = MediaFragment.newInstance();
        else if (position == 3) fragment = TrippersFragment.newInstance();
        return fragment;
    }

    @Override
    public int getCount() {// have four fragments: Summary, Plan, Media, Trippers
        return 4;
    }
}
