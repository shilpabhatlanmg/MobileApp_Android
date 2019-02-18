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

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProtectApi {
    @POST("security-login")
    Call<GenericResponseModel<ProfileData>> doSecurityLogin(@Body RequestBody request);
    @POST("request-otp")
    Call<GenericResponseModel<RequestOTPData>> requestOTP(@Body RequestBody request);
    @POST("verify-otp")
    Call<GenericResponseModel<VerifyOTPData>> verifyOTP(@Body RequestBody request);

    @POST("update-password")
    Call<GenericResponseModel<Object>> createPassword(@Header("token") String token ,@Body RequestBody request);

    @GET("get-user")
    Call<GenericResponseModel<ProfileData>> getProfile(@Header("token") String token);

    @GET("get-uuid")
    Call<GenericResponseModel<UUIDData>> getUUID();

    @GET("get-incident")
    Call<GenericResponseModel<GetIncidentsData>> getIncidents(@Header("token") String token , @Query("type") String type, @Query("page") int page);

    @POST("visitor-incident")
    Call<GenericResponseModel<Object>> reportIncident(@Header("token") String token ,@Body RequestBody request);

    @POST("get-location")
    Call<GenericResponseModel<GetLocationData>> getLocation(@Header("token") String token , @Body RequestBody request);

    @GET("get-contacts")
    Call<GenericResponseModel<GetContactsData>> getContacts(@Header("token") String token, @Query("page") int page);

    @GET("getLastMessage")
    Call<GenericResponseModel<GetContactsData>> getRecentChats(@Header("token") String token, @Query("page") int page);

    @POST("updateLastMessage")
    Call<GenericResponseModel<Object>> updateLastMessage(@Header("token") String token , @Body RequestBody request);

    @POST("resetUnread")
    Call<GenericResponseModel<Object>> resetUnreadChat(@Header("token") String token , @Body RequestBody request);

    @GET("total_badge")
    Call<GenericResponseModel<GetBadgeCountData>> getBadgeCount(@Header("token") String token);

    @POST("notification_token_registration")
    Call<GenericResponseModel<Object>> updateFCMToken(@Header("token") String token , @Body RequestBody request);

    @POST("update-user-profile")
    Call<GenericResponseModel<ProfileData>> updateUserProfile(@Header("token") String token , @Body RequestBody request);

    @GET("user-logout")
    Call<GenericResponseModel<Object>> doLogout(@Header("token") String token,@Query("deviceToken")String fcmToken);

    @POST("change-password")
    Call<GenericResponseModel<Object>> changePassword(@Header("token") String token , @Body RequestBody request);

    @GET("page-info")
    Call<GenericResponseModel<GetCMSPageData>> getCMSPage(@Query("contentType") String contentType);

    @POST("records-response")
    Call<GenericResponseModel<Object>> recordResponse(@Header("token") String token , @Body RequestBody request);
}
