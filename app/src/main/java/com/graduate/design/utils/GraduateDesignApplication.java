package com.graduate.design.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.allenliu.classicbt.BleManager;
import com.allenliu.classicbt.Connect;
// import com.graduate.design.exception.GlobalCrashHandler;
import com.graduate.design.proto.UserLogin;

import java.nio.charset.StandardCharsets;


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

    // 自定义一个完整数据包的开头和结束字节
    private static byte[] start = "开始\n".getBytes(StandardCharsets.UTF_8);
    private static byte[] end = "结束\n".getBytes(StandardCharsets.UTF_8);
    private static Connect curConnect;

    @Override
    public void onCreate() {
        super.onCreate();
        token = null;
        mAppContext = getApplicationContext();

        // 初始化bleManager
        BleManager.getInstance().init(mAppContext);
        // 使用前台service传输数据
        BleManager.getInstance().setForegroundService(true);

    //    Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler.getGlobalCrashHandler());
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

    public static byte[] getStart() {
        return start;
    }

    public static byte[] getEnd() {
        return end;
    }

    public static Connect getCurConnect() {
        return curConnect;
    }

    public static void setCurConnect(Connect curConnect) {
        GraduateDesignApplication.curConnect = curConnect;
    }
}
