package com.graduate.design.sharettokenProtocol;

import com.graduate.design.entity.BiIndex;
import com.graduate.design.proto.Common;
import com.graduate.design.utils.ByteUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ShareTokenGen {
    /**
     * input: 文件id fileif
     * output 分享令牌
     * @param fileId
     * @return
     */
    public Common.ShareToken genShareToken(String fileId, String ownerId, String userId) {
        byte[] key1 = GraduateDesignApplication.getKey1();
        byte[] key2 = GraduateDesignApplication.getKey2();
        BiIndex biIndex = GraduateDesignApplication.getBiIndex();
        Map<String, String> lastW = biIndex.getLastW();
        String wLast = lastW.get(fileId);
        if (wLast == null) {
            return null;
        }
        byte[] idBytes = fileId.getBytes(StandardCharsets.UTF_8);
        byte[] wordBytes = wLast.getBytes(StandardCharsets.UTF_8);
        byte[] word_id = ByteUtils.mergeBytes(wordBytes, idBytes);
        byte[] id_word = ByteUtils.mergeBytes(idBytes, wordBytes);
        byte[] L = HmacSha256(key1, word_id);
        byte[] Jid = HmacSha256(key2, id_word);
        byte[] kid = HmacSha256(key2, idBytes);

        return Common.ShareToken.newBuilder()
                .setL(FileUtils.bytes2Base64(L))
                .setJId(FileUtils.bytes2Base64(Jid))
                .setKId(FileUtils.bytes2Base64(kid))
                .setFileId(fileId)
                .setOwnerId(ownerId)
                .setUserId(userId)
                .build();
    }

    public byte[] cutOffTo128(byte[] data) {
        byte[] res = new byte[16];
        for (int i = 0; i < 16; i++) {
            res[i] = data[i];
        }
        return res;
    }

    public byte[] HmacSha256(byte[] key, byte[] data) {
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
}
