package com.graduate.design.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;



public class JsonUtils {

    private static final Gson gson = new Gson();

    // Object-->Json
    public static String toJson(Object object) {
        try {
            return JsonFormat.printer().print((MessageOrBuilder) object);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    // Json-->Object
    public static <T> T toObject(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

    // Json-->Object
    public static <T> T toObject(String json, TypeToken<T> token) {
        return gson.fromJson(json, token.getType());
    }
}
