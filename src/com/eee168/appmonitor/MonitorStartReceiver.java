
package com.eee168.appmonitor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class MonitorStartReceiver extends BroadcastReceiver {
    private static final String TAG = "MonitorStartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean needStartService = false;
        if (intent.getAction().compareTo(Intent.ACTION_BOOT_COMPLETED) == 0) {
            Log.d(TAG, "boot completed");
            needStartService = true;
        } else if (intent.getAction().compareTo(Intent.ACTION_PACKAGE_INSTALL) == 0) {
            Log.d(TAG, "package install");
            needStartService = true;
        } else if (intent.getAction().compareTo(Intent.ACTION_PACKAGE_ADDED) == 0) {
            Log.d(TAG, "package added");
            needStartService = true;
        } else if (intent.getAction().compareTo(Intent.ACTION_PACKAGE_REMOVED) == 0) {
            Log.d(TAG, "package removed");
            needStartService = true;
        }

        if (needStartService) {
            boolean isServiceRunning = isServiceRunning(context,
                    context.getApplicationInfo().packageName);
            if (!isServiceRunning) {
                context.startService(new Intent(context, MonitorService.class));
            }
        }

    }

    private boolean isServiceRunning(Context context, String servicePkgName) {
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> mServiceList = mActivityManager
                .getRunningServices(1024);

        boolean flag = false;
        for (RunningServiceInfo serviceInfo : mServiceList) {
            if (serviceInfo != null) {
                if (flag = serviceInfo.service.getPackageName().equals(servicePkgName)) {
                    Log.d(TAG, servicePkgName + " is running.");
                    break;
                }
            }
        }

        return flag;
    }

}
