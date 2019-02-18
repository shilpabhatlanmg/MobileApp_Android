package com.protectapp.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.protectapp.activity.Dashboard;
import com.protectapp.activity.EnterOTPActivity;
import com.protectapp.activity.MobileVerificationActivity;
import com.protectapp.model.ProfileData;

public class NavigationEventHandlerImpl implements NavigationEventHandler {
    @Override
    public void afterSplash(Context context,Bundle bundle) {
        boolean loggedIn = AppSession.getInstance().getAccessToken()!=null;
        context.startActivity(new Intent(context,loggedIn ? Dashboard.class : MobileVerificationActivity.class));

    }

    @Override
    public void afterLogin(Context context, Bundle bundle) {

    }

    @Override
    public void afterGetOtpClick(Context context, Bundle bundle) {
        context.startActivity(new Intent(context,EnterOTPActivity.class).putExtras(bundle));
    }


    @Override
    public void afterOTPVerification(Context context, Bundle bundle) {
        Intent intent = new Intent(context, Dashboard.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        AppSession.getInstance().saveSession(bundle.getString(Constants.EXTRA.ACCESS_TOKEN), (ProfileData) bundle.getSerializable(Constants.EXTRA.PROFILE_DATA));

        context.startActivity(intent);

    }

    @Override
    public void afterForgotPasswordClick(Context context, Bundle bundle) {
        context.startActivity(new Intent(context,MobileVerificationActivity.class).putExtras(bundle));
    }

    @Override
    public void afterCreatePassword(Context context, Bundle bundle) {

    }

    @Override
    public void afterLogout(Context context, Bundle bundle) {

    }
}
