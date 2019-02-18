package com.protectapp.util;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.protectapp.activity.ChatActivity;
import com.protectapp.activity.Dashboard;
import com.protectapp.model.Contact;
import com.protectapp.model.GenericNotificationData;
import com.protectapp.model.Incident;

public class NotificationIntentFactory {

    public static final Intent getNotificationIntent(Context context,String notificationDataJson)
    {
        try
        {
        GenericNotificationData notificationData =
                new Gson().fromJson(notificationDataJson,GenericNotificationData.class);
        if (notificationData.getNotificationType().equals(Constants.NOTIFICATION_TYPE.CHAT))
        {

            return  getChatNotificationIntent(context, (GenericNotificationData<Contact>) new Gson().fromJson(notificationDataJson,new TypeToken<GenericNotificationData<Contact>>(){}.getType()));
        }
        else
        if (notificationData.getNotificationType().equals(Constants.NOTIFICATION_TYPE.REPORT))
        {
            return  getReportNotificationIntent(context, (GenericNotificationData<Incident>) new Gson().fromJson(notificationDataJson,new TypeToken<GenericNotificationData<Incident>>(){}.getType()));
        }
        }
        catch (Exception e)
        {

        }
        return null;
    }

    public static final Intent getChatNotificationIntent(Context context,GenericNotificationData<Contact> chatNotificationData) {
        if(chatNotificationData.getPayload()!=null)
        {

            Intent notificationIntent = new Intent(context,ChatActivity.class);
            notificationIntent.putExtra(Constants.EXTRA.CONTACT,chatNotificationData.getPayload());
            return  notificationIntent;
        }

        return null;
    }
    public static final Intent getReportNotificationIntent(Context context,GenericNotificationData<Incident> reportNotificationData) {
        if(reportNotificationData.getPayload()!=null)
        {

            Intent notificationIntent = new Intent(context,Dashboard.class);
            notificationIntent.putExtra(Constants.EXTRA.INCIDENT,reportNotificationData.getPayload());
            return  notificationIntent;
        }

        return null;
    }
    public static final Intent getReportReminderNotificationIntent(Context context,GenericNotificationData<Incident> reportNotificationData) {
        if(reportNotificationData.getPayload()!=null)
        {

            Intent notificationIntent = new Intent(context,Dashboard.class);
            notificationIntent.putExtra(Constants.EXTRA.INCIDENT,reportNotificationData.getPayload());
            notificationIntent.putExtra(Constants.EXTRA.REPORT_REMINDER,true);
            return  notificationIntent;
        }

        return null;
    }
}
