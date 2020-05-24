package com.alsaeedcullivan.ourtrips.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Pic implements Parcelable {
    private String path;
    private String date;

    public Pic(String path, String date) {
        this.path = path;
        this.date = date;
    }

    protected Pic(Parcel in) {
        path = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pic> CREATOR = new Creator<Pic>() {
        @Override
        public Pic createFromParcel(Parcel in) {
            return new Pic(in);
        }

        @Override
        public Pic[] newArray(int size) {
            return new Pic[size];
        }
    };

    // getters

    public String getPicPath() {
        return path;
    }

    public String getPicDate() {
        return date;
    }
}
