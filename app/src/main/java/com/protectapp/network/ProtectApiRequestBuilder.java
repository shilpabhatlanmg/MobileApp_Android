package com.protectapp.network;

import org.json.JSONException;
import org.json.JSONObject;

public class ProtectApiRequestBuilder {

    public static String getLoginRequest(String contactNumber, String password) {

        JSONObject request = new JSONObject();
        try {

            request.put("contactNumber", contactNumber);
            request.put("password", password);

        } catch (JSONException e) {

        }
        return request.toString();
    }

    public static String getRequestOTPRequest(String contactNumber, boolean hasForgottenPW,boolean isVisitor) {

        JSONObject request = new JSONObject();
        try {

            request.put("contactNumber", contactNumber);
            request.put("hasForgottenPw", hasForgottenPW);
            request.put("isVisitor", isVisitor);

        } catch (JSONException e) {

        }
        return request.toString();
    }

    public static String getVerifyOTPRequest(String contactNumber, String OTP,boolean isVisitor) {

        JSONObject request = new JSONObject();
        try {

            request.put("contactNumber", contactNumber);
            request.put("OTP", OTP);
            request.put("isVisitor", isVisitor);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getCreatePasswordRequest(String password) {

        JSONObject request = new JSONObject();
        try {
            request.put("password", password);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getReportIncidentRequest(int incidentType,String majorID,String minorID) {

        JSONObject request = new JSONObject();
        try {

            request.put("incidentType",incidentType);
            request.put("majorID",majorID);
            request.put("minorID", minorID);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getLocationRequest(String majorID,String minorID) {

        JSONObject request = new JSONObject();
        try {

            request.put("majorID",majorID);
            request.put("minorID", minorID);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getUpdateLastMessageRequest(int receiverID,String lastMessage) {

        JSONObject request = new JSONObject();
        try {

            request.put("receiverID",receiverID);
            request.put("lastMessage", lastMessage);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getRecentLastReadChat(int receiverID) {

        JSONObject request = new JSONObject();
        try {

            request.put("receiverID",receiverID);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getUpdateFCMTokenRequest(String fcmToken) {

        JSONObject request = new JSONObject();
        try {

            request.put("deviceToken",fcmToken);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getUpdateUserProfileRequest(String name,String profileImage) {

        JSONObject request = new JSONObject();
        try {

            request.put("name",name);
            request.put("profileImage",profileImage);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getChangePasswordRequest(String oldPassword,String newPassword) {

        JSONObject request = new JSONObject();
        try {

            request.put("oldPassword",oldPassword);
            request.put("newPassword",newPassword);

        } catch (JSONException e) {

        }
        return request.toString();
    }
    public static String getRecordResponseRequest(int incidentID) {

        JSONObject request = new JSONObject();
        try {

            request.put("incidentID",incidentID);

        } catch (JSONException e) {

        }
        return request.toString();
    }
}