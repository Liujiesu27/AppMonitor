
package com.eee168.appmonitor.apk;

import com.eee168.appmonitor.util.EncryptUtils;
import com.eee168.appmonitor.util.Helper;
import com.eee168.appmonitor.util.JSONUtils;
import com.eee168.appmonitor.util.NetByteConvert;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ApkTailData {
    private static final String TAG = "ApkTailData";

    public static final String CHARSET = "UTF-8";

    public static final String STORE = "store";

    public static final String SALER = "saler";

    public static final String TIME = "time";

    private static final byte[] TAIL_TAG = new byte[]{0x12,0x34};

    private static byte VERSION = 0x01;

    public static final int TAIL_TAG_LENGTH = TAIL_TAG.length;

    public static final int SIZE_LENGTH = 4;

    public static final int VERSION_LENGTH = 1;
    
    private static final byte[] KEY = new byte[]{0x21,0x51,0x40,0x57,0x33,0x65,0x34,0x72};

    private int mDataSize;

    private int mPacketSize;

    private String mStore;

    private String mSaler;

    private String mTime;

    private byte[] mTailPacket;

    private String mJsonData;

    public ApkTailData(String store, String saler, String dateTime) {
        mStore = store;
        mSaler = saler;
        mTime = dateTime;
        process(mStore, mSaler, mTime);
    }

    private ApkTailData() {
    }

    public String getStore() {
        return mStore;
    }

    public String getSaler() {
        return mSaler;
    }

    public String getTime() {
        return mTime;
    }
    
    public int getTotalSize() {
        return mDataSize + TAIL_TAG_LENGTH + SIZE_LENGTH + VERSION_LENGTH;
    }

    private void process(String storeId, String salerId, String dateTime) {
        mJsonData = getJSON(storeId, salerId, dateTime);
        try {
            byte[] data = mJsonData.getBytes(CHARSET);
            byte[] encryptData = EncryptUtils.encryptByDES(data, KEY);
            if (encryptData != null && encryptData.length > 0) {
                mDataSize = encryptData.length;
                mPacketSize = mDataSize + VERSION_LENGTH + TAIL_TAG_LENGTH + SIZE_LENGTH;
                mTailPacket = new byte[mPacketSize];
                System.arraycopy(encryptData, 0, mTailPacket, 0, encryptData.length);
                byte[] lengthBytes = NetByteConvert.intToBytes(mDataSize);
                System.arraycopy(lengthBytes, 0, mTailPacket, encryptData.length, lengthBytes.length);
                byte[] versionBytes = new byte[]{VERSION};
                System.arraycopy(versionBytes, 0, mTailPacket, encryptData.length + SIZE_LENGTH,
                        versionBytes.length);
                System.arraycopy(TAIL_TAG, 0, mTailPacket, mPacketSize - TAIL_TAG_LENGTH,
                        TAIL_TAG_LENGTH);

            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "process apk error" ,e);
        } catch (BadPaddingException e) {
            Log.e(TAG, "process apk error" ,e);
        }
    }

    private String getJSON(String storeId, String salerId, String time) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(STORE, storeId);
        params.put(SALER, salerId);
        params.put(TIME, time);
        return JSONUtils.getJson(params);
    }

    public byte[] getTailPacket() {
        return mTailPacket;
    }

    public static ApkTailData parse(File file) {
        if (!checkTailTag(file)) {
            return null;
        }
        ApkTailData apkTailData = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long length = raf.length();
            raf.seek(length - (VERSION_LENGTH + TAIL_TAG_LENGTH + SIZE_LENGTH));
            byte[] sizeBytes = new byte[SIZE_LENGTH];
            raf.read(sizeBytes);
            int size = NetByteConvert.bytesToInt(sizeBytes);
            byte[] tailPacket = new byte[VERSION_LENGTH + TAIL_TAG_LENGTH + SIZE_LENGTH + size];
            raf.seek(length - (VERSION_LENGTH + TAIL_TAG_LENGTH + SIZE_LENGTH + size));
            raf.read(tailPacket);
            apkTailData = parse(tailPacket);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "parse apk taildata error" ,e);
        } catch (IOException e) {
            Log.e(TAG, "parse apk taildata error" ,e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    Log.e(TAG, "close RandomAccessFile error" ,e);
                }
            }
        }
        return apkTailData;
    }

    public static ApkTailData parse(byte[] tailPacket) {
        ApkTailData apkTail = null;
        if (tailPacket != null && checkTailTag(tailPacket)
                && tailPacket.length > (VERSION_LENGTH + TAIL_TAG_LENGTH + SIZE_LENGTH)) {
            try {
                apkTail = new ApkTailData();
                apkTail.VERSION = tailPacket[tailPacket.length - VERSION_LENGTH - TAIL_TAG_LENGTH];
                apkTail.mPacketSize = tailPacket.length;
                byte[] dataSizeBytes = new byte[SIZE_LENGTH];
                System.arraycopy(tailPacket, tailPacket.length - VERSION_LENGTH - TAIL_TAG_LENGTH
                        - SIZE_LENGTH, dataSizeBytes, 0, SIZE_LENGTH);
                apkTail.mDataSize = NetByteConvert.bytesToInt(dataSizeBytes);
                byte[] encryptedJsonData = new byte[apkTail.mDataSize];
                System.arraycopy(tailPacket, 0, encryptedJsonData, 0, apkTail.mDataSize);
                byte[] jsonData = EncryptUtils.decryptByDES(encryptedJsonData, KEY);
                apkTail.mJsonData = new String(jsonData, CHARSET);
                apkTail.mStore = JSONUtils.getString(apkTail.mJsonData, STORE);
                apkTail.mSaler = JSONUtils.getString(apkTail.mJsonData, SALER);
                apkTail.mTime = JSONUtils.getString(apkTail.mJsonData, TIME);
                apkTail.mTailPacket = tailPacket;
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (InvalidKeyException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (NoSuchPaddingException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (IllegalBlockSizeException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (BadPaddingException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (InvalidKeySpecException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            } catch (InvalidAlgorithmParameterException e) {
                Log.e(TAG, "parse apk taildata error" ,e);
            }

        }
        return apkTail;
    }
    
    public static boolean checkTailTag(byte[] tailData) {
        if (tailData != null && tailData.length >= TAIL_TAG_LENGTH) {
            byte[] tail = new byte[TAIL_TAG_LENGTH];
            System.arraycopy(tailData, tailData.length - TAIL_TAG_LENGTH, tail, 0, TAIL_TAG_LENGTH);
            return Helper.compareByteArray(tail, TAIL_TAG);
        } else {
            return false;
        }
    }

    public static boolean checkTailTag(File file) {
        boolean flag = false;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long length = raf.length();
            raf.seek(length - TAIL_TAG_LENGTH);
            byte[] tailTag = new byte[TAIL_TAG_LENGTH];
            raf.read(tailTag);
            flag = checkTailTag(tailTag);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "" ,e);
        } catch (IOException e) {
            Log.e(TAG, "" ,e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    Log.e(TAG, "" ,e);
                }
            }
        }

        return flag;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tail tag : " + Helper.getByteArrayHexString(this.TAIL_TAG));
        sb.append("\nversion : " + this.VERSION);
        sb.append("\ndata size : " + this.mDataSize);
        sb.append("\njson data : " + this.mJsonData);
        sb.append("\npacket size : " + this.mPacketSize);
        sb.append("\npacket : " + Helper.getByteArrayHexString(this.mTailPacket));
        return sb.toString();
    }

}
