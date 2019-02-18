package com.protectapp.listener;

public interface DashboardFragmentListener {
    void reportIncident(int incidentType);
    void incidentListLoaded();
    void onProfileUpdated();
}
