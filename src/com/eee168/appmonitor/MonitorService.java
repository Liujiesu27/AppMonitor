package com.eee168.appmonitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MonitorService extends Service {
    private static final String TAG = "MonitorService";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "start service");
        LogMonitor monitor = LogMonitor.getInstance(MonitorService.this);
        if(!monitor.isAlive()){
            monitor.start();
        } else  {
            monitor.detectMonitor();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    

    

}
