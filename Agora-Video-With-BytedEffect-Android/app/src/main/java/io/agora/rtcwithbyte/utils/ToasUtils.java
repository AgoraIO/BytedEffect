package io.agora.rtcwithbyte.utils;

import android.content.Context;
import android.widget.Toast;

import library.LogUtils;


public class ToasUtils {
    private static Context mAppContext = null;

    public static void init(Context context) {
        mAppContext = context;
    }


    public static void show(String msg) {
        if (null == mAppContext) {
            LogUtils.d("ToasUtils not inited with Context");
            return;
        }
        Toast.makeText(mAppContext, msg, Toast.LENGTH_SHORT).show();
    }



}
