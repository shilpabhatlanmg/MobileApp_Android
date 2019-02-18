package com.protectapp.util;

public class Constants {
    private Constants()
    {

    }
    public static final String PROTECT_API_BASE_URL="https://demo.newmediaguru.co/protect-app/api/";
    public static final long SPLASH_DELAY = 2000;
    public static final long MOBILE_NUMBER_LENGTH =10;
    public static final long PASSWORD_MIN_LENGTH =6;
    public static final long OTP_COUNTDOWN_MINUTES=2;
    public static final String COUNTRY_CODE="+1";
    public static final int MAX_ATTENDEE_IMAGES=3;
    public interface EXTRA
    {
        String MOBILE_NUMBER="extra_mobile_number";
        String OTP_VALIDITY="extra_otp_validity";
        String FROM_FORGOT_PASSWORD="extra_from_forgot_passsword";
        String ACCESS_TOKEN="extra_access_token";
        String PROFILE_DATA="extra_profile_data";
        String CREATE_PASSWORD="extra_create_password";
        String TOOLBAR_TITLE="extra_toolbar_title";
        String CONTACT="extra_contact";
        String DID_SENT_MESSAGE="extra_did_sent_message";
        String INCIDENT="extra_incident";
        String REPORT_REMINDER="extra_report_incident";
    }
    public interface PREF
    {
        String ACCESS_TOKEN="pref_access_token";
        String PROFILE_DATA="pref_profile_data";
        String FCM_TOKEN_UPDATED="pref_fcm_token_updated";
        String FCM_TOKEN="pref_fcm_token";
    }
    public interface FLAVOR
    {
        String MAIN="main";
        String VISITOR="visitor";
        String SECURITY="security";
    }
    public interface API
    {
        int SUCCESS=200;

    }
    public interface INCIDENT
    {
        int TYPE_MEDICAL=1;
        int TYPE_FIRE=2;
        int TYPE_POLICE=3;
        int TYPE_ASSIST=4;

        String TODAY="today";
        String YESTERDAY="yesterday";
        String OTHERS="others";
    }
    public interface BEACON_STATUS
    {
        int SEARCHING=7001;
        int UPDATING=7002;
        int UPDATED=7003;
    }
    public interface NOTIFICATION_TYPE
    {
        String CHAT="chat";
        String REPORT="report";
        String REPORT_REMINDER="cron-report";
    }
    public interface CMS
    {
        String ABOUT_US="about us";
        String PRIVACY_POLICY="privacy policy";
    }
    public interface ACTIVITY_RQ
    {
        int SELECT_MEMBER = 40001;
        int CHAT_UPDATE = 40002;
    }
    public static final long BEACON_SCAN_INTERVAL=2*1000;
    public static final int BEACON_DISTANCE_BUFFER=3;
    public static final String FIREBASE_CHAT_KEY="protect_chat";
    public static final int FIREBASE_CHAT_LIMIT=12;


        }

