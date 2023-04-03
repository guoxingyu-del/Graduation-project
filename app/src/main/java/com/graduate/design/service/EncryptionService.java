package com.graduate.design.service;

public interface EncryptionService {
    // 利用sha-256根据用户名和密码生成主密钥
    byte[] getSecretKey(String username, String password);

    byte[] encryptByAES128(String plaintext, byte[] secretKey);
    byte[] encryptByAES128(byte[] plaintext, byte[] secretKey);

    byte[] decryptByAES128(String ciphertext, byte[] secretKey);
    byte[] decryptByAES128(byte[] ciphertext, byte[] secretKey);
}
