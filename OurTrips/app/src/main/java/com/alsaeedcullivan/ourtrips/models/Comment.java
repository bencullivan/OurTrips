package com.alsaeedcullivan.ourtrips.models;

/**
 * Model to contain the comment of a user
 */
public class Comment {

    // comment data
    private String user = "";
    private Long timeStamp;
    private String comment = "";

    // getters and setters

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String tripId) {
        this.comment = comment;
    }

}
