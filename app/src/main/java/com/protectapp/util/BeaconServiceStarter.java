package com.protectapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BeaconServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,AppBeaconService.class));
    }
}
