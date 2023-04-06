package com.graduate.design.entity;

import com.alibaba.fastjson.serializer.JSONSerializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BiIndex implements Serializable {
    private Map<Long, String> lastW;
    private Map<String, Long> lastID;

    public BiIndex(){
        lastID = new HashMap<>();
        lastW = new HashMap<>();
    }

    public Map<Long, String> getLastW() {
        return lastW;
    }

    public void setLastW(Map<Long, String> lastW) {
        this.lastW = lastW;
    }

    public Map<String, Long> getLastID() {
        return lastID;
    }

    public void setLastID(Map<String, Long> lastID) {
        this.lastID = lastID;
    }

    public byte[] writeObject() {
        byte[] res;
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(lastID);
            objectStream.writeObject(lastW);
            res = byteStream.toByteArray();
            objectStream.close();
            byteStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public void readObject(byte[] bytes) {
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            lastID = (Map<String, Long>) objectStream.readObject();
            lastW = (Map<Long, String>) objectStream.readObject();
            objectStream.close();
            byteStream.close();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
