
package com.eee168.appmonitor;

import com.eee168.appmonitor.util.Helper;

import android.content.Context;

public class Device {
    private String IMEI = null;

    private String MAC = null;

    private Context mContext = null;

    private static Device mInstance = null;

    private Device(Context context) {
        mContext = context;
        IMEI = Helper.getIMEI(mContext);
        MAC = Helper.getMacAddress(mContext);
    }

    public synchronized static Device getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Device(context);
        }
        return mInstance;
    }

    public String getIMEI() {
        return IMEI;
    }

    public String getMAC() {
        return MAC;
    }

}
