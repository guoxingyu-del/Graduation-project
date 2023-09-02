package com.graduate.design.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeUtils {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // timeMillis to string
    public static String timerToString(Long timer) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(STANDARD_FORMAT, Locale.CHINA);
        String res = simpleDateFormat.format(timer);
        return res;
    }

    // get Current time of string
    public static String getNowDateTime() {
        return timerToString(System.currentTimeMillis());
    }
}
