package com.alsaeedcullivan.ourtrips.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Pic implements Parcelable {
    private String path;
    private long date;
    private String docId;

    public Pic() {}

    private Pic(Parcel in) {
        path = in.readString();
        date = in.readLong();
        docId = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(date);
        dest.writeString(docId);
    }

    // getters and setters

    public String getDocId() {
        return docId;
    }

    public String getPicPath() {
        return path;
    }

    public long getPicDate() {
        return date;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
