package com.graduate.design.service.impl;


import com.graduate.design.service.NetWorkService;
import com.graduate.design.utils.GraduateDesignApplication;
import com.graduate.design.utils.HTTPSUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetWorkServiceImpl implements NetWorkService {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public String request(String url, String data) {
        try {
            HTTPSUtils httpsUtils = new HTTPSUtils(GraduateDesignApplication.getAppContext());
            OkHttpClient client = httpsUtils.getInstance();
            RequestBody body = RequestBody.create(data, JSON);
            Request request = new Request.Builder()
                    .url(url).post(body)
                    .build();
            Response response;
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
               return response.body() != null ? response.body().string() : null;
            }
            else {
                System.out.println("-----------请求失败-------------");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*ServerResponse serverResponse = new ServerResponse();
        serverResponse.setStatus(ResponseCodeENUM.ERROR.getCode());
        serverResponse.setMsg("请求超时");*/
        // return serverResponse;
        return null;
    }
}
