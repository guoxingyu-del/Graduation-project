package com.graduate.design.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

public class JsonUtils {
    // Object-->Json
    public static String toJson(Object object) {
        try {
            return JsonFormat.printer().print((MessageOrBuilder) object);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
