package com.alsaeedcullivan.ourtrips.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Model to contain the summary data of a trip
 */
public class TripSummary implements Parcelable {

    // data
    private String title = "";
    private String date = "";
    private String id = "";

    public TripSummary() {
        // required empty public constructor
    }

    // getters and setters

    private TripSummary(Parcel in) {
        title = in.readString();
        date = in.readString();
    }

    public static final Creator<TripSummary> CREATOR = new Creator<TripSummary>() {
        @Override
        public TripSummary createFromParcel(Parcel in) {
            return new TripSummary(in);
        }

        @Override
        public TripSummary[] newArray(int size) {
            return new TripSummary[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
