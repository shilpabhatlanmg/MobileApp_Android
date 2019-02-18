package com.protectapp.network;

import com.protectapp.model.Contact;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetBadgeCountData;
import com.protectapp.model.GetCMSPageData;
import com.protectapp.model.GetContactsData;
import com.protectapp.model.GetIncidentsData;
import com.protectapp.model.GetLocationData;
import com.protectapp.model.ProfileData;
import com.protectapp.model.RequestOTPData;
import com.protectapp.model.UUIDData;
import com.protectapp.model.VerifyOTPData;
import com.protectapp.util.Constants;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProtectApiHelper {
    private static final ProtectApiHelper ourInstance = new ProtectApiHelper();

    public static ProtectApiHelper getInstance() {
        return ourInstance;
    }

    private ProtectApi protectApi;
    private ProtectApiHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PROTECT_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        protectApi = retrofit.create(ProtectApi.class);
    }


    public void doSecurityLogin(String contactNumber, String password, Callback<GenericResponseModel<ProfileData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<ProfileData>> call =  protectApi.doSecurityLogin(
                    getRequestBody(ProtectApiRequestBuilder.getLoginRequest(contactNumber,password)));
            call.enqueue(responseCallback);

        }
    }
    public void requestOTP(String contactNumber, boolean hasForgottenPw,boolean isVisitor, Callback<GenericResponseModel<RequestOTPData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<RequestOTPData>> call =  protectApi.requestOTP(
                    getRequestBody(ProtectApiRequestBuilder.getRequestOTPRequest(contactNumber,hasForgottenPw,isVisitor)));
            call.enqueue(responseCallback);

        }
    }
    public void verifyOTP(String contactNumber, String OTP,boolean isVisitor, Callback<GenericResponseModel<VerifyOTPData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<VerifyOTPData>> call =  protectApi.verifyOTP(
                    getRequestBody(ProtectApiRequestBuilder.getVerifyOTPRequest(contactNumber,OTP,isVisitor)));
            call.enqueue(responseCallback);

        }
    }
    public void createPassword(String token,String password, Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.createPassword(token,
                    getRequestBody(ProtectApiRequestBuilder.getCreatePasswordRequest(password)));
            call.enqueue(responseCallback);

        }
    }
    public void getIncidents(String token,String type,int page, Callback<GenericResponseModel<GetIncidentsData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<GetIncidentsData>> call =  protectApi.getIncidents(token,type,page);
            call.enqueue(responseCallback);

        }
    }
    public void getProfile(String token, Callback<GenericResponseModel<ProfileData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<ProfileData>> call =  protectApi.getProfile(token);
            call.enqueue(responseCallback);

        }
    }
    public void getUUID(Callback<GenericResponseModel<UUIDData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<UUIDData>> call =  protectApi.getUUID();
            call.enqueue(responseCallback);

        }
    }
    public void reportIncident(String token,int incidentType,String majorID,String minorID, Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.reportIncident(token,
                    getRequestBody(ProtectApiRequestBuilder.getReportIncidentRequest(incidentType,majorID,minorID)));
            call.enqueue(responseCallback);

        }
    }
    public void getLocation(String token,String majorID,String minorID, Callback<GenericResponseModel<GetLocationData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<GetLocationData>> call =  protectApi.getLocation(token,
                    getRequestBody(ProtectApiRequestBuilder.getLocationRequest(majorID,minorID)));
            call.enqueue(responseCallback);

        }
    }
    public void getContacts(String token,int page, Callback<GenericResponseModel<GetContactsData>> responseCallback)
    {
        if(protectApi!=null)
        {
            Call<GenericResponseModel<GetContactsData>> call =  protectApi.getContacts(token,page);
            call.enqueue(responseCallback);

        }
    }
    public void getRecentChats(String token,int page, Callback<GenericResponseModel<GetContactsData>> responseCallback)
    {
        if(protectApi!=null)
        {
            Call<GenericResponseModel<GetContactsData>> call =  protectApi.getRecentChats(token,page);
            call.enqueue(responseCallback);

        }
    }

    public void updateLastMessage(String token,int receiverID,String lastMessage, Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.updateLastMessage(token,
                    getRequestBody(ProtectApiRequestBuilder.getUpdateLastMessageRequest(receiverID,lastMessage)));
            call.enqueue(responseCallback!=null ? responseCallback : new EmptyCallback<GenericResponseModel<Object>>());

        }
    }
    public void resetUnreadChat(String token,int receiverID, Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.resetUnreadChat(token,
                    getRequestBody(ProtectApiRequestBuilder.getRecentLastReadChat(receiverID)));
            call.enqueue(responseCallback!=null ? responseCallback : new EmptyCallback<GenericResponseModel<Object>>());

        }
    }

    public void getBadgeCount(String token,Callback<GenericResponseModel<GetBadgeCountData>> responseCallback)
    {
        if(protectApi!=null)
        {
            Call<GenericResponseModel<GetBadgeCountData>> call =  protectApi.getBadgeCount(token);
            call.enqueue(responseCallback);

        }
    }
    public void updateFCMToken(String token,String fcmToken, Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.updateFCMToken(token,
                    getRequestBody(ProtectApiRequestBuilder.getUpdateFCMTokenRequest(fcmToken)));
            call.enqueue(responseCallback!=null ? responseCallback : new EmptyCallback<GenericResponseModel<Object>>());

        }
    }
    public void updateUserProfile(String token,String name,String profileImage, Callback<GenericResponseModel<ProfileData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<ProfileData>> call =  protectApi.updateUserProfile(token,
                    getRequestBody(ProtectApiRequestBuilder.getUpdateUserProfileRequest(name,profileImage)));
            call.enqueue(responseCallback);

        }
    }
    public void doLogout(String token,String fcmToken, Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.doLogout(token,fcmToken);
            call.enqueue(responseCallback!=null ? responseCallback : new EmptyCallback<GenericResponseModel<Object>>());

        }
    }
    public void changePassword(String token,String oldPassword,String newPassword,Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.changePassword(token,
                    getRequestBody(ProtectApiRequestBuilder.getChangePasswordRequest(oldPassword, newPassword)));
            call.enqueue(responseCallback!=null ? responseCallback : new EmptyCallback<GenericResponseModel<Object>>());

        }
    }

    public void recordResponse(String token,int incidentID,Callback<GenericResponseModel<Object>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<Object>> call =  protectApi.changePassword(token,
                    getRequestBody(ProtectApiRequestBuilder.getRecordResponseRequest(incidentID)));
            call.enqueue(responseCallback!=null ? responseCallback : new EmptyCallback<GenericResponseModel<Object>>());

        }
    }

    public void getCMSPage(String contentType, Callback<GenericResponseModel<GetCMSPageData>> responseCallback)
    {

        if(protectApi!=null)
        {
            Call<GenericResponseModel<GetCMSPageData>> call =  protectApi.getCMSPage(contentType);
            call.enqueue(responseCallback);

        }
    }
    private RequestBody getRequestBody(String json) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
    }


}
