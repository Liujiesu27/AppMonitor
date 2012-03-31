
package com.eee168.appmonitor;

import com.eee168.appmonitor.apk.ApksManager;
import com.eee168.appmonitor.apk.MonitedApk;
import com.eee168.appmonitor.util.WowClick;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class LogMonitor extends Thread {
    private static final String TAG = "LogMonitor";

    private static final String LOGCAT_CMD = "logcat -v time ActivityManager:I RegisterService:I *:S";

    private static final String INSTALL_REGEX = "android.intent.action.PACKAGE_ADDED package:%s";

    private static final String INSTALL_REGEX_HEAD = "android.intent.action.PACKAGE_ADDED package:";

    private static final String UNINSTALL_REGEX = "android.intent.action.PACKAGE_REMOVED package:%s";

    private static final String UNINSTALL_REGEX_HEAD = "android.intent.action.PACKAGE_REMOVED package:";

    private static final String START_REGEX = "Start proc %s for activity ";

    private static final String START_REGEX_HEAD = "Start proc";

    private static final String STOP_REGEX = "Process %s has died";

    private static final String STOP_REGEX_HEAD = "Process";

    private static LogMonitor mInstance = null;

    private Context mContext = null;

    private static ApksManager mApksManager = null;

    private static boolean mIsMonitoring = false;

    private LogMonitor(Context context) {
        Log.d(TAG, "LogMonitor");
        mContext = context;
        mApksManager = ApksManager.getInstance(mContext);
    }

    public static synchronized LogMonitor getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LogMonitor(context);
        }
        return mInstance;
    }

    public boolean isMonitoring() {
        return mIsMonitoring;
    }

    @Override
    public void run() {
        Log.d(TAG, "run " + this.getId() + " " + mIsMonitoring);
        monitor();
    }

    public void detectMonitor() {
        if (!mIsMonitoring) {
            Log.d(TAG, "monitor again ");
            monitor();
        } else {
            Log.d(TAG, "monitor is alive");
        }
    }

    private synchronized void monitor() {
        mIsMonitoring = true;

        BufferedReader reader = getLogReader();
        String logLine = null;
        try {
            Map<String, MonitedApk> monitoredApks = mApksManager.getMonitoredApks();
            while ((logLine = reader.readLine()) != null) {
//                String dateTime = logLine.substring(0, 18);
                String packageName = null;
                if (isInstallLog(logLine)) {
                    packageName = getPackageNameFromLog(logLine, INSTALL_REGEX_HEAD);
                    mApksManager.refresh();
                    MonitedApk monitedApk = monitoredApks.get(packageName);
                    if (monitedApk != null) {
                        WowClick.apkInstall(mContext, monitedApk);
                    }
                } else if (isUninstallLog(logLine)) {
                    packageName = getPackageNameFromLog(logLine, UNINSTALL_REGEX_HEAD);
                    MonitedApk monitedApk = monitoredApks.get(packageName);
                    if (monitedApk != null) {
                        WowClick.apkUninstall(mContext, monitedApk);
                        mApksManager.refresh();
                    }
                } else if (isStartLog(logLine)) {
                    packageName = getPackageNameFromLog(logLine, START_REGEX_HEAD);
                    MonitedApk monitedApk = monitoredApks.get(packageName);
                    if (monitedApk != null) {
                        WowClick.apkStart(mContext, monitedApk);
                    }
                } else if (isStopLog(logLine)) {
                    packageName = getPackageNameFromLog(logLine, STOP_REGEX_HEAD);
                    MonitedApk monitedApk = monitoredApks.get(packageName);
                    if (monitedApk != null) {
                        WowClick.apkStop(mContext, monitedApk);
                    }
                }

            }
            clearLog();
        } catch (IOException e) {
            Log.e(TAG, "monitor error", e);
        } finally {
            Log.d(TAG, "monitor finish");

            mIsMonitoring = false;
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "close reader error", e);
                }
            }
        }
    }

    private BufferedReader getLogReader() {
        Process process = exec(String.format(LOGCAT_CMD));
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader;
    }

    private boolean isSpecifiedLog(String logLine, String pkgName, String regex) throws IOException {
        int index = -1;
        boolean flag = false;
        if (logLine != null && logLine.length() > 0 && pkgName != null && pkgName.length() > 0) {
            index = logLine.indexOf(String.format(regex, pkgName));
            flag = index >= 0;
        }
        return flag;
    }

    private boolean isInstallLog(String logLine, String pkgName) throws IOException {
        return isSpecifiedLog(logLine, pkgName, INSTALL_REGEX);
    }

    private boolean isUninstallLog(String logLine, String pkgName) throws IOException {
        return isSpecifiedLog(logLine, pkgName, UNINSTALL_REGEX);
    }

    private boolean isStartLog(String logLine, String pkgName) throws IOException {
        return isSpecifiedLog(logLine, pkgName, START_REGEX);
    }

    private boolean isStopLog(String logLine, String pkgName) throws IOException {
        return isSpecifiedLog(logLine, pkgName, STOP_REGEX);
    }

    private boolean isSpecifiedLog(String logLine, String regex) throws IOException {
        int index = -1;
        boolean flag = false;
        if (logLine != null && logLine.length() > 0) {
            index = logLine.indexOf(regex);
            flag = index >= 0;
        }
        return flag;
    }

    private boolean isInstallLog(String logLine) throws IOException {
        return isSpecifiedLog(logLine, INSTALL_REGEX_HEAD);
    }

    private boolean isUninstallLog(String logLine) throws IOException {
        return isSpecifiedLog(logLine, UNINSTALL_REGEX_HEAD);
    }

    private boolean isStartLog(String logLine) throws IOException {
        return isSpecifiedLog(logLine, START_REGEX_HEAD);
    }

    private boolean isStopLog(String logLine) throws IOException {
        return isSpecifiedLog(logLine, STOP_REGEX_HEAD);
    }

    private String getPackageNameFromLog(String logLine, String headRegex) {
        String pkgName = null;
        if (logLine != null && logLine.length() > 0) {
            int headIndex = logLine.indexOf(headRegex) + headRegex.length();
            int tailIndex = -1;
            if (tailIndex < 0) {
                tailIndex = logLine.indexOf(" ", headIndex + 1);
            }
            if (tailIndex < 0) {
                tailIndex = logLine.length();
            }

            pkgName = logLine.substring(headIndex, tailIndex).trim();
        }

        return pkgName;
    }

    private void clearLog() {
        Log.d(TAG, "clear log");
        exec("logcat -c");
    }

    private Process exec(String cmd) {
        Process process = null;
        try {
            Log.d(TAG, "exec : " + cmd);
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Log.e(TAG, "exec " + cmd + " error", e);
        }
        return process;
    }

}
