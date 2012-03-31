
package com.eee168.appmonitor.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class EncryptUtils {

    private static byte[] iv1 = {
            (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB,
            (byte) 0xCD, (byte) 0xEF
    };

    public static byte[] encryptByDES(byte[] source, byte[] keys) throws InvalidKeyException,
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(iv1);
        DESKeySpec dks = new DESKeySpec(keys);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte data[] = source;
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData;
    }

    public static byte[] decryptByDES(byte[] source, byte[] keys) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        IvParameterSpec iv = new IvParameterSpec(iv1);
        DESKeySpec dks = new DESKeySpec(keys);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] clearByte = cipher.doFinal(source);
        return clearByte;
    }

    public static void main(String[] args) {
//        byte[] source = new byte[] {
//                0x01, 0x02, 0x03, 0x04
//        };
//        byte[] dest;
//        try {
//            dest = encryptByDES(source, "abcdefgh".getBytes("UTF-8"));
//            System.out.println(Helper.getByteArrayHexString(dest));
//            byte[] src = decryptByDES(dest, "abcdefgh".getBytes("UTF-8"));
//            System.out.println(Helper.getByteArrayHexString(src));
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        try {
            System.out.println(Helper.getByteArrayHexString("!Q@W3e4r".getBytes("UTF-8")));
            System.out.println(new String(new byte[]{0x21,0x51,0x40,0x57,0x33,0x65,0x34,0x72}));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        

    }
}
