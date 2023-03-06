package com.graduate.design.utils;

import android.app.Application;
import android.content.Context;

import com.graduate.design.exception.GlobalCrashHandler;
import com.graduate.design.proto.UserLogin;

public class GraduateDesignApplication extends Application {
    /**
     * System context
     */
    private static Context mAppContext;

    /*
    *   登录成功后服务器返回的token值
    * */
    private static String token;

    private static UserLogin.UserInfo userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        token = null;
        mAppContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler.getGlobalCrashHandler());
    }

    /**
     * get system context used for ToastUtil class
     */
    public static Context getAppContext() {
        return mAppContext;
    }

    public static String getToken() { return token; }

    public static void setToken(String new_token) { token = new_token; }

    public static UserLogin.UserInfo getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(UserLogin.UserInfo userInfo) {
        GraduateDesignApplication.userInfo = userInfo;
    }
}
