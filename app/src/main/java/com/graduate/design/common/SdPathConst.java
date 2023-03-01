package com.graduate.design.common;

import android.os.Environment;

public interface SdPathConst {
    String sdPath = Environment.getExternalStorageDirectory().getPath() + "/design";
}
