package com.graduate.design.service;

import com.google.protobuf.ByteString;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.GetRecvFile;

import java.util.List;

public interface UserService {
    int ping(String name, String token);

    int login(String username, String password);

    int register(String username, String password, String email);

    int createDir(String dirName, Long parentId, String token);

    int uploadFile(String fileName, Long parentId, List<String> indexList,
                   ByteString content, ByteString secretKey, String token);

    List<Common.Node> searchFile(String keyword, String token);

    int registerFile(Long fileId, Long dirId, ByteString secretKey,
                  Boolean isWeb, Long shareId, String token);

    List<Common.Node> getNodeList(Long nodeId, String token);
    String getNodeContent(Long nodeId, String token);

    int shareFile(String username, Long fileId, ByteString key, String token);

    List<GetRecvFile.SharedFile> getRecvFile(String token);

}
