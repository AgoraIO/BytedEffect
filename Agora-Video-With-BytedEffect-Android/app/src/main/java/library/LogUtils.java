// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package library;

import android.util.Log;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;

public class LogUtils {

    private static final String TAG = BytedEffectConstants.TAG;

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }
}
