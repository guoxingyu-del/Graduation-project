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
import com.graduate.design.proto.FileUpload;
import com.graduate.design.proto.GetNode;
import com.graduate.design.proto.GetNodeId;
import com.graduate.design.proto.GetRecvFile;
import com.graduate.design.proto.GetShareTokens;
import com.graduate.design.proto.Ping;
import com.graduate.design.proto.RegisterFile;
import com.graduate.design.proto.SearchFile;
import com.graduate.design.proto.SendSearchToken;
import com.graduate.design.proto.ShareFile;
import com.graduate.design.proto.ShareFirst;
import com.graduate.design.proto.UpLoadShareToken;
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
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UserServiceImpl implements UserService {
    private final String base = "https://192.168.0.105:8888";
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
    public int uploadFile(String fileName, Long parentId, List<FileUpload.indexToken> indexList, ByteString content, String biIndex, Long fileId, String token) {
        FileUpload.UploadFileRequest req = FileUpload.UploadFileRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setFileName(fileName)
                .setParentId(parentId)
                .setContent(content)
                .setBiIndex(biIndex)
                .setNodeId(fileId)
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

    @Override
    public List<Common.Node> getNodeList(Long nodeId, String token) {
        List<Common.Node> res = new ArrayList<>();

        GetNode.GetNodeRequest req = GetNode.GetNodeRequest.newBuilder()
                .setNodeId(nodeId)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 6);
        // 请求失败
        if (jsonObject == null) return null;

        JSONObject node = jsonObject.getJSONObject("node");

        // 拿到子节点数组，即文件数组
        JSONArray subNodeList = node.getJSONArray("subNodeList");
        // 没有文件节点
        if (subNodeList == null) return res;
        // 遍历所有文件节点
        for (int i = 0; i < subNodeList.size(); i++) {
            JSONObject sonJson = subNodeList.getJSONObject(i);
            // 获取节点类型
            Common.NodeType realType;
            String nodeType = sonJson.getString("nodeType");
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
            // 构造子节点
            Common.Node son = Common.Node.newBuilder()
                    .setNodeType(realType)
                    .setNodeId(Long.parseLong(sonJson.getString("nodeId")))
                    .setNodeName(sonJson.getString("nodeName"))
                    .setCreateTime(Long.parseLong(sonJson.getString("createTime")))
                    .setUpdateTime(Long.parseLong(sonJson.getString("updateTime")))
                    .build();
            res.add(son);
        }
        return res;
    }

    @Override
    public String getNodeContent(Long nodeId, String token) {
        GetNode.GetNodeRequest req = GetNode.GetNodeRequest.newBuilder()
                .setNodeId(nodeId)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 6);
        // 请求失败
        if (jsonObject == null) return null;

        JSONObject node = jsonObject.getJSONObject("node");

        String content;

        // 此时获得的文件内容和文件密钥均为Base64编码
        byte[] contentBytes = node.getBytes("nodeContent");
        if (contentBytes == null) content = "";
        else content = ByteString.copyFrom(contentBytes).toString(StandardCharsets.UTF_8);

        // TODO 解密
        EncryptionService encryptionService = new EncryptionServiceImpl();
        byte[] fileContentEncrypt = FileUtils.Base64ToBytes(content);

        byte[] fileSecret = GraduateDesignApplication.getKey2();
        byte[] fileContent = encryptionService.decryptByAES256(fileContentEncrypt, fileSecret);

        return ByteString.copyFrom(fileContent).toString(StandardCharsets.UTF_8);
    }

    @Override
    public int registerFile(Long fileId, Long dirId, ByteString secretKey, Boolean isWeb, Long shareId, String token) {
        RegisterFile.RegisterFileRequest req = RegisterFile.RegisterFileRequest.newBuilder()
                .setFileId(fileId)
                .setDirId(dirId)
                .setSecretKey(secretKey)
                .setIsWeb(isWeb)
                .setShareId(shareId)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        return sendData(req, 7) == null ? 1 : 0;
    }

    @Override
    public int shareFile(String username, Long fileId, ByteString key, String token) {
        ShareFile.ShareFileRequest req = ShareFile.ShareFileRequest.newBuilder()
                .setUserName(username)
                .setFileId(fileId)
                .setKey(key)
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        return sendData(req, 8) == null ? 1 : 0;
    }

    @Override
    public List<GetRecvFile.SharedFile> getRecvFile(String token) {
        List<GetRecvFile.SharedFile> res = new ArrayList<>();

        GetRecvFile.GetRecvFileRequest req = GetRecvFile.GetRecvFileRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .build();

        JSONObject jsonObject = sendData(req, 9);

        if (jsonObject == null) return null;

        JSONArray shareFileList = jsonObject.getJSONArray("shareFileList");

        if (shareFileList == null) return res;

        for (int i = 0; i < shareFileList.size(); i++) {
            JSONObject shareFileJson = shareFileList.getJSONObject(i);

            GetRecvFile.SharedFile sharedFile = GetRecvFile.SharedFile.newBuilder()
                    .setShareId(shareFileJson.getLong("shareId"))
                    .setFileId(shareFileJson.getLong("fileId"))
                    .setFileName(shareFileJson.getString("fileName"))
                    .setKey(ByteString.copyFrom(shareFileJson.getBytes("key")))
                    .setFrom(shareFileJson.getString("from"))
                    .build();

            res.add(sharedFile);
        }
        return res;
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

    /**
     * 上传shareToken至加密数据库
     *
     * @param shareToken 分享令牌，包括了userId,ownerId,L,kid,Jid,id
     * @param token      用户token
     * @return 上传情况
     */
    public int uploadShareToken(Common.ShareToken shareToken, String token) {
        UpLoadShareToken.UpLoadShareTokenRequest req = UpLoadShareToken.UpLoadShareTokenRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setShareToken(shareToken)
                .build();
        JSONObject jsonObject = sendData(req, 13);
        return jsonObject != null ? 1 : 0;
    }


    /**
     * 根据userid从分享数据库中找到所有的sharetoken
     *
     * @param userid 用户id
     * @param token  用户token
     * @return 所有的shareToken
     */
    public List<Common.ShareToken> getAllShareToken(String userid, String token) {
        GetShareTokens.GetShareTokensRequest req = GetShareTokens.GetShareTokensRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setUserId(userid)
                .build();
        JSONObject jsonObject = sendData(req, 14);
        if (jsonObject == null) return null;
        List<Common.ShareToken> shareTokens = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("shareTokens");
        for (int i = 0; i < jsonArray.size(); i++) {
            shareTokens.add(jsonArray.getObject(i, Common.ShareToken.class));
        }
        return shareTokens;
    }

    /**
     * 将接收方选择接受的shareToken进行注册
     *
     * @param shareToken 分享令牌
     * @param token      用户token
     * @return 0表示注册失败 1表示注册成功
     */
//    public int shareTokenRegister(Common.ShareToken shareToken, String token) {
//        ShareFirst.ShareFirstRequest req = ShareFirst.ShareFirstRequest.newBuilder()
//                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
//                .setL(shareToken.getL())
//                .setJId(shareToken.getJId())
//                .build();
//        JSONObject jsonObject = sendData(req, 14);
//        if (jsonObject == null) return 0;
//        List<String> S = new ArrayList<>();
//        JSONArray jsonArray = jsonObject.getJSONArray("S");
//        for (int i = 0; i < jsonArray.size(); i++) {
//            S.add(jsonArray.getString(i));
//            Log.e("S", jsonArray.getString(i));
//        }
//
//
//        EncryptionService encryptionService = new EncryptionServiceImpl();
//        String id = shareToken.getFileId();
//        String Kid = shareToken.getKId();
//        byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);
//        byte[] KidBytes = FileUtils.Base64ToBytes(Kid);
//        byte[] key1 = GraduateDesignApplication.getKey1();
//        byte[] key2 = GraduateDesignApplication.getKey2();
//        List<Common.SearchIndexSecond> newS = new ArrayList<>();
//        BiIndex biIndex = GraduateDesignApplication.getBiIndex();
//        Map<String, Long> lastID = biIndex.getLastID();
//        Map<Long, String> lastW = biIndex.getLastW();
//        for (String s : S) {
//            byte[] w = encryptionService.decryptByAES128(s, KidBytes);
//            Long lastId = lastID.get(FileUtils.bytes2Base64(w));
//            String lastw = lastW.get(Long.parseLong(id));
//            byte[] Cw = encryptionService.encryptByAES128(id,
//                    cutOffTo128(HmacSha256(key2, w)));
//            byte[] Cid = encryptionService.encryptByAES128(w,
//                    cutOffTo128(HmacSha256(key2, idBytes)));
//            byte[] w_id = ByteUtils.mergeBytes(w, idBytes);
//            byte[] id_w = ByteUtils.mergeBytes(idBytes, w);
//            byte[] L = cutOffTo128(HmacSha256(key1, w_id));
//            byte[] Rw = ByteUtils.getRandomBytes(16);
//            byte[] Rid = ByteUtils.getRandomBytes(16);
//            byte[] Iw;
//            if (lastId == null) {
//                Iw = HmacSha256(cutOffTo128(HmacSha256(key2, w_id)), Rw);
//            } else {
//                byte[] oldIDBytes = String.valueOf(lastId).getBytes(StandardCharsets.UTF_8);
//                byte[] oldL = cutOffTo128(HmacSha256(key1,
//                        ByteUtils.mergeBytes(w, oldIDBytes)));
//                byte[] oldJw = cutOffTo128(HmacSha256(key2, ByteUtils.mergeBytes(w, oldIDBytes)));
//                byte[] Jw = cutOffTo128(HmacSha256(key2, w_id));
//                Iw = ByteUtils.xor(HmacSha256(Jw, Rw), ByteUtils.mergeBytes(oldL, oldJw));
//            }
//            byte[] Iid;
//            if (lastw == null) {
//                byte[] Jid = cutOffTo128(HmacSha256(key2, id_w));
//                Iid = HmacSha256(Jid, Rid);
//            } else {
//                byte[] oldWordBytes = lastw.getBytes(StandardCharsets.UTF_8);
//                byte[] oldL = cutOffTo128(HmacSha256(key1, ByteUtils.mergeBytes(oldWordBytes, idBytes)));
//                byte[] oldJid = cutOffTo128(HmacSha256(key2, ByteUtils.mergeBytes(idBytes, oldWordBytes)));
//                byte[] Jid = cutOffTo128(HmacSha256(key2, id_w));
//                Iid = ByteUtils.xor(HmacSha256(Jid, Rid), ByteUtils.mergeBytes(oldL, oldJid));
//            }
//            lastW.put(Long.valueOf(id), Arrays.toString(w));
//            lastID.put(Arrays.toString(w), Long.valueOf(id));
//
//            biIndex.setLastID(lastID);
//            biIndex.setLastW(lastW);
//            newS.add(Common.SearchIndexSecond.newBuilder()
//                    .setL(FileUtils.bytes2Base64(L))
//                    .setIw(FileUtils.bytes2Base64(Iw))
//                    .setRw(FileUtils.bytes2Base64(Rw))
//                    .setCw(FileUtils.bytes2Base64(Cw))
//                    .setIId(FileUtils.bytes2Base64(Iid))
//                    .setRId(FileUtils.bytes2Base64(Rid))
//                    .setCId(FileUtils.bytes2Base64(Cid))
//                    .build());
//
//        }
//        Log.e("length", String.valueOf(newS.size()));
//        ShareSecond.ShareSecondRequest shareSecondRequest = ShareSecond.ShareSecondRequest.newBuilder()
//                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
//                .addAllSearchIndexSecond(newS)
//                .build();
//        jsonObject = sendData(shareSecondRequest, 15);
//        return jsonObject == null ? 0 : 1;
//    }
    public List<FileUpload.indexToken> shareTokenRegister(Common.ShareToken shareToken, String token) {
        ShareFirst.ShareFirstRequest req = ShareFirst.ShareFirstRequest.newBuilder()
                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
                .setL(shareToken.getL())
                .setJId(shareToken.getJId())
                .build();
//        Log.e("llllllllllllllllllL", shareToken.getL());
//        Log.e("Jid", shareToken.getJId());
        JSONObject jsonObject = sendData(req, 15);
        if (jsonObject == null) return null;
        List<String> S = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("S");
        for (int i = 0; i < jsonArray.size(); i++) {
            S.add(jsonArray.getString(i));
            Log.e("S", jsonArray.getString(i));
        }


        EncryptionService encryptionService = new EncryptionServiceImpl();
        String id = shareToken.getFileId();
        String Kid = shareToken.getKId();
        byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);
        byte[] KidBytes = FileUtils.Base64ToBytes(Kid);
        byte[] key1 = GraduateDesignApplication.getKey1();
        byte[] key2 = GraduateDesignApplication.getKey2();
//        List<Common.SearchIndexSecond> newS = new ArrayList<>();
        List<FileUpload.indexToken> newS = new ArrayList<>();
        BiIndex biIndex = GraduateDesignApplication.getBiIndex();
        Map<String, Long> lastID = biIndex.getLastID();
        Map<Long, String> lastW = biIndex.getLastW();
        for (String s : S) {
            byte[] w = encryptionService.decryptByAES256(s, KidBytes);
//            Long lastId = lastID.get(FileUtils.bytes2Base64(w));
            Long lastId = lastID.get(new String(w));
            String lastw = lastW.get(Long.parseLong(id));
            byte[] Cw = encryptionService.encryptByAES256(id,
                    HmacSha256(key2, w));
            byte[] Cid = encryptionService.encryptByAES256(w,
                    HmacSha256(key2, idBytes));

            byte[] w_id = ByteUtils.mergeBytes(w, idBytes);
            byte[] id_w = ByteUtils.mergeBytes(idBytes, w);

            byte[] L = HmacSha256(key1, w_id);
            byte[] Rw = ByteUtils.getRandomBytes(32);
            byte[] Rid = ByteUtils.getRandomBytes(32);
            byte[] Iw, Iid;
            if (lastId == null) {
                Iw = encryptionService.SHA512(ByteUtils.mergeBytes(HmacSha256(key2, w_id), Rw));
            } else {
                byte[] oldIDBytes = String.valueOf(lastId).getBytes(StandardCharsets.UTF_8);
                byte[] oldL = HmacSha256(key1, ByteUtils.mergeBytes(w, oldIDBytes));
                byte[] oldJw = HmacSha256(key2, ByteUtils.mergeBytes(w, oldIDBytes));
                byte[] Jw = HmacSha256(key2, w_id);
                Iw = ByteUtils.xor(encryptionService.SHA512(ByteUtils.mergeBytes(Jw, Rw)), ByteUtils.mergeBytes(oldL, oldJw));
            }
            if (lastw == null) {
                byte[] Jid = HmacSha256(key2, id_w);
                Iid = encryptionService.SHA512(ByteUtils.mergeBytes(Jid, Rid));
            } else {
                byte[] oldWordBytes = lastw.getBytes(StandardCharsets.UTF_8);
                byte[] oldL = HmacSha256(key1, ByteUtils.mergeBytes(oldWordBytes, idBytes));
                byte[] oldJid = HmacSha256(key2, ByteUtils.mergeBytes(idBytes, oldWordBytes));
                byte[] Jid = HmacSha256(key2, id_w);
                Iid = ByteUtils.xor(encryptionService.SHA512(ByteUtils.mergeBytes(Jid, Rid)), ByteUtils.mergeBytes(oldL, oldJid));
            }
            lastW.put(Long.valueOf(id), new String(w));
            lastID.put(new String(w), Long.valueOf(id));

            biIndex.setLastID(lastID);
            biIndex.setLastW(lastW);
            newS.add(FileUpload.indexToken.newBuilder()
                    .setL(FileUtils.bytes2Base64(L))
                    .setIw(FileUtils.bytes2Base64(Iw))
                    .setRw(FileUtils.bytes2Base64(Rw))
                    .setCw(FileUtils.bytes2Base64(Cw))
                    .setIid(FileUtils.bytes2Base64(Iid))
                    .setRid(FileUtils.bytes2Base64(Rid))
                    .setCid(FileUtils.bytes2Base64(Cid))
                    .build());
        }
//        Log.e("length", String.valueOf(newS.size()));
//        ShareSecond.ShareSecondRequest shareSecondRequest = ShareSecond.ShareSecondRequest.newBuilder()
//                .setBaseReq(Common.BaseReq.newBuilder().setToken(token).build())
//                .addAllSearchIndexSecond(newS)
//                .build();
//        jsonObject = sendData(shareSecondRequest, 15);
        return newS;
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
                "/file/shareSecond" // 16
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

    // 这两个函数后面需要封装一下
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