package com.protectapp.listener;

import com.protectapp.model.GetLocationData;

public interface DashboardFragmentListener {
    void reportIncident(int incidentType);
    void incidentListLoaded();
    void onProfileUpdated();
    void onLocationLoaded(GetLocationData location);
}
