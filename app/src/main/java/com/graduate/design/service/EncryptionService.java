package com.graduate.design.service;

import com.graduate.design.proto.FileUpload;
import com.graduate.design.proto.SearchFile;
import com.graduate.design.proto.SendSearchToken;

import java.util.List;

public interface EncryptionService {
    // 利用sha-256根据用户名和密码生成主密钥
    byte[] SHA256(byte[] input);
    byte[] SHA512(byte[] input);

    byte[] encryptByAES256(String plaintext, byte[] secretKey);
    byte[] encryptByAES256(byte[] plaintext, byte[] secretKey);

    byte[] decryptByAES256(String ciphertext, byte[] secretKey);
    byte[] decryptByAES256(byte[] ciphertext, byte[] secretKey);

    FileUpload.indexToken uploadIndex(Long id, String word);

    SendSearchToken.SearchToken getSearchToken(String word);

    List<Long> getNodeIdByCw(List<String> Cw, String word);
}