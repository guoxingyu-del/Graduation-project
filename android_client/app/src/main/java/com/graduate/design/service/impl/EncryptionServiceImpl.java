package com.graduate.design.service.impl;

import android.os.Build;

import com.graduate.design.entity.BiIndex;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.proto.SearchFile;
import com.graduate.design.proto.SendSearchToken;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.utils.ByteUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionServiceImpl implements EncryptionService {
    @Override
    public byte[] SHA256(byte[] input) {
        MessageDigest messageDigest;
        byte[] mainSecret;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input);
            mainSecret = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return mainSecret;
    }

    @Override
    public byte[] SHA512(byte[] input) {
        MessageDigest messageDigest;
        byte[] output;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(input);
            output = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    @Override
    public byte[] encryptByAES256(String plaintext, byte[] secretKey) {
        byte[] plainBytes = plaintext.getBytes(StandardCharsets.ISO_8859_1);
        return encryptByAES256(plainBytes, secretKey);
    }

    @Override
    public byte[] encryptByAES256(byte[] plaintext, byte[] secretKey) {
        byte[] encryptRes;
        try {
            SecretKeySpec spec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/加密/填充"
            IvParameterSpec iv = new IvParameterSpec(GraduateDesignApplication.IV.getBytes(StandardCharsets.ISO_8859_1));
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
    public byte[] decryptByAES256(String ciphertext, byte[] secretKey) {
        byte[] cipherBytes = FileUtils.Base64ToBytes(ciphertext);
        return decryptByAES256(cipherBytes, secretKey);
    }

    @Override
    public byte[] decryptByAES256(byte[] ciphertext, byte[] secretKey) {
        byte[] decryptRes;
        try {
            SecretKeySpec spec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // "算法/加密/填充"
            IvParameterSpec iv = new IvParameterSpec(GraduateDesignApplication.IV.getBytes(StandardCharsets.ISO_8859_1));
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

    public byte[] HmacSha256(byte[] key, byte[] data){
        byte[] res;
        try {
            SecretKeySpec secret = new SecretKeySpec(key, "HmacSha256");
            Mac mac = Mac.getInstance("HmacSha256");
            mac.init(secret);
            res = mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public List<String> genKeyPair() {
        List<String> res = new ArrayList<>();
        try {
            // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 初始化密钥对生成器，密钥大小为96-1024位
            keyPairGen.initialize(1024,new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
            String privateKeyString = FileUtils.bytes2Base64(privateKey.getEncoded());
            String publicKeyString = FileUtils.bytes2Base64(publicKey.getEncoded());
            // 将公钥和私钥保存
            res.add(publicKeyString);
            res.add(privateKeyString);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public String encryptByRSA(String plaintext, String publicKey) {
        String res;
        try {
            byte[] publicKeyBytes = FileUtils.Base64ToBytes(publicKey);
            byte[] plaintextBytes = FileUtils.Base64ToBytes(plaintext);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] bytes = cipher.doFinal(plaintextBytes);
            res = FileUtils.bytes2Base64(bytes);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public String decryptByRSA(String ciphertext, String privateKey) {
        String res;
        try {
            //64位解码加密后的字符串
            byte[] ciphertextBytes = FileUtils.Base64ToBytes(ciphertext);
            //base64编码的私钥
            byte[] privateKeyBytes = FileUtils.Base64ToBytes(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] here = cipher.doFinal(ciphertextBytes);
            res = FileUtils.bytes2Base64(here);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return res;
    }


    // 上传加密搜索索引
    @Override
    public Common.indexToken uploadIndex(Long id, String word) {
        BiIndex biIndex = GraduateDesignApplication.getBiIndex();
        Map<String, String> lastW = biIndex.getLastW();
        Map<String, String> lastID = biIndex.getLastID();
        byte[] key1 = GraduateDesignApplication.getKey1();
        byte[] key2 = GraduateDesignApplication.getKey2();


        String oldWord = lastW.get(String.valueOf(id)); String oldIDString = lastID.get(word);

        byte[] idBytes = String.valueOf(id).getBytes(StandardCharsets.ISO_8859_1);
        byte[] wordBytes = word.getBytes(StandardCharsets.ISO_8859_1);
        byte[] word_id = ByteUtils.mergeBytes(wordBytes, idBytes); byte[] id_word = ByteUtils.mergeBytes(idBytes, wordBytes);
        byte[] L = HmacSha256(key1, word_id);
        byte[] Rw = ByteUtils.getRandomBytes(32); byte[] Rid = ByteUtils.getRandomBytes(32);
        byte[] kw = HmacSha256(key2, wordBytes); byte[] kid = HmacSha256(key2, idBytes);
        byte[] Cw = encryptByAES256(idBytes, kw); byte[] Cid = encryptByAES256(word, kid);
        byte[] Iw;
        if(oldIDString == null) {
            byte[] Jw = HmacSha256(key2, word_id);
            Iw = SHA512(ByteUtils.mergeBytes(Jw, Rw));
        }
        else {
            Long oldID = Long.parseLong(oldIDString);
            byte[] oldIDBytes = String.valueOf(oldID).getBytes(StandardCharsets.ISO_8859_1);
            byte[] oldL = HmacSha256(key1, ByteUtils.mergeBytes(wordBytes, oldIDBytes));
            byte[] oldJw = HmacSha256(key2, ByteUtils.mergeBytes(wordBytes, oldIDBytes));
            byte[] Jw = HmacSha256(key2, word_id);
            Iw = ByteUtils.xor(SHA512(ByteUtils.mergeBytes(Jw, Rw)), ByteUtils.mergeBytes(oldL, oldJw));
        }
        byte[] Iid;
        if(oldWord==null) {
            byte[] Jid = HmacSha256(key2, id_word);
            Iid = SHA512(ByteUtils.mergeBytes(Jid, Rid));
        }
        else {
            byte[] oldWordBytes = oldWord.getBytes(StandardCharsets.ISO_8859_1);
            byte[] oldL = HmacSha256(key1, ByteUtils.mergeBytes(oldWordBytes, idBytes));
            byte[] oldJid = HmacSha256(key2, ByteUtils.mergeBytes(idBytes, oldWordBytes));
            byte[] Jid = HmacSha256(key2, id_word);
            Iid = ByteUtils.xor(SHA512(ByteUtils.mergeBytes(Jid, Rid)), ByteUtils.mergeBytes(oldL, oldJid));
        }
        lastW.put(String.valueOf(id), word);
        lastID.put(word, String.valueOf(id));

        biIndex.setLastID(lastID);
        biIndex.setLastW(lastW);

        return Common.indexToken.newBuilder().setL(FileUtils.bytes2Base64(L))
                .setIw(FileUtils.bytes2Base64(Iw))
                .setRw(FileUtils.bytes2Base64(Rw))
                .setCw(FileUtils.bytes2Base64(Cw))
                .setIid(FileUtils.bytes2Base64(Iid))
                .setRid(FileUtils.bytes2Base64(Rid))
                .setCid(FileUtils.bytes2Base64(Cid))
                .build();
    }

    @Override
    public SendSearchToken.SearchToken getSearchToken(String word) {
        BiIndex biIndex = GraduateDesignApplication.getBiIndex();
        Map<String, String> lastID = biIndex.getLastID();
        byte[] key1 = GraduateDesignApplication.getKey1();
        byte[] key2 = GraduateDesignApplication.getKey2();

        String idString = lastID.get(word);
        if(idString==null) return null;

        Long id = Long.parseLong(idString);
        byte[] idBytes = String.valueOf(id).getBytes(StandardCharsets.ISO_8859_1);
        byte[] wordBytes = word.getBytes(StandardCharsets.ISO_8859_1);
        byte[] word_id = ByteUtils.mergeBytes(wordBytes, idBytes);
        byte[] L = HmacSha256(key1, word_id);
        byte[] Jw = HmacSha256(key2, word_id);

        return SendSearchToken.SearchToken.newBuilder()
                .setL(FileUtils.bytes2Base64(L))
                .setJw(FileUtils.bytes2Base64(Jw))
                .build();
    }

    @Override
    public List<Long> getNodeIdByCw(List<String> Cw, String word) {
        byte[] key2 = GraduateDesignApplication.getKey2();

        byte[] kw = HmacSha256(key2, word.getBytes(StandardCharsets.ISO_8859_1));
        List<Long> res = new ArrayList<>();
        for(int i=0;i<Cw.size();i++){
            String cw = Cw.get(i);
            byte[] cwBytes = FileUtils.Base64ToBytes(cw);
            byte[] idBytes = decryptByAES256(cwBytes, kw);
            Long id = Long.parseLong(new String(idBytes, StandardCharsets.ISO_8859_1));
            res.add(id);
        }
        return res;
    }


}
