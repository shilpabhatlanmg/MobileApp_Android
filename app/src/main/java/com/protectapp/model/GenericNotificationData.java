package com.protectapp.model;

import com.google.gson.annotations.SerializedName;

public class GenericNotificationData<T> {

    @SerializedName("notification-type")
    private String notificationType;

    @SerializedName("payload")
    private T payload;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
