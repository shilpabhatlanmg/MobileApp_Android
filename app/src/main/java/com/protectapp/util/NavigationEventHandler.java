package com.protectapp.util;

import android.content.Context;
import android.os.Bundle;

public interface NavigationEventHandler {
    void afterSplash(Context context,Bundle bundle);
    void afterLogin(Context context,Bundle bundle);
    void afterGetOtpClick(Context context,Bundle bundle);
    void afterOTPVerification(Context context,Bundle bundle);
    void afterForgotPasswordClick(Context context,Bundle bundle);
    void afterCreatePassword(Context context,Bundle bundle);
    void afterLogout(Context context,Bundle bundle);
}
