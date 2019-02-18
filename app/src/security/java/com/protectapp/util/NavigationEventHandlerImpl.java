package com.protectapp.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.protectapp.activity.CreatePasswordActivity;
import com.protectapp.activity.Dashboard;
import com.protectapp.activity.EnterOTPActivity;
import com.protectapp.activity.LoginActivity;
import com.protectapp.activity.MobileVerificationActivity;
import com.protectapp.model.GenericNotificationData;
import com.protectapp.model.ProfileData;


public class NavigationEventHandlerImpl implements NavigationEventHandler {
    @Override
    public void afterSplash(Context context, Bundle bundle) {
        boolean loggedIn = AppSession.getInstance().getAccessToken()!=null;

        //If not Logged In redirect to Login Page
        if(!loggedIn)
        {
            context.startActivity(new Intent(context,LoginActivity.class));
            return;
        }

        //Handle the bundle and open specific pages
        if(bundle!=null)
        {

            String notificationDataJson = bundle.getString("data");

            if (notificationDataJson!=null)
            {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(NotificationIntentFactory.getNotificationIntent(context,notificationDataJson));
                stackBuilder.startActivities();
                return;
            }
        }
        context.startActivity(new Intent(context,loggedIn ? Dashboard.class : LoginActivity.class));
    }

    @Override
    public void afterLogin(Context context, Bundle bundle) {
        boolean shouldCreatePassword = bundle.getBoolean(Constants.EXTRA.CREATE_PASSWORD, false);
        Intent intent = new Intent(context, shouldCreatePassword ? CreatePasswordActivity.class : Dashboard.class);
        intent.putExtras(bundle);
        if (!shouldCreatePassword)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            AppSession.getInstance().saveSession(bundle.getString(Constants.EXTRA.ACCESS_TOKEN), (ProfileData) bundle.getSerializable(Constants.EXTRA.PROFILE_DATA));
        }
        context.startActivity(intent);
    }

    @Override
    public void afterGetOtpClick(Context context, Bundle bundle) {
        context.startActivity(new Intent(context, EnterOTPActivity.class).putExtras(bundle));
    }

    @Override
    public void afterOTPVerification(Context context, Bundle bundle) {
        boolean fromForgotPassword = bundle.getBoolean(Constants.EXTRA.FROM_FORGOT_PASSWORD, false);
        Intent intent = new Intent(context, fromForgotPassword ? CreatePasswordActivity.class : Dashboard.class);
        intent.putExtras(bundle);
        if (!fromForgotPassword)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            AppSession.getInstance().saveSession(bundle.getString(Constants.EXTRA.ACCESS_TOKEN), (ProfileData) bundle.getSerializable(Constants.EXTRA.PROFILE_DATA));
        }
        context.startActivity(intent);
    }

    @Override
    public void afterForgotPasswordClick(Context context, Bundle bundle) {
        context.startActivity(new Intent(context, MobileVerificationActivity.class).putExtras(bundle));
    }

    @Override
    public void afterCreatePassword(Context context, Bundle bundle) {
        AppSession.getInstance().saveSession(bundle.getString(Constants.EXTRA.ACCESS_TOKEN), (ProfileData) bundle.getSerializable(Constants.EXTRA.PROFILE_DATA));
        context.startActivity(new Intent(context, Dashboard.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtras(bundle));
    }

    @Override
    public void afterLogout(Context context, Bundle bundle) {
        context.startActivity(new Intent(context, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtras(bundle));
    }
}