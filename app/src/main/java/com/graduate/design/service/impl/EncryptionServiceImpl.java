package com.graduate.design.service.impl;

import android.os.Build;

import com.graduate.design.service.EncryptionService;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionServiceImpl implements EncryptionService {
    @Override
    public byte[] getSecretKey(String username, String password) {
        MessageDigest messageDigest;
        byte[] mainSecret16;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((username+password).getBytes(StandardCharsets.UTF_8));
            byte[] mainSecret = messageDigest.digest();
            // 截断，取前16个字节
            mainSecret16 = new byte[16];
            for(int i=0;i<16;i++) {
                mainSecret16[i] = mainSecret[i];
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return mainSecret16;
    }

    @Override
    public byte[] encryptByAES128(String plaintext, byte[] secretKey) {
        byte[] plainBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        return encryptByAES128(plainBytes, secretKey);
    }

    @Override
    public byte[] encryptByAES128(byte[] plaintext, byte[] secretKey) {
        byte[] encryptRes;
        try {
            SecretKeySpec spec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/加密/填充"
            IvParameterSpec iv = new IvParameterSpec(GraduateDesignApplication.IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, spec, iv);
            encryptRes = cipher.doFinal(plaintext);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return encryptRes;
    }

    @Override
    public byte[] decryptByAES128(String ciphertext, byte[] secretKey) {
        byte[] cipherBytes = FileUtils.Base64ToBytes(ciphertext);
        return decryptByAES128(cipherBytes, secretKey);
    }

    @Override
    public byte[] decryptByAES128(byte[] ciphertext, byte[] secretKey) {
        byte[] decryptRes;
        try {
            SecretKeySpec spec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // "算法/加密/填充"
            IvParameterSpec iv = new IvParameterSpec(GraduateDesignApplication.IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, spec, iv);
            decryptRes = cipher.doFinal(ciphertext);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return decryptRes;
    }
}
