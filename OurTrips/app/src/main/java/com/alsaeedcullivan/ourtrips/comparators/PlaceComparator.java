package com.alsaeedcullivan.ourtrips.comparators;

import com.alsaeedcullivan.ourtrips.models.Place;

import java.util.Comparator;

public class PlaceComparator implements Comparator<Place> {
    @Override
    public int compare(Place o1, Place o2) {
        long diff = o1.getTimeStamp() - o2.getTimeStamp();
        if (diff < 0) return -1;
        else if (diff > 0) return 1;
        return 0;
    }
}
