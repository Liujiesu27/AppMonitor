
package com.eee168.appmonitor.apk;

public class MonitedApk {
    private String packageName;

    private String appName;

    private ApkTailData apkTailData;

    public MonitedApk(String packageName, String appName, ApkTailData apkTailData) {
        this.packageName = packageName;
        this.appName = appName;
        this.apkTailData = apkTailData;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public ApkTailData getApkTailData() {
        return apkTailData;
    }

    public void setApkTailData(ApkTailData apkTailData) {
        this.apkTailData = apkTailData;
    }

    
}
