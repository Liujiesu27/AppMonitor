
package com.eee168.appmonitor.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;

public class Helper {
    private static final String TAG = "Helper";

    public static void testCode(String src) {
        if (src != null && src.length() > 0) {
            try {

                System.out.println("UTF-8 > GBK " + new String(src.getBytes("UTF-8"), "GBK"));
                System.out.println("UTF-8 > ISO8859-1 "
                        + new String(src.getBytes("UTF-8"), "ISO8859-1"));
                System.out.println("GBK > UTF-8 " + new String(src.getBytes("GBK"), "UTF-8"));
                System.out.println("GBK > ISO8859-1 "
                        + new String(src.getBytes("GBK"), "ISO8859-1"));
                System.out.println("ISO8859-1 > UTF-8 "
                        + new String(src.getBytes("ISO8859-1"), "UTF-8"));
                System.out.println("ISO8859-1 > GBK "
                        + new String(src.getBytes("ISO8859-1"), "GBK"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public static String getByteArrayHexString(byte[] data) {
        StringBuffer sb = new StringBuffer();
        sb.append(Hex.encodeHexStr(data));
        return sb.toString();
    }

    public static boolean compareByteArray(byte[] arg1, byte[] arg2) {

        if (arg1 == null || arg2 == null) {
            throw new IllegalArgumentException("byte array can not be null");
        }

        boolean flag = true;
        if (arg1.length != arg2.length) {
            flag = false;
        } else {
            for (int i = 0; i < arg1.length; i++) {
                if (arg1[i] != arg2[i]) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = telephonyManager.getDeviceId();
        return IMEI;
    }

    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        return mac;
    }
}
