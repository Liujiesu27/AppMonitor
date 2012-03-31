
package com.eee168.appmonitor.util;

import com.eee168.appmonitor.apk.ApkTailData;
import com.eee168.appmonitor.apk.MonitedApk;
import com.wowclick.WowClickAgent;

import android.content.Context;
import android.util.Log;

public class WowClick {
    private static final String TAG = "WowClick";

    private static final String EVENT_INSTALL = "install";

    private static final String EVENT_UNINSTALL = "uninstall";

    private static final String EVENT_START = "start";

    private static final String EVENT_STOP = "stop";

    private static final String LABEL_PACKAGE = "package";

    private static final String LABEL_APP_LABEL = "label";

    private static final String LABEL_STORE = "store";

    private static final String LABEL_SALER = "saler";

    private static final void apkOperate(Context context, final String event,
            MonitedApk monitoredApk) {
        String store = "";
        String saler = "";
        String app = "";
        String pkgName = "";
        if (monitoredApk != null) {
            ApkTailData apkTail = monitoredApk.getApkTailData();
            if (apkTail != null) {
                store = handleLabel(apkTail.getStore());
                saler = handleLabel(apkTail.getSaler());
            }
            app = monitoredApk.getAppName();
            pkgName = monitoredApk.getPackageName();
        }
        Log.d(TAG, event + "," + pkgName + "," + app + "," + store + "," + saler);
        WowClickAgent.onEvent(context, event, LABEL_PACKAGE, pkgName, LABEL_APP_LABEL, app,
                LABEL_STORE, store, LABEL_SALER, saler);
    }

    public static final void apkInstall(Context context, MonitedApk monitoredApk) {
        apkOperate(context, EVENT_INSTALL, monitoredApk);
    }

    public static final void apkUninstall(Context context, MonitedApk monitoredApk) {
        apkOperate(context, EVENT_UNINSTALL, monitoredApk);
    }

    public static final void apkStart(Context context, MonitedApk monitoredApk) {
        apkOperate(context, EVENT_START, monitoredApk);
    }

    public static final void apkStop(Context context, MonitedApk monitoredApk) {
        apkOperate(context, EVENT_STOP, monitoredApk);
    }

    private static String handleLabel(String label) {
        String handleLabel = "";
        if (label != null && label.length() > 0) {
            handleLabel = label.trim();
        }

        return handleLabel;

    }
}
