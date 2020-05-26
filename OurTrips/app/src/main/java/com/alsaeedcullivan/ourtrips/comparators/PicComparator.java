package com.alsaeedcullivan.ourtrips.comparators;

import com.alsaeedcullivan.ourtrips.models.Pic;

import java.util.Comparator;

public class PicComparator implements Comparator<Pic> {
    @Override
    public int compare(Pic o1, Pic o2) {
        long dif = o2.getPicDate() - o1.getPicDate();
        if (dif < 0) return -1;
        else if (dif > 0) return 1;
        else return 0;
    }
}
