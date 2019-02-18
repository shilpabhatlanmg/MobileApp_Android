package com.protectapp.model;

import com.google.gson.annotations.SerializedName;

public class VerifyOTPData {
    private boolean isOTPValid;
    @SerializedName("accesstoken")
    private String accessToken;

    public boolean isOTPValid() {
        return isOTPValid;
    }

    public void setOTPValid(boolean OTPValid) {
        isOTPValid = OTPValid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
