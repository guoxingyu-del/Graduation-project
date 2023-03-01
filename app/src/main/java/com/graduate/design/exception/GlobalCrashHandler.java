package com.graduate.design.exception;

import androidx.annotation.NonNull;

import com.graduate.design.utils.FileUtils;

public class GlobalCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final GlobalCrashHandler globalCrashHandler = new GlobalCrashHandler();

    public synchronized static GlobalCrashHandler getGlobalCrashHandler() {
        return globalCrashHandler;
    }

    private GlobalCrashHandler() {
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        FileUtils.createAndWriteUncaughtExceptionLog(e.getMessage());
    }
}
