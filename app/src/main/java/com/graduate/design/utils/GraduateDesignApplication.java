package com.graduate.design.utils;

import android.app.Application;
import android.content.Context;

import com.graduate.design.exception.GlobalCrashHandler;

public class GraduateDesignApplication extends Application {
    /**
     * System context
     */
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler.getGlobalCrashHandler());
    }

    /**
     * get system context used for ToastUtil class
     */
    public static Context getAppContext() {
        return mAppContext;
    }
}
