package com.alsaeedcullivan.ourtrips.comparators;

import com.alsaeedcullivan.ourtrips.models.Plan;

import java.util.Comparator;

public class PlanComparator implements Comparator<Plan> {

    @Override
    public int compare(Plan o1, Plan o2) {
        long diff = o1.getPlanTimeStamp() - o2.getPlanTimeStamp();
        if (diff > 0) return 1;
        else if (diff < 0) return -1;
        else return 0;
    }
}
