package com.graduate.design.service;

import java.io.InputStream;
import java.net.URL;

import okhttp3.ResponseBody;

public interface NetWorkService {
    String request(String url, String data);
}
