package com.alsaeedcullivan.ourtrips.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Model to contain the summary data associated with a user
 */
public class UserSummary implements Parcelable {

    // data
    private String name = "";
    private String email = "";
    private String userId = "";

    /*
    possibly
    private Bitmap profilePic;
     */

    public UserSummary() {
        // required empty public constructor
    }

    // getters and setters

    private UserSummary(Parcel in) {
        name = in.readString();
        email = in.readString();
        userId = in.readString();
    }

    public static final Creator<UserSummary> CREATOR = new Creator<UserSummary>() {
        @Override
        public UserSummary createFromParcel(Parcel in) {
            return new UserSummary(in);
        }

        @Override
        public UserSummary[] newArray(int size) {
            return new UserSummary[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(userId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof UserSummary)) return false;
        UserSummary user = (UserSummary) obj;
        return name.equals(user.getName()) && userId.equals(user.getUserId())
                && email.equals(user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email);
    }
}
