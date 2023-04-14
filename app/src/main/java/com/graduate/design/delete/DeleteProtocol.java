package com.graduate.design.delete;

import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.impl.EncryptionServiceImpl;
import com.graduate.design.utils.FileUtils;

public class DeleteProtocol {
    static EncryptionService encryptionService = new EncryptionServiceImpl();

    public static String idOpPairCipherGen(byte[] key1, String nodeId, String op) {
        String plainText = "nodeId:" + nodeId + "op:" + op;
        byte[] cipherText = encryptionService.encryptByAES256(plainText, key1);
        return FileUtils.bytes2Base64(cipherText);
    }

    public static String[] idOpPairDecrypt(byte[] key1, String idOpPairCipherText) {
        byte[] bytes = encryptionService.decryptByAES256(FileUtils.Base64ToBytes(idOpPairCipherText), key1);
        String plainText = new String(bytes);
        int opIndex = plainText.indexOf("op:");
        String nodeId = plainText.substring("nodeId:".length(), opIndex);
        String op = plainText.substring(opIndex + "op:".length());
        return new String[]{nodeId, op};
    }

}
