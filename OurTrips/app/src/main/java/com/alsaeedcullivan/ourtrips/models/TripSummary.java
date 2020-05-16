package com.alsaeedcullivan.ourtrips.models;

import java.util.ArrayList;

/**
 * Model to contain the summary data of a trip
 */
public class TripSummary {

    // data
    private String title = "";
    private String date = "";

    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
