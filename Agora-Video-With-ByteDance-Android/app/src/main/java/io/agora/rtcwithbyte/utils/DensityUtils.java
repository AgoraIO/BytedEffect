package io.agora.rtcwithbyte.utils;

import android.content.Context;

public class DensityUtils {
    public static float dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5F;
    }

    public static float px2dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return px / scale + 0.5F;
    }

    public static int sp2px(Context context, float sp) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scale + 0.5f);
    }

    public static int getStatusHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
