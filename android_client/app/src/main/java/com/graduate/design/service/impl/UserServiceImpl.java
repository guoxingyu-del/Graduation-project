package com.graduate.design.service.impl;

import android.app.Application;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.graduate.design.R;
import com.graduate.design.entity.BiIndex;
import com.graduate.design.proto.ChangePassword;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.CreateDir;
import com.graduate.design.proto.DeleteFile;
import com.graduate.design.proto.DeleteShareToken;
import com.graduate.design.proto.FileUpload;
import com.graduate.design.proto.GetDir;
import com.graduate.design.proto.GetNode;
import com.graduate.design.proto.GetNodeId;
import com.graduate.design.proto.GetShareTokens;
import com.graduate.design.proto.Ping;
import com.graduate.design.proto.SearchFile;
import com.graduate.design.proto.SendSearchToken;
import com.graduate.design.proto.ShareFirst;
import com.graduate.design.proto.ShareSecond;
import com.graduate.design.proto.UploadShareToken;
import com.graduate.design.proto.UserLogin;
import com.graduate.design.proto.UserRegister;
import com.graduate.design.service.EncryptionService;
import com.graduate.design.service.NetWorkService;
import com.graduate.design.service.UserService;
import com.graduate.design.utils.ByteUtils;
import com.graduate.design.utils.FileUtils;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UserServiceImpl implements UserService {
    private final String base = "https://192.168.43.39:8888";
    private NetWorkService netWorkService = new NetWorkServiceImpl();

    @Override
    public int ping(String name, String token) {
        Ping.PingRequest req = Ping.PingRequest.newBuilder()
                .setName(name)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 0);
        return jsonObject == null ? 0 : 1;
    }

    @Override
    public int login(String username, String hashId) {
        // 构造登录请求对象
        UserLogin.UserLoginRequest req = UserLogin.UserLoginRequest.newBuilder()
                .setUserName(username)
                .setHashId(hashId)
                .build();

        JSONObject jsonObject = sendData(req, 1);
        // 请求失败
        if (jsonObject == null) return 1;

        // 拿到登陆成功后的token值
        String token = jsonObject.getString("token");

        // 将token值设置到全局变量中
        GraduateDesignApplication.setToken(token);

        // 构造UserInfo对象
        JSONObject userInfoJson = jsonObject.getJSONObject("userInfo");
        Long rootId = Long.parseLong(userInfoJson.getString("rootId"));
        String respUsername = userInfoJson.getString("userName");
        String email = userInfoJson.getString("email");
        String biIndex = userInfoJson.getString("biIndex");
        String key1 = userInfoJson.getString("key1");
        String key2 = userInfoJson.getString("key2");
        UserLogin.UserInfo userInfo = UserLogin.UserInfo.newBuilder()
                .setRootId(rootId)
                .setUserName(respUsername)
                .setEmail(email)
                .setBiIndex(biIndex)
                .setKey1(key1)
                .setKey2(key2)
                .build();

        // 将userInfo设置到全局变量中
        GraduateDesignApplication.setUserInfo(userInfo);

        // 请求成功
        return 0;
    }

    @Override
    public int register(String username, String hashId, String email, String biIndex, String key1, String key2) {
        // 构造注册请求对象
        UserRegister.UserRegisterRequest req = UserRegister.UserRegisterRequest.newBuilder()
                .setUserName(username)
                .setHashId(hashId)
                .setEmail(email)
                .setBiIndex(biIndex)
                .setKey1(key1)
                .setKey2(key2)
                .build();

        JSONObject jsonObject = sendData(req, 2);
        // 请求失败
        return jsonObject == null ? 1 : 0;
    }

    @Override
    public int createDir(String dirName, Long parentId, String token) {
        CreateDir.CreateDirRequest req = CreateDir.CreateDirRequest.newBuilder()
                .setDirName(dirName)
                .setParentId(parentId)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();



        return sendData(req, 3) == null ? 1 : 0;
    }


    @Override
    public int uploadFile(String fileName, Long parentId, List<Common.indexToken> indexList, ByteString content
            , String biIndex, Long fileId, String token, String fileSecret) {
        FileUpload.UploadFileRequest req = FileUpload.UploadFileRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setFileName(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .setParentId(parentId)
                .setContent(content)
                .setBiIndex(biIndex)
                .setNodeId(fileId)
                .setFileSecret(fileSecret)
                .addAllIndexList(indexList)
                .build();

        JSONObject jsonObject = sendData(req, 4);

        return jsonObject == null ? 1 : 0;
    }

    @Override
    public List<Common.Node> searchFile(List<Long> idList, String token) {
        List<Common.Node> res = new ArrayList<>();

        SearchFile.SearchFileRequest req = SearchFile.SearchFileRequest.newBuilder()
                .addAllNodeId(idList)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 5);

        if (jsonObject == null) return null;

        JSONArray nodeList = jsonObject.getJSONArray("nodeList");

        if (nodeList == null) return res;

        // 遍历所有文件节点
        for (int i = 0; i < nodeList.size(); i++) {
            JSONObject nodeJson = nodeList.getJSONObject(i);
            // 获取节点类型
            Common.NodeType realType;
            String nodeType = nodeJson.getString("nodeType");
            switch (nodeType) {
                case "File":
                    realType = Common.NodeType.File;
                    break;
                case "Dir":
                    realType = Common.NodeType.Dir;
                    break;
                default:
                    realType = Common.NodeType.Unknown;
                    break;
            }
            String nodeName = nodeJson.getString("nodeName");
            // 构造子节点
            Common.Node node = Common.Node.newBuilder()
                    .setNodeType(realType)
                    .setNodeId(Long.parseLong(nodeJson.getString("nodeId")))
                    .setNodeName(nodeJson.getString("nodeName"))
                    .setCreateTime(Long.parseLong(nodeJson.getString("createTime")))
                    .setUpdateTime(Long.parseLong(nodeJson.getString("updateTime")))
                    .build();
            res.add(node);
        }

        return res;
    }

    /**
     * @param nodeId dirId
     * @param token  用户token
     * @return dirId下的所有确定存在的Node信息
     */
    @Override
    public List<Common.Node> getDir(Long nodeId, String token) {
        List<Common.Node> res = new ArrayList<>();

        GetDir.GetDirRequest req = GetDir.GetDirRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setNodeId(nodeId)
                .build();

        System.out.println(req.getBaseReq().getToken());

        JSONObject jsonObject = sendData(req, 17);
        if (jsonObject == null) return null;
        JSONArray nodeIdList = jsonObject.getJSONArray("nodeIdList");
        if (nodeIdList == null) {
            return new ArrayList<>();
        }

        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < nodeIdList.size(); i++) {
            idList.add(nodeIdList.getLong(i));
        }

        return searchFile(idList, token);
    }

    @Override
    public String[] getNodeContent(Long nodeId, String token) {
        GetNode.GetNodeRequest req = GetNode.GetNodeRequest.newBuilder()
                .setNodeId(nodeId)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 6);
        // 请求失败
        if (jsonObject == null) return null;

        JSONObject node = jsonObject.getJSONObject("node");

        String content, secretKey;

        // 此时获得的文件内容和文件密钥均为Base64编码
        byte[] contentBytes = node.getBytes("nodeContent");
        if (contentBytes == null) content = "";
        else content = ByteString.copyFrom(contentBytes).toString(StandardCharsets.ISO_8859_1);

        byte[] secretKeyBytes = node.getBytes("secretKey");
        // 源文件已被删除
        if (secretKeyBytes == null) {
            return new String[]{"", ""};
        }
        else secretKey = ByteString.copyFrom(secretKeyBytes).toString(StandardCharsets.ISO_8859_1);

        // TODO 解密
        EncryptionService encryptionService = new EncryptionServiceImpl();
        // 先用key2解密fileSecretKey
        byte[] fileSecretKeyEncrypt = FileUtils.Base64ToBytes(secretKey);

        byte[] key2 = GraduateDesignApplication.getKey2();
        byte[] fileSecret = encryptionService.decryptByAES256(fileSecretKeyEncrypt, key2);

        byte[] fileContentEncrypt = FileUtils.Base64ToBytes(content);
        byte[] fileContent = encryptionService.decryptByAES256(fileContentEncrypt, fileSecret);

        String contentString = ByteString.copyFrom(fileContent).toString(StandardCharsets.ISO_8859_1);
        String secretString = FileUtils.bytes2Base64(fileSecret);

        return new String[]{contentString, secretString};
    }

    @Override
    public Long getNodeId(String token) {
        GetNodeId.GetNodeIdRequest req = GetNodeId.GetNodeIdRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 10);

        if (jsonObject == null) return null;

        Long nodeId = jsonObject.getLong("nodeId");

        return nodeId;
    }

    @Override
    public List<String> sendSearchToken(SendSearchToken.SearchToken searchToken, String token) {
        SendSearchToken.SendSearchTokenRequest req = SendSearchToken.SendSearchTokenRequest.newBuilder()
                .setSearchToken(searchToken)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 11);

        if (jsonObject == null) return null;

        List<String> res = new ArrayList<>();
        JSONArray array = jsonObject.getJSONArray("Cw");
        // 没有文件节点
        if (array == null) return res;
        // 遍历所有文件节点
        for (int i = 0; i < array.size(); i++) {
            String cw = array.getString(i);
            res.add(cw);
        }
        return res;
    }

    @Override
    public int changePassword(String oldHashId, String newHashId, String key1, String key2, String token) {
        ChangePassword.ChangePasswordRequest req = ChangePassword.ChangePasswordRequest.newBuilder()
                .setOldHashId(oldHashId)
                .setNewHashId(newHashId)
                .setKey1(key1)
                .setKey2(key2)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 12);

        return jsonObject == null ? 1 : 0;
    }

    @Override
    public int deleteFileOrDir(List<Long> nodeId, String token) {
        DeleteFile.DeleteFileRequest req = DeleteFile.DeleteFileRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .addAllDelNodeID(nodeId)
                .build();

        JSONObject jsonObject = sendData(req, 20);

        return jsonObject == null ? 1 : 0;
    }

    @Override
    public List<String> firstShare(String L, String Jid, String token) {
        ShareFirst.ShareFirstRequest req = ShareFirst.ShareFirstRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setL(L)
                .setJId(Jid)
                .build();

        JSONObject jsonObject = sendData(req, 15);

        if (jsonObject == null) return null;

        List<String> res = new ArrayList<>();
        JSONArray array = jsonObject.getJSONArray("S");
        // 没有文件节点
        if (array == null) return res;
        // 遍历所有文件节点
        for (int i = 0; i < array.size(); i++) {
            String cid = array.getString(i);
            res.add(cid);
        }
        return res;
    }

    @Override
    public int secondShare(String filename, Long parentId, String biIndex, Long fileId, Boolean isShare, Long address, String fileSecret, List<Common.indexToken> indexList, String token) {
        ShareSecond.ShareSecondRequest req = ShareSecond.ShareSecondRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setFileName(new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .setParentId(parentId)
                .setBiIndex(biIndex)
                .setNodeId(fileId)
                .setIsShare(isShare)
                .setAddress(address)
                .setFileSecret(fileSecret)
                .addAllSearchIndexSecond(indexList)
                .build();

        JSONObject jsonObject = sendData(req, 16);

        return jsonObject == null ? 1 : 0;
    }

    @Override
    public int uploadShareToken(Common.ShareToken shareToken, String secretKey, String fileName, String token) {
        UploadShareToken.UploadShareTokenRequest req = UploadShareToken.UploadShareTokenRequest.newBuilder()
                .setShareToken(shareToken)
                .setSecretKey(secretKey)
                .setFileName(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .setIsShare("1")
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 13);

        return jsonObject == null ? 1 : 0;
    }

    @Override
    public List<GetShareTokens.ShareMesssage> getAllShareToken(String userid, String token) {
        GetShareTokens.GetShareTokensRequest req = GetShareTokens.GetShareTokensRequest.newBuilder()
                .setUserId(userid)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 14);

        if (jsonObject==null) return null;

        List<GetShareTokens.ShareMesssage> res = new ArrayList<>();

        JSONArray array = jsonObject.getJSONArray("shareMesssages");
        // 没有文件节点
        if (array == null) return res;
        // 遍历所有文件节点
        for (int i = 0; i < array.size(); i++) {
            JSONObject detailObj = array.getJSONObject(i);
            JSONObject shareTokenObj = detailObj.getJSONObject("shareToken");
            Common.ShareToken shareToken = Common.ShareToken.newBuilder()
                    .setL(shareTokenObj.getString("L"))
                    .setJId(shareTokenObj.getString("JId"))
                    .setKId(shareTokenObj.getString("kId"))
                    .setOwnerId(shareTokenObj.getString("ownerId"))
                    .setUserId(shareTokenObj.getString("userId"))
                    .setFileId(shareTokenObj.getString("fileId"))
                    .build();

            GetShareTokens.ShareMesssage msg = GetShareTokens.ShareMesssage.newBuilder()
                    .setFileName(detailObj.getString("fileName"))
                    .setSecretKey(detailObj.getString("secretKey"))
                    .setCreateTime(Long.parseLong(detailObj.getString("createTime")))
                    .setShareTokenId(detailObj.getString("shareTokenId"))
                    .setIsShare(detailObj.getString("isShare"))
                    .setShareToken(shareToken)
                    .build();

            res.add(msg);
        }
        return res;
    }

    @Override
    public void deleteShareToken(String tokenId, String token) {
        DeleteShareToken.DeleteShareTokenRequest req = DeleteShareToken.DeleteShareTokenRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setShareTokenId(tokenId)
                .build();

        JSONObject jsonObject = sendData(req, 21);
    }

    @Override
    public Set<Long> getAllDeleteNodes(String userName, String token) {
        return null;
    }

    private JSONObject sendData(Object req, int number) {
        String[] urls = {
                "ping", // 0
                "/user/login", // 1
                "/user/register", // 2
                "/dir/create", // 3
                "/file/upload", // 4
                "/file/search", // 5
                "/node/get", // 6
                "/file/register", // 7
                "/file/share", // 8
                "/file/recv/get", // 9
                "/node/getId", // 10
                "/file/sendSearchToken", // 11
                "/user/changePwd", // 12
                "/file/uploadShareToken", // 13
                "/file/getShareTokens", // 14
                "/file/shareFirst", // 15
                "/file/shareSecond", // 16
                "/dir/get", // 17 用于请求文件夹下的所有结点idOpPair信息
                "/node/gets", // 18 用户请求所有存在的文件
                "/node/username", // 19 用于请求特定用户下所有的idOpPair
                "/node/delete",   // 删除文件或文件夹
                "/node/shareToken",  // 删除分享token
        };
        // 将请求对象转换成json格式，不要使用Gson
        String data = JsonUtils.toJson(req);

        // 向服务器发送请求并拿到返回值
        String respData = netWorkService.request(base + urls[number], data);

        // 将返回值解析成json格式
        JSONObject jsonObject = JSONObject.parseObject(respData);

        // 拿到返回信息
        JSONObject baseResp = jsonObject.getJSONObject("baseResp");
        String message = baseResp.getString("message");
        // 请求失败
        if (!"success".equals(message)) return null;

        return jsonObject;
    }
}