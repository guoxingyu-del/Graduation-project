package com.graduate.design.utils;

import android.util.Log;

import com.graduate.design.common.Const;
import com.graduate.design.common.SdPathConst;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    // create and write log file
    public static void createAndWriteUncaughtExceptionLog(String exceptionDetail) {
        // get external storage path
        String storage = SdPathConst.sdPath;
        File dirFile = new File(storage);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                return;
            }
        }
        File logFile = new File(SdPathConst.sdPath, Const.LOG_FILE_NAME.getDesc());
        FileWriter fileWriter = null;
        try {
            if (!logFile.exists()) {
                if (logFile.createNewFile()) {
                    Log.i("msg", "create the log file");
                }
            }
            if (logFile.exists()) {
                fileWriter = new FileWriter(logFile, true);
                fileWriter.append(DateTimeUtils.getNowDateTime()).append("--->").append(exceptionDetail).append("\r");
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
