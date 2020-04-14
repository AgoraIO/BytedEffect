package io.agora.rtcwithbyte.utils;


import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import static android.content.Context.UI_MODE_SERVICE;

public class AppUtils {

    private static boolean isDebug = false;
    // for test 使用yuv 不推荐启用，会有帧不同步问题
    private static boolean useYuv = false;

    // for test 使用yuv数据作为特效处理的输入 如果为false 使用yuv作为检测算法的输入
    private static boolean testEffectWithBuffer = false;

    private static boolean isAnimoji = true;

    public static boolean isTestEffectWithBuffer() {
        return testEffectWithBuffer;
    }

    // 加速glreadPixels 针对低性能GPU能加速，但不建议开启，会有帧不同步问题
    //  Acceleration glreadPixels is capable of acceleration for low performance GPU,
    // but it is not recommended to turn it on, because of the problem of frame unsynchronization
    private static boolean accGlReadPixels = false;
    // 适配特殊设备
    // Adapt to special equipment
    private static SupportMode supportMode = SupportMode.NORMAL;

    public static SupportMode getSupportMode() {
        return supportMode;
    }

    public static boolean isAccGlReadPixels(){
        return accGlReadPixels;
    }

    public static boolean isUseYuv(){
        return useYuv;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static boolean isAnimoji() {
        return isAnimoji;
    }

    // for test 是否打印性能相关数据
    private static boolean isProfile = true;

    public static boolean isProfile() {
        return isProfile;
    }

    /**
     * 判断当前设备是否是电视
     *
     * @param context
     * @return 电视返回 True，手机返回 False
     */
    public static boolean isTv(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        return (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
    }

    /**
     * 当前是否是横屏
     *
     * @param context
     * @return
     */
    public static boolean isLandScape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;


    }

    /**
     * 是否支持触摸屏
     * @param context
     * @return
     */
    public static boolean hasTouchScreen(Context context) {

        if (context == null)

            return false;

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)

                || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);

    }

    public enum  SupportMode{
        NORMAL,
        HR
    }


}
