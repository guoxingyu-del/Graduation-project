package com.graduate.design.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.graduate.design.utils.JsonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiIndex implements Serializable {
    private Map<String, String> lastW;
    private Map<String, String> lastID;

    public BiIndex(){
        lastID = Collections.synchronizedMap(new HashMap<>());
        lastW = Collections.synchronizedMap(new HashMap<>());
    }

    public BiIndex(BiIndex biIndex) {
        this.lastW = Collections.synchronizedMap(biIndex.getLastW());
        this.lastID = Collections.synchronizedMap(biIndex.getLastID());
    }

    public Map<String, String> getLastW() {
        return lastW;
    }

    public void setLastW(Map<String, String> lastW) {
        this.lastW = lastW;
    }

    public Map<String, String> getLastID() {
        return lastID;
    }

    public void setLastID(Map<String, String> lastID) {
        this.lastID = lastID;
    }

    public byte[] writeObject() {
        List list = new ArrayList();
        list.add(lastID);
        list.add(lastW);
        byte[] res = JSONObject.toJSONString(list).getBytes(StandardCharsets.ISO_8859_1);
        return res;
    }

    public void readObject(byte[] bytes) {
        String res = new String(bytes, StandardCharsets.ISO_8859_1);
        JSONArray array = JSONObject.parseArray(res);
        if(array.size() == 0) return;
        Map<String, String> lastID = JSONObject.parseObject(array.getString(0), Map.class);
        Map<String, String> lastW = JSONObject.parseObject(array.getString(1), Map.class);

        setLastID(lastID);
        setLastW(lastW);
    }
}
