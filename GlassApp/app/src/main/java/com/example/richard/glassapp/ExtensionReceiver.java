package com.example.richard.glassapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by richard on 17. 3. 7.
 */

public class ExtensionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(Constants.LOG_TAG, "onReceive: " + intent.getAction());
        intent.setClass(context, AppSampleExtensionService.class);
        context.startService(intent);
    }
}
