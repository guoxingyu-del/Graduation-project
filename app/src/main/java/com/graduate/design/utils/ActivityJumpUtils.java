package com.graduate.design.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

public class ActivityJumpUtils {
    public static void jumpActivity(Activity activity, Intent intent, Long delayMillis, Boolean isNotFinish) {
        new Handler().postDelayed(() -> {
            activity.startActivity(intent);
            if (isNotFinish) {
                activity.finish();
            }
        }, delayMillis);
    }
}
