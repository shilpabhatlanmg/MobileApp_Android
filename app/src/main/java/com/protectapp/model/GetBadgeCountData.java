package com.protectapp.model;

import com.google.gson.annotations.SerializedName;

public class GetBadgeCountData {

    @SerializedName("chat_badge_count")
    private int chatBadgeCount;
    @SerializedName("incident_badge_count")
    private int incidentBadgeCount;

    public int getChatBadgeCount() {
        return chatBadgeCount;
    }

    public void setChatBadgeCount(int chatBadgeCount) {
        if(chatBadgeCount>=0)
        this.chatBadgeCount = chatBadgeCount;
        else
        this.chatBadgeCount=0;
    }

    public int getIncidentBadgeCount() {
        return incidentBadgeCount;
    }

    public void setIncidentBadgeCount(int incidentBadgeCount) {
        this.incidentBadgeCount = incidentBadgeCount;
    }
}
