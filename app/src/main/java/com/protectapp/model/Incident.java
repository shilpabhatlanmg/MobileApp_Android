package com.protectapp.model;

import java.io.Serializable;
import java.util.List;

public class Incident implements Serializable {
    private boolean isDateHeader;
    private boolean isExpanded;
    private String displayableDate;
    private String displayableTime;


    private String timestamp;

    private User initiatedBy;

    private String organization;

    private String premise;

    private String location;

    private List<User> attendedBy;

    private String hasResponded;

    private int type;

    private int reportID;

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public User getInitiatedBy ()
    {
        return initiatedBy;
    }

    public void setInitiatedBy (User initiatedBy)
    {
        this.initiatedBy = initiatedBy;
    }

    public String getOrganization ()
    {
        return organization;
    }

    public void setOrganization (String organization)
    {
        this.organization = organization;
    }

    public String getPremise ()
    {
        return premise;
    }

    public void setPremise (String premise)
    {
        this.premise = premise;
    }

    public String getLocation ()
    {
        return location;
    }

    public void setLocation (String location)
    {
        this.location = location;
    }

    public List<User> getAttendedBy ()
    {
        return attendedBy;
    }

    public void setAttendedBy (List<User> attendedBy)
    {
        this.attendedBy = attendedBy;
    }

    public String getHasResponded ()
    {
        return hasResponded;
    }

    public void setHasResponded (String hasResponded)
    {
        this.hasResponded = hasResponded;
    }

    public int getType ()
    {
        return type;
    }

    public void setType (int type)
    {
        this.type = type;
    }

    public int getReportID ()
    {
        return reportID;
    }

    public void setReportID (int reportID)
    {
        this.reportID = reportID;
    }

    public boolean isDateHeader() {
        return isDateHeader;
    }

    public void setDateHeader(boolean dateHeader) {
        isDateHeader = dateHeader;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getDisplayableDate() {
        return displayableDate;
    }

    public void setDisplayableDate(String displayableDate) {
        this.displayableDate = displayableDate;
    }

    public String getDisplayableTime() {
        return displayableTime;
    }

    public void setDisplayableTime(String displayableTime) {
        this.displayableTime = displayableTime;
    }
}
