package com.alsaeedcullivan.ourtrips.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Plan implements Parcelable {

    private String message = "";
    private String planDocId = "";
    private String planUserId = "";
    private String planUserName = "";
    private long planTimeStamp = 0;

    public Plan() { }

    private Plan(Parcel in) {
        message = in.readString();
        planDocId = in.readString();
        planUserId = in.readString();
        planUserName = in.readString();
        planTimeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(planDocId);
        dest.writeString(planUserId);
        dest.writeString(planUserName);
        dest.writeLong(planTimeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlanDocId() {
        return planDocId;
    }

    public void setPlanDocId(String planDocId) {
        this.planDocId = planDocId;
    }

    public String getPlanUserId() {
        return planUserId;
    }

    public void setPlanUserId(String planUserId) {
        this.planUserId = planUserId;
    }

    public String getPlanUserName() {
        return planUserName;
    }

    public void setPlanUserName(String planUserName) {
        this.planUserName = planUserName;
    }

    public long getPlanTimeStamp() {
        return planTimeStamp;
    }

    public void setPlanTimeStamp(long planTimeStamp) {
        this.planTimeStamp = planTimeStamp;
    }
}
