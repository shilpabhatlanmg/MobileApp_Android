package com.protectapp.model;

import com.google.gson.annotations.SerializedName;

public class RequestOTPData {
    private String OTP;
    @SerializedName("validity")
    private String validity;

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
