package com.graduate.design;

import org.junit.Test;

import static org.junit.Assert.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.graduate.design.entity.BiIndex;
import com.graduate.design.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        BiIndex biIndex = new BiIndex();
        Map<String, String> lastW = new HashMap<>();
        lastW.put("123156465", "dasd");
        lastW.put("1231564651", "dasd");
        Map<String, String> lastID = new HashMap<>();
        lastID.put("fadawda", "1345115");
        biIndex.setLastW(lastW);
        biIndex.setLastID(lastID);
        List list = new ArrayList();
        list.add(lastID);
        list.add(lastW);
        String res = JsonUtils.toJson(list);
        System.out.println(res);
//        System.out.println(JSONObject.parseObject(res, BiIndex.class).getLastW());

        JSONArray array = JSONObject.parseArray(res);
        String s = array.getString(0);
        System.out.println(s);
        Map<String, String> map = JSONObject.parseObject(s, Map.class);
        System.out.println(map.get("fadawda"));
        System.out.println(map.get("fadawda").getClass());
    }
}