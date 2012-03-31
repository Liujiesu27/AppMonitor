
package com.eee168.appmonitor;

import com.eee168.appmonitor.apk.ApksManager;
import com.eee168.appmonitor.util.ApkUtils;
import com.eee168.appmonitor.util.FileTools;
import com.eee168.appmonitor.util.Helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppMonitorActivity extends Activity {
    private static final String TAG = "ApkMonitorActivity";

    private Button mBtnShowApps;

    private Button mBtnShowApks;

    private TextView mTVApps;

    public static final int SHOW = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    String appsMsg = (String) msg.obj;
                    mTVApps.setText(appsMsg);
                    break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTVApps = (TextView) findViewById(R.id.tv_apps);
        mBtnShowApps = (Button) findViewById(R.id.btn_showapps);
        mBtnShowApps.setOnClickListener(new ShowAppsListener());
        mBtnShowApks = (Button) findViewById(R.id.btn_showapks);
        mBtnShowApks.setOnClickListener(new ShowApksListener());

    }

    private class ShowApksListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            new ApksThread().start();
        }

        private class ApksThread extends Thread {

            @Override
            public void run() {
                // List<String> packageList =
                // ApkUtils.getApkPath(ApkMonitorActivity.this);
                Map<String, PackageInfo> packageMap = ApkUtils
                        .getApkPathMap(AppMonitorActivity.this);
                ApksManager manager = ApksManager.getInstance(AppMonitorActivity.this);
                manager.refresh();
                if (packageMap != null && packageMap.size() > 0) {
                    StringBuffer names = new StringBuffer();
                    Message msg = mHandler.obtainMessage(SHOW);
                    Set<String> keys = packageMap.keySet();
                    for (String key : keys) {
                        PackageInfo value = packageMap.get(key);
                        names.append(key + ":" + value.applicationInfo.name + "\n");
                    }
                    msg.obj = names.toString();
                    mHandler.sendMessage(msg);
                }
            }

            /*
             * @Override public void run() { SimpleDateFormat sdf = new
             * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
             * sdf.setTimeZone(TimeZone.getTimeZone("GMT")); Calendar cal =
             * Calendar.getInstance(); String mTime = sdf.format(cal.getTime());
             * ApkTailData apkTail = new ApkTailData("suning", "zhangsan",
             * mTime); ApkTailData apk =
             * ApkTailData.parse(apkTail.getTailPacket()); Message msg =
             * mHandler.obtainMessage(SHOW); msg.obj = apk.toString();
             * mHandler.sendMessage(msg); }
             */

        }

    }

    private class ShowAppsListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            new AppsThread().start();
        }

        private class AppsThread extends Thread {

            @Override
            public void run() {
                test4();
            }

            private void test4() {
                ActivityManager mActivityManager = (ActivityManager) AppMonitorActivity.this
                        .getSystemService(AppMonitorActivity.this.ACTIVITY_SERVICE);

                List<RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(300);

                int i = 0 ;
                for (RunningServiceInfo serviceInfo : mServiceList) {
                    if (serviceInfo != null) {
                        Log.d(TAG, ""+i++);
                        Log.d(TAG, serviceInfo.service.getPackageName());
                        Log.d(TAG, serviceInfo.service.getClassName());
                    }
                }
            }

            private void test3() {
                AppMonitorActivity.this.startService(new Intent(AppMonitorActivity.this,
                        MonitorService.class));
            }

            private void test2() {
                String[] pkgNames = new String[] {
                        "com.su27.remotecontrol", "com.eee168.apkmonitor"
                };
                ApksManager manager = ApksManager.getInstance(AppMonitorActivity.this);
                manager.refresh();
                // LogMonitor.getInstance(AppMonitorActivity.this).monitor();
            }

            private void test1() {
                String packageName = "com.su27.remotecontrol";
                String[] pkgNames = new String[] {
                        "com.su27.remotecontrol", "com.eee168.apkmonitor"
                };
                File file = null;
                try {
                    file = ApkUtils.getInstallApkFile(AppMonitorActivity.this, packageName);
                    // LogMonitor.getInstance(AppMonitorActivity.this).monitor();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (file != null) {
                    Message msg = mHandler.obtainMessage(SHOW);
                    try {
                        byte[] data = FileTools.getBytesFromFile(file);
                        Log.d(TAG, file.getAbsolutePath());
                        if (data != null && data.length > 5) {
                            Log.d(TAG, "" + data.length);
                            byte[] tailData = new byte[5];
                            System.arraycopy(data, data.length - 5, tailData, 0, 5);
                            msg.obj = Helper.getByteArrayHexString(tailData);
                            mHandler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
}
