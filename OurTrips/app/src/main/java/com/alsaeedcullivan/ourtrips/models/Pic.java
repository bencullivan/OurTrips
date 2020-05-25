package com.alsaeedcullivan.ourtrips.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Pic implements Parcelable {
    private String path;
    private long date;

    public Pic(String path, long date) {
        this.path = path;
        this.date = date;
    }

    protected Pic(Parcel in) {
        path = in.readString();
        date = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(date);
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

    public long getPicDate() {
        return date;
    }
}
