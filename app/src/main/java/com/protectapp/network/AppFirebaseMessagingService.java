package com.protectapp.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.protectapp.R;
import com.protectapp.model.Contact;
import com.protectapp.model.GenericNotificationData;
import com.protectapp.model.Incident;
import com.protectapp.util.AppCommons;
import com.protectapp.util.AppSession;
import com.protectapp.util.Constants;
import com.protectapp.util.NotificationID;
import com.protectapp.util.NotificationIntentFactory;
import com.protectapp.util.Prefs;

import org.greenrobot.eventbus.EventBus;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!=null)
        {

         try {
             String data = remoteMessage.getData().get("data");
             GenericNotificationData notificationData = new Gson().fromJson(data,GenericNotificationData.class);
             if(notificationData==null)
             {
                 showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),null,0);
                 return;
             }
             if (notificationData.getNotificationType().equals(Constants.NOTIFICATION_TYPE.CHAT))
             {

                handleChatNotification(data,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
             }
             else
             if (notificationData.getNotificationType().equals(Constants.NOTIFICATION_TYPE.REPORT))
             {
                 handleReportNotification(data,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
             }
             else
             if (notificationData.getNotificationType().equals(Constants.NOTIFICATION_TYPE.REPORT_REMINDER))
             {
                 handlerReportReminderNotification(data,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
             }

         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

        }

    }



    private void handleChatNotification(String data,String title,String message) {
        GenericNotificationData notificationData = new Gson().fromJson(data,new TypeToken<GenericNotificationData<Contact>>(){}.getType());
        if (notificationData.getPayload() instanceof  Contact)
        {
            AppSession.getInstance().getBadgeCountData().
                    setChatBadgeCount(AppSession.getInstance().getBadgeCountData().getChatBadgeCount()+1);
            Contact contact = (Contact)notificationData.getPayload();
            EventBus.getDefault().post(notificationData);
            int currentChatUserId = AppSession.getInstance().getCurrentChatUserId();

            if(currentChatUserId==contact.getUserID())
            {
                //Suppress notification

            }
            else
            {   //Send Notification
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(NotificationIntentFactory.getChatNotificationIntent(this,notificationData));
                PendingIntent contentIntent =stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                int notificationCode = AppCommons.generateChatNotificationCode(AppSession.getInstance().getProfileData().getUserID(),contact.getUserID());
                showNotification(title,message,contentIntent,notificationCode);
            }
        }

    }
    private void handleReportNotification(String data,String title,String message) {
        GenericNotificationData notificationData = new Gson().fromJson(data,new TypeToken<GenericNotificationData<Incident>>(){}.getType());
        if(notificationData.getPayload() instanceof Incident)
        {
            AppSession.getInstance().getBadgeCountData().
                    setIncidentBadgeCount(AppSession.getInstance().getBadgeCountData().getIncidentBadgeCount()+1);
            Incident incident = (Incident)notificationData.getPayload();
            EventBus.getDefault().post(notificationData);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    NotificationIntentFactory.getReportNotificationIntent(this,notificationData), PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification(title,message,contentIntent,incident.getReportID());
        }

    }
    private void handlerReportReminderNotification(String data, String title, String message) {
        GenericNotificationData notificationData = new Gson().fromJson(data,new TypeToken<GenericNotificationData<Incident>>(){}.getType());
        if(notificationData.getPayload() instanceof Incident)
        {
            Incident incident = (Incident)notificationData.getPayload();
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    NotificationIntentFactory.getReportNotificationIntent(this,notificationData), PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification(title,message,contentIntent,incident.getReportID());
        }
    }
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Prefs.saveBoolean(getApplicationContext(),Constants.PREF.FCM_TOKEN_UPDATED,false);
        AppSession.getInstance().updateFCMToken(getApplicationContext());
    }

    private void showNotification(String title, String message, PendingIntent intent, int notificationId)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "general")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this,R.color.colorGreen))
                .setContentTitle(title!=null ? title : getString(R.string.app_name))
                .setContentText(message!=null ? message : "")
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message!=null ? message : ""))
                .setContentIntent(intent)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.GREEN, 3000, 3000);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationManager.createNotificationChannel(getNotificationChannel());
        }

        notificationManager.notify(notificationId, mBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel getNotificationChannel()
    {
        NotificationChannel channel = new NotificationChannel("general","general",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.setBypassDnd(true);
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),att);
        channel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
        return channel;
    }

}
