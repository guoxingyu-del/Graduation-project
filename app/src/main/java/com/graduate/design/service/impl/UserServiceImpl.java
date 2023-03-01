package com.graduate.design.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.graduate.design.R;
import com.graduate.design.proto.Common;
import com.graduate.design.proto.UserLogin;
import com.graduate.design.proto.UserRegister;
import com.graduate.design.service.NetWorkService;
import com.graduate.design.service.UserService;
import com.graduate.design.utils.JsonUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class UserServiceImpl implements UserService {

    private NetWorkService netWorkService = new NetWorkServiceImpl();

    @Override
    public int login(String username, String password) {

        // 构造登录请求对象
        UserLogin.UserLoginRequest req = UserLogin.UserLoginRequest.
                newBuilder().setUserName(username).setPassword(password)
                .build();

        // 将请求对象转换成json格式，不要使用Gson
        String data = JsonUtils.toJson(req);

        // 向服务器发送请求并拿到返回值
        String respData = netWorkService.request("https://192.168.0.2:8888/user/login", data);

        // 将返回值解析成json格式
        JSONObject jsonObject = JSONObject.parseObject(respData);

        // 拿到返回信息
        JSONObject baseResp = jsonObject.getJSONObject("baseResp");
        String message = baseResp.getString("message");
        // 请求失败
        if(!"success".equals(message)) return 1;

        // 拿到登陆成功后的token值
        String token = jsonObject.getString("token");

        // 构造UserInfo对象
        JSONObject userInfoJson = jsonObject.getJSONObject("userInfo");
        Long rootId = Long.parseLong(userInfoJson.getString("rootId"));
        String respUsername = userInfoJson.getString("userName");
        String email = userInfoJson.getString("email");
        UserLogin.UserInfo userInfo = UserLogin.UserInfo.newBuilder().setRootId(rootId)
                .setUserName(respUsername).setEmail(email).build();

        // 请求成功
        return 0;
    }

    @Override
    public int register(String username, String password, String email) {
        // 构造注册请求对象
        UserRegister.UserRegisterRequest req = UserRegister.UserRegisterRequest.
                newBuilder().setUserName(username).setPassword(password).
                setEmail(email).build();

        // 将请求对象转换成json格式，不要使用Gson
        String data = JsonUtils.toJson(req);

        // 向服务器发送请求并拿到返回值
        String respData = netWorkService.request("https://192.168.0.2:8888/user/register", data);

        // 将返回值解析成json格式
        JSONObject jsonObject = JSONObject.parseObject(respData);

        // 拿到返回信息
        JSONObject baseResp = jsonObject.getJSONObject("baseResp");
        String message = baseResp.getString("message");
        // 请求失败
        if(!"success".equals(message)) return 1;

        // 请求成功
        return 0;
    }
}
