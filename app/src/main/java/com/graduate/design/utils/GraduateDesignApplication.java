package com.graduate.design.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.allenliu.classicbt.BleManager;
import com.allenliu.classicbt.Connect;
// import com.graduate.design.exception.GlobalCrashHandler;
import com.graduate.design.R;
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
    private static byte[] start;
    private static byte[] end;
    private static Connect curConnect;
    private static byte[] mainSecret;
    private static String originPassword;
    // 设置AES256加密得到默认向量常量，16个字节
    public static final String IV = "0123456789012345";

    @Override
    public void onCreate() {
        super.onCreate();
        token = null;
        mAppContext = getApplicationContext();
        start = mAppContext.getString(R.string.startMsg).getBytes(StandardCharsets.UTF_8);
        end = mAppContext.getString(R.string.endMsg).getBytes(StandardCharsets.UTF_8);

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

    public static byte[] getMainSecret() {
        return mainSecret;
    }

    public static void setMainSecret(byte[] mainSecret) {
        GraduateDesignApplication.mainSecret = mainSecret;
    }

    public static String getOriginPassword() {
        return originPassword;
    }

    public static void setOriginPassword(String originPassword) {
        GraduateDesignApplication.originPassword = originPassword;
    }
}
