package com.protectapp.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetBadgeCountData;
import com.protectapp.model.ProfileData;
import com.protectapp.network.ProtectApiHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppSession {
    private static final AppSession ourInstance = new AppSession();
    private Context context;
    private String accessToken;
    private ProfileData profileData;
    private GetBadgeCountData badgeCountData;
    private int currentChatUserId=-1;
    public static AppSession getInstance() {
        return ourInstance;
    }

    public void init(Context context) {
        this.context = context;
        currentChatUserId=-1;
        String accessToken = Prefs.getString(context, Constants.PREF.ACCESS_TOKEN, null);
        ProfileData profileData = new Gson().fromJson(Prefs.getString(context, Constants.PREF.PROFILE_DATA, null), ProfileData.class);
        this.accessToken = accessToken;
        this.profileData = profileData;

    }

    private AppSession() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ProfileData getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
        Prefs.saveString(context, Constants.PREF.PROFILE_DATA, new Gson().toJson(profileData));
    }

    public void saveSession(String accessToken, ProfileData profileData) {
        this.accessToken = accessToken;
        this.profileData = profileData;
        Prefs.saveString(context, Constants.PREF.ACCESS_TOKEN, accessToken);
        Prefs.saveString(context, Constants.PREF.PROFILE_DATA, new Gson().toJson(profileData));
    }


    public void logout() {
        this.accessToken = null;
        this.profileData = null;
        badgeCountData=null;
        Prefs.clearAll(context);

    }

    public GetBadgeCountData getBadgeCountData() {
        return badgeCountData;
    }

    public void setBadgeCountData(GetBadgeCountData badgeCountData) {
        this.badgeCountData = badgeCountData;
    }

    public void updateFCMToken(final Context context)
    {
        if(accessToken==null || Prefs.getBoolean(context,Constants.PREF.FCM_TOKEN_UPDATED,false))return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                final String fcmToken = task.getResult().getToken();
                ProtectApiHelper.getInstance().updateFCMToken(accessToken, fcmToken, new Callback<GenericResponseModel<Object>>() {
                    @Override
                    public void onResponse(Call<GenericResponseModel<Object>> call, Response<GenericResponseModel<Object>> response) {
                        if(response.body().getSuccess()==1)
                        {
                         Prefs.saveBoolean(context,Constants.PREF.FCM_TOKEN_UPDATED,true);
                         Prefs.saveString(context,Constants.PREF.FCM_TOKEN,fcmToken);
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponseModel<Object>> call, Throwable t) {

                    }
                });
            }
        });
    }

    public int getCurrentChatUserId() {
        return currentChatUserId;
    }
    public void resetCurrentChatUserId() {
        currentChatUserId=-1;
    }
    public void setCurrentChatUserId(int currentChatUserId) {
        this.currentChatUserId = currentChatUserId;
    }
}
