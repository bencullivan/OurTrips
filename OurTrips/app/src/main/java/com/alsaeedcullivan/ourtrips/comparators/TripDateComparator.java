package com.alsaeedcullivan.ourtrips.comparators;

import com.alsaeedcullivan.ourtrips.models.TripSummary;

import java.util.Comparator;

public class TripDateComparator implements Comparator<TripSummary> {
    @Override
    public int compare(TripSummary o1, TripSummary o2) {
        // get the dates
        String a = o1.getDate();
        String b = o2.getDate();

        if (a == null || b == null) return 0;  // this will never happen

        // split the dates into their months days and years
        String[] aDate = a.split("/");
        String[] bDate = b.split("/");

        int first = bDate[2].compareTo(aDate[2]);
        if (first == 0) {
            int second = bDate[0].compareTo(aDate[0]);
            if (second == 0) {
                return bDate[1].compareTo(aDate[1]);
            } else return second;
        } else return first;
    }
}
