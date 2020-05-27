package com.alsaeedcullivan.ourtrips.models;

public class Plan {

    private String message = "";
    private String planDocId = "";
    private String planUserId = "";
    private String planUserName = "";
    private long planTimeStamp = 0;

    public Plan() { }

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
