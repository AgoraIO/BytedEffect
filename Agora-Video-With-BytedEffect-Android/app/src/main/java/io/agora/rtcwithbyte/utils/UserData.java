package io.agora.rtcwithbyte.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by QunZhang on 2019-11-04 18:22
 */
public class UserData {
    private static final String SP_NAME = "com.bytedance.labcv.demo";
    private static final String EXCLUSIVE = "exclusive";

    private static volatile UserData sInstance;
    private SharedPreferences sp;

    private UserData(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static UserData getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserData.class) {
                if (sInstance == null) {
                    sInstance = new UserData(context);
                }
            }
        }
        return sInstance;
    }

    public static void setExclusive(Context context, boolean exclusive) {
        getInstance(context).sp.edit().putBoolean(EXCLUSIVE, exclusive).apply();
    }

    public static boolean getExclusive(Context context, boolean notExist) {
        return getInstance(context).sp.getBoolean(EXCLUSIVE, notExist);
    }
}
