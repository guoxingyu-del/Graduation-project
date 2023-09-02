package com.graduate.design.service;

import com.google.protobuf.ByteString;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.proto.GetShareTokens;
import com.graduate.design.proto.SearchFile;
import com.graduate.design.proto.SendSearchToken;

import java.util.List;
import java.util.Set;

public interface UserService {
    int ping(String name, String token);

    int login(String username, String hashId);

    int register(String username, String hashId, String email, String biIndex, String key1, String key2);

    int createDir(String dirName, Long parentId, String token);

    int uploadFile(String fileName, Long parentId, List<Common.indexToken> indexList,
                   ByteString content, String biIndex, Long fileId, String token, String fileSecret);

    List<Common.Node> searchFile(List<Long> idList, String token);

    List<Common.Node> getDir(Long nodeId, String token);

    String[] getNodeContent(Long nodeId, String token);

    Long getNodeId(String token);

    List<String> sendSearchToken(SendSearchToken.SearchToken searchToken, String token);

    int changePassword(String oldHashId, String newHashId, String key1, String key2, String token);

    int deleteFileOrDir(List<Long> nodeId, String token);

    List<String> firstShare(String L, String Jid, String token);

    int secondShare(String filename, Long parentId, String biIndex, Long fileId, Boolean isShare,
                    Long address, String fileSecret, List<Common.indexToken> indexList, String token);

    public int uploadShareToken(Common.ShareToken shareToken, String secretKey, String fileName,String token);

    public List<GetShareTokens.ShareMesssage> getAllShareToken(String userid, String token);

    public void deleteShareToken(String tokenId, String token);

    public Set<Long> getAllDeleteNodes(String userName, String token);
}
