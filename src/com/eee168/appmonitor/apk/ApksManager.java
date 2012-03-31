
package com.eee168.appmonitor.apk;

import com.eee168.appmonitor.util.ApkUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ApksManager {
    private static final String TAG = "ApksManager";

    private static final Map<String,MonitedApk> mMonitoredApkMap = new HashMap<String,MonitedApk>();

    private static ApksManager mInstance = null;

    private Context mContext = null;

    private ApksManager(Context context) {
        mContext = context;
        refresh();
    }

    public static synchronized ApksManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApksManager(context);
        }
        return mInstance;
    }

    public Map<String,MonitedApk> getMonitoredApks() {
        return mMonitoredApkMap;
    }

    public synchronized void refresh() {
        long begin_time = System.currentTimeMillis();
        mMonitoredApkMap.clear();
        Map<String, PackageInfo> apkPathMap = ApkUtils.getApkPathMap(mContext);
        PackageManager pm = mContext.getPackageManager();
        if (apkPathMap != null && apkPathMap.size() > 0) {
            Set<Entry<String, PackageInfo>> packageSet = apkPathMap.entrySet();
            if (packageSet != null && packageSet.size() > 0) {
                for (Entry<String, PackageInfo> pkgEntry : packageSet) {
                    PackageInfo pkg = pkgEntry.getValue();
                    File file = new File(pkg.applicationInfo.sourceDir);
                    boolean isMonitoredApk = ApkTailData.checkTailTag(file);
                    if (isMonitoredApk) {
                        Log.d(TAG, pkg.packageName);
                        ApkTailData tailData = ApkTailData.parse(file);
                        MonitedApk monitedApk = new MonitedApk(pkg.packageName,pkg.applicationInfo.loadLabel(pm).toString(),tailData);
                        mMonitoredApkMap.put(pkg.packageName, monitedApk);
                    }
                }
            }
        }

        Log.d(TAG, "refresh time " + (System.currentTimeMillis() - begin_time));
    }

}
