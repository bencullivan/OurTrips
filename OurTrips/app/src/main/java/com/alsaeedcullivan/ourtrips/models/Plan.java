package com.alsaeedcullivan.ourtrips.models;

public class Plan {

    private String message, planTripId, planUserId, planUserName;
    private long planTimeStamp;

    public Plan() { }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlanTripId() {
        return planTripId;
    }

    public void setPlanTripId(String planTripId) {
        this.planTripId = planTripId;
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
