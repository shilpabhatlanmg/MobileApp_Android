package com.protectapp.model;

public  class BeaconEvent {
    private String majorID;
    private String minorID;
    private double beaconDistance;
    private String beaconLocation;

    public String getMajorID() {
        return majorID;
    }

    public void setMajorID(String majorID) {
        this.majorID = majorID;
    }

    public String getMinorID() {
        return minorID;
    }

    public void setMinorID(String minorID) {
        this.minorID = minorID;
    }

    public double getBeaconDistance() {
        return beaconDistance;
    }

    public void setBeaconDistance(double beaconDistance) {
        this.beaconDistance = beaconDistance;
    }

    public String getBeaconLocation() {
        return beaconLocation;
    }

    public void setBeaconLocation(String beaconLocation) {
        this.beaconLocation = beaconLocation;
    }
}