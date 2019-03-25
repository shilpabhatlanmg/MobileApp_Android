package com.protectapp.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.protectapp.R;
import com.protectapp.activity.Dashboard;
import com.protectapp.model.BeaconEvent;
import com.protectapp.model.GenericResponseModel;
import com.protectapp.model.GetLocationData;
import com.protectapp.model.UUIDData;
import com.protectapp.network.ProtectApiHelper;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.protectapp.util.AppCommons.callUser;
import static com.protectapp.util.AppCommons.toBeaconEvent;
import static com.protectapp.util.Constants.APP_BEACON_LAYOUT;
import static com.protectapp.util.Constants.FOREGROUND_BEACON_RANGE_ID;

public class AppBeaconService extends JobIntentService implements BeaconConsumer {
    private BeaconManager beaconManager;
    private String uuid;
    private Beacon trackedBeacon = null;
    private static final int BEACON_SERVICE_NOTIFICATION_ID = 148763902;
    private static AppBeaconService  instance;
    public static final AppBeaconService getInstance()
    {
        return instance;
    }
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        initBeaconManager();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance=null;
        unbindBeaconManager();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        AppCommons.setBluetooth(true);
        fetchUUID();
        if(trackedBeacon==null)
        {
            showNotification(null);
        }



//        trackedBeacon = null;
//        showNotification(null);

        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchUUID() {
        ProtectApiHelper.getInstance().getUUID(new Callback<GenericResponseModel<UUIDData>>() {
            @Override
            public void onResponse(Call<GenericResponseModel<UUIDData>> call, Response<GenericResponseModel<UUIDData>> response) {
                if(response.body()!=null && response.body().getData()!=null)
                {
                    uuid = response.body().getData().getUUID();
                    bindBeaconManager();
                }
                else
                {
                    fetchUUID();
                }
            }

            @Override
            public void onFailure(Call<GenericResponseModel<UUIDData>> call, Throwable throwable) {
                fetchUUID();
            }
        });
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    private void initBeaconManager() {

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(APP_BEACON_LAYOUT));

    }


    private void bindBeaconManager() {
        if (beaconManager != null) beaconManager.unbind(this);
        beaconManager.bind(this);
    }

    private void unbindBeaconManager() {
        if (beaconManager != null) beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {


        try {
            beaconManager.setForegroundScanPeriod(Constants.BEACON_SCAN_INTERVAL);
            beaconManager.updateScanPeriods();
        } catch (Exception e) {

        }

        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {


            }

            @Override
            public void didExitRegion(Region region) {
                trackedBeacon = null;
                EventBus.getDefault().post(toBeaconEvent(null));
                beaconManager.removeAllRangeNotifiers();
                stopBeaconRangingInRegion(region);
                showNotification(null);

            }


            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                beaconManager.removeAllRangeNotifiers();
                beaconManager.addRangeNotifier(beaconRangeNotifier);
                startBeaconRangingInRegion(region);
            }
        });
        startBeaconMonitoringInRegion();

//        startBeaconRangingInRegion(null);
//        beaconManager.removeAllRangeNotifiers();
//        beaconManager.addRangeNotifier(beaconRangeNotifier);
    }

    private void startBeaconMonitoringInRegion() {
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region(FOREGROUND_BEACON_RANGE_ID, uuid != null ? Identifier.parse(uuid) : null, null, null));
        } catch (RemoteException e) {
            Log.d("Beacon", "beacon error");

        }
    }

    private void startBeaconRangingInRegion(Region region) {
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.d("Beacon", "beacon error");

        }
    }

    private void stopBeaconRangingInRegion(Region region) {
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.d("Beacon", "beacon error");

        }
    }

    private int noBeaconCounter;
    private RangeNotifier beaconRangeNotifier = new RangeNotifier() {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (beacons.size() > 0) {
                noBeaconCounter = 0;
                Beacon beacon = selectValidBeacon((ArrayList<Beacon>) beacons);

                if (trackedBeacon == null || !beacon.equals(trackedBeacon)) {
                    trackedBeacon = beacon;
                    fetchLocation();
                    EventBus.getDefault().post(toBeaconEvent(beacon));
                    startActivity(NotificationIntentFactory.getDefaultIntent(getApplicationContext())
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                            .putExtra(Constants.EXTRA.BEACON_EVENT, toBeaconEvent(beacon)));
                }


            } else if (noBeaconCounter > 10) {
//                trackedBeacon = null;
//                EventBus.getDefault().post(toBeaconEvent(null));
            } else {
                noBeaconCounter++;
            }
        }
    };

    private Beacon selectValidBeacon(ArrayList<Beacon> beaconList) {
        Collections.sort(beaconList, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon o1, Beacon o2) {
                return o1.getDistance() > o2.getDistance() ? 1 : -1;
            }
        });
        int trackedBeaconIndex = trackedBeacon != null ? beaconList.indexOf(trackedBeacon) : -1;
        trackedBeacon = trackedBeaconIndex >= 0 ? beaconList.get(trackedBeaconIndex) : trackedBeacon;

        final Beacon newBeacon = beaconList.get(0);

        if (trackedBeacon != null && !newBeacon.equals(trackedBeacon) && trackedBeaconIndex != -1 && Math.abs(newBeacon.getDistance() - trackedBeacon.getDistance()) < Constants.BEACON_DISTANCE_BUFFER) {
            return trackedBeacon;
        } else {

            return newBeacon;
        }

    }


    private void showNotification(GetLocationData locationData) {
        try
        {
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    NotificationIntentFactory.getDefaultIntent(this), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "general")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(ContextCompat.getColor(this, R.color.colorGreen))
                    .setContentTitle(locationData != null ? locationData.getOrganization().getName() : getString(R.string.app_name))
                    .setContentText(locationData != null ? locationData.getPremise() : getString(R.string.beacon_service_notification_msg_default))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentIntent(contentIntent)
                    .setOnlyAlertOnce(true)

                    .setPriority(NotificationCompat.PRIORITY_MAX);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                notificationManager.createNotificationChannel(getNotificationChannel());
            }

            notificationManager.notify(BEACON_SERVICE_NOTIFICATION_ID, mBuilder.build());
        }
        catch (Exception e)
        {

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel getNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("general", "general",
                NotificationManager.IMPORTANCE_DEFAULT);

        return channel;
    }

    private void fetchLocation() {
        if (trackedBeacon != null) {
            ProtectApiHelper.getInstance().getLocation(AppSession.getInstance().getAccessToken(), trackedBeacon.getId2().toString(), trackedBeacon.getId3().toString(), new Callback<GenericResponseModel<GetLocationData>>() {
                @Override
                public void onResponse(Call<GenericResponseModel<GetLocationData>> call, Response<GenericResponseModel<GetLocationData>> response) {
                    if (response != null &&
                            response.body() != null &&
                            response.body().getData() != null) {

                        showNotification(response.body().getData());
                    }
                }

                @Override
                public void onFailure(Call<GenericResponseModel<GetLocationData>> call, Throwable t) {

                }
            });
        }
    }
    public BeaconEvent getTrackedBeaconEvent()
    {
        return toBeaconEvent(trackedBeacon);
    }
    private void getDashboardIntent() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                NotificationIntentFactory.getDefaultIntent(this), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
