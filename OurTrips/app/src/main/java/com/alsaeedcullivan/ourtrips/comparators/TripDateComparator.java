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

        int first = aDate[2].compareTo(bDate[2]);
        if (first == 0) {
            int second = aDate[0].compareTo(bDate[0]);
            if (second == 0) {
                return aDate[1].compareTo(bDate[1]);
            } else return second;
        } else return first;
    }
}
