package com.graduate.design.utils;

import java.util.Random;

public class ByteUtils {
    public static byte[] mergeBytes(byte[] b1, byte[] b2){
        if (b1.length==0 || b2.length==0) return null;
        byte[] b3 = new byte[b1.length+ b2.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        return b3;
    }

    public static byte[] xor(byte[] b1, byte[] b2){
        byte[] longByte,shortByte;
        if (b1.length>= b2.length){
            longByte = b1;
            shortByte = b2;
        }else {
            longByte = b2;
            shortByte = b1;
        }
        byte[] bytes = new byte[longByte.length];
        int i = 0;
        for (;i< shortByte.length;i++){
            bytes[i] = (byte) (shortByte[i]^longByte[i]);
        }
        for (;i< longByte.length;i++){
            bytes[i] = longByte[i];
        }
        return bytes;
    }

    public static byte[] getRandomBytes(int len){
        Random random = new Random();
        byte[] res = new byte[len];
        for (int i=0;i<len;i++){
            res[i] = (byte) random.nextInt(10);
        }
        return res;
    }
}
