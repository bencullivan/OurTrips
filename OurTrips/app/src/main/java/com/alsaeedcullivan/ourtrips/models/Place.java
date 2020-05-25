package com.alsaeedcullivan.ourtrips.models;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private String name;
    private LatLng location;
    private String docId;

    public Place() { }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }

    public String getPlaceName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }
}
