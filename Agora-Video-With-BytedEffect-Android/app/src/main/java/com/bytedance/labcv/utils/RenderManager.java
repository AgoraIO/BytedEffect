// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.bytedance.labcv.effectsdk;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC;

/**
 * 特效（包括滤镜、美颜、美体、美妆、贴纸）入口
 * Special effects (including filters, beauty, stickers, makeup) entry
 */
public class RenderManager {

    private long mNativePtr;

    private volatile boolean mInited;

    static {
        System.loadLibrary("effect");
        System.loadLibrary("effect_proxy");
    }

    /**
     * 初始化特效句柄,默认设置叠加特效（包括美颜、美形、美体、美妆等）和贴纸（包括贴纸、animoji 等）可以共存
     * Initializes the effects handle
     * @param context android context 应用上下文
     * @param modelDir 模型文件的根目录，注意不是模型文件的绝对路径，该目录下文件层次和目录名称必须和Demo中提供的完全一致
     *                 The root directory of the model file, note that it is not an absolute path to the model file,
     *                 and the file hierarchy and directory names in this directory must be exactly the same as those provided in the Demo
     * @param licensePath license file path 授权文件绝对路径

     * @return 成功返回BEF_RESULT_SUC，其他返回值参考{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     */
    public int init(Context context, String modelDir, String licensePath) {

        int ret = BEF_RESULT_SUC;
        if (!mInited) {
            ret = nativeInit(context, modelDir, licensePath);
            if (ret != BEF_RESULT_SUC){
                Log.e(BytedEffectConstants.TAG, "nativeInit failed: error code" + ret);
            }
            // 设置设置叠加特效（包括美颜、美形、美体、美妆等）和贴纸（包括贴纸、animoji 等）可以共存
            ret = nativeSetComposerMode(1, 0);
            if (ret != BEF_RESULT_SUC){
                Log.e(BytedEffectConstants.TAG, "set composer mode failed: error code" + ret);
            }


            mInited = (ret == BEF_RESULT_SUC);

        }

        return ret;
    }

    /**
     * 设置图片模式，在没有开启美颜的情况下设置美妆贴纸时，需要设置imageMode=true,其余场景下不用设置
     * @param imageMode
     * @return
     */
    public boolean setImageMode(boolean imageMode){
        if (!mInited) {
            return false;
        }
        return nativeSetImageMode(imageMode) == BEF_RESULT_SUC;

    }


    /**
     * 设置摄像头前后
     * @param isFront true表示是前置摄像头 false表示后置摄像头
     * @return true表示成功
     */
    public boolean setCameraPostion(boolean isFront){
        if (!mInited) {
            return false;
        }
        return nativeSetCameraPosition(isFront) == BEF_RESULT_SUC;

    }

    public boolean isInited() {
        return mInited;
    }

    /**
     * 释放特效相关句柄
     * Releases effect-related handles
     */
    public void release() {
        if (mInited) {
            nativeRelease();
        }
        mInited = false;
    }



    /**
     * 设置美颜素材
     * Set beauty material
     * @param resourcePath 素材绝对路径 如果传null或者空字符，则取消美颜效果
     *                     Material absolute path if pass null or null character, then cancel beauty effect
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     * @Deprecated 此接口适用于 2.6 版本及之前设置美颜，在 2.7 版本之后请务必使用 setComposerNodes 接口设置美颜等
     */
    @Deprecated
    public boolean setBeauty(String resourcePath) {
        if (!mInited) {
            return false;
        }
        if (resourcePath == null) {
            resourcePath = "";
        }
        return nativeSetBeauty(resourcePath) == BEF_RESULT_SUC;
    }
    /**
     * 设置塑形素材
     * Set shaping materials
     * @param resourcePath 素材绝对路径 如果传null或者空字符，则取消塑形效果
     *                     Material absolute path
     *                     If null or null characters are passed, the shaping effect is cancelled
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     * @Deprecated 此接口适用于 2.6 版本及之前设置美形，在 2.7 版本之后请务必使用 setComposerNodes 接口设置美形等
     */
    @Deprecated
    public boolean setReshape(String resourcePath) {
        if (!mInited) {
            return false;
        }
        if (resourcePath == null) {
            resourcePath = "";
        }
        return nativeSetReshape(resourcePath) == BEF_RESULT_SUC;
    }

    /**
     * 设置滤镜素材
     * Set the filter material
     * @param resourcePath 素材绝对路径 如果传null或者空字符，则取消滤镜效果
     *                     Material absolute path,
     *                     If null or null characters are passed, the filter effect is cancelled
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     */
    public boolean setFilter(String resourcePath) {
        if (!mInited) {
            return false;
        }
        if (resourcePath == null) {
            resourcePath = "";
        }
        return nativeSetFilter(resourcePath) == BEF_RESULT_SUC;
    }

    /**
     * 设置美妆素材
     * Set the makeup material
     * @param resourcePath 素材绝对路径 如果传null或者空字符，则取消美妆效果
     *                     Material absolute path,
     *                     If null or null characters are passed, the makeup effect is cancelled
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     * @Deprecated 此接口适用于 2.6 版本及之前设置美妆，在 2.7 版本之后请务必使用 setComposerNodes 接口设置美妆等
     */
    @Deprecated
    public boolean setMakeUp(String resourcePath) {
        if (!mInited) {
            return false;
        }
        if (resourcePath == null) {
            resourcePath = "";
        }
        return nativeSetMakeUp(resourcePath) == BEF_RESULT_SUC;
    }

    /**
     * 设置贴纸素材
     * Set the sticker material
     * @param resourcePath 素材绝对路径 如果传null或者空字符，则取消贴纸效果
     *                     Material absolute path,
     *                     If null or null characters are passed, the sticker effect is cancelled
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     */
    public boolean setSticker(String resourcePath) {
        if (!mInited) {
            return false;
        }
        if (resourcePath == null) {
            resourcePath = "";
        }
        Log.e("wl","setSticker "+resourcePath);
        return nativeSetSticker(resourcePath) == BEF_RESULT_SUC;
    }

    public boolean getAvailableFeatures(String[] features) {
        return nativeGetAvailableFeatures(features) == BEF_RESULT_SUC;
    }

    /**
     * 处理纹理输入 processTexture texture
     * 注意，请确保人脸已经转正，如果是Android设备，一般需要旋转一定角度
     * @param srcTextureId input texture id 输入纹理ID
     * @param dstTextureId output texture id 输出纹理ID
     * @param width texture width纹理宽度
     * @param height texture height 纹理高度
     * @param rotation texture rotation 纹理旋转角，参考{@link BytedEffectConstants.Rotation}
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     */
    public boolean processTexture(int srcTextureId, int dstTextureId, int width, int height, BytedEffectConstants.Rotation rotation, long timestamp) {
        if (!mInited) {
            return false;
        }
        return nativeProcess(srcTextureId, dstTextureId, width, height, rotation.id, getSurfaceTimeStamp(timestamp)) == BEF_RESULT_SUC;
    }

    /**
     * 处理像素数据输入，建议采用纹理输入性能更高
     * Processing pixel data
     * @param inputdata input data输入数据
     * @param orient orientation 旋转角，参考{@link BytedEffectConstants.Rotation}
     * @param in_pixformat data format 数据格式 参考{@link BytedEffectConstants}
     * @param imagew image width 图片宽度
     * @param imageh image height 图片高度
     * @param imagestride image stride 图片步长
     * @param outdata ouput data 输出结果
     * @param out_pixformat output format 输出结果格式 参考{@link BytedEffectConstants}
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     */
    @Deprecated // 建议采用纹理输入效率更高
    public boolean processBuffer(ByteBuffer inputdata, BytedEffectConstants.Rotation orient, int in_pixformat, int imagew, int imageh, int imagestride, byte[] outdata, int out_pixformat) {
        if (!mInited) {
            return false;
        }
        double timestamp = System.nanoTime();
        int retStatus = nativeProcessBuffer(inputdata, orient.id, in_pixformat, imagew, imageh, imagestride, outdata, out_pixformat, timestamp);
        return retStatus == BEF_RESULT_SUC;
    }

    /**
     * 设置强度
     * Set the intensity
     * @param intensitytype type 类型
     * @param intensity
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     * 此接口适用于 2.6 版本及之前设置美颜强度，在 2.7 版本之后请务必使用 updateComposerNodes 接口设置美颜强度等
     * 此外，所有版本设置滤镜强度都使用此接口
     */
    public boolean updateIntensity(int intensitytype, float intensity) {
        return nativeUpdateIntensity(intensitytype, intensity) == BEF_RESULT_SUC;
    }

    /**
     * 设置塑形参数
     * Set shaping parameters
     * @param cheekintensity The intensity of thin face 瘦脸强度 0-1
     * @param eyeintensity The intensity of bigger eye 大眼参数 0-1
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     *
     * @Deprecated 此接口适用于 2.6 版本及之前设置美形强度，在 2.7 版本之后请务必使用 updateComposerNodes 接口设置美形强度等
     */
    @Deprecated
    public boolean updateReshape(float cheekintensity, float eyeintensity) {
        return nativeUpdateReshape(cheekintensity, eyeintensity) == BEF_RESULT_SUC;
    }

    /**
     * 设置叠加美颜、美形、美体、美妆特效的初始化设置
     * Set the initialization Settings for overlay beauty effects
     * @param composerPath
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     */
    public int setComposer(String composerPath){
        return nativeSetComposer(composerPath);
    }

    /**
     * 设置composer特效和 贴纸是否可以叠加
     * @param mode 1 可以叠加 0 不可叠加
     * @param orderType 渲染顺序，暂时不可用
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     */
    public int setComposerMode(int mode, int orderType) {
        return nativeSetComposerMode(mode, orderType);
    }

    /**
     * 设置叠加美颜、美形、美体、美妆特效
     * Set overlay beauty effects
     * @param composerNodes
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     */
    public int setComposerNodes(String[] composerNodes){
        return nativeSetComposerNodes(composerNodes);

    }

    /**
     * 设置某一个 Composer 的强度
     * Set the strength of Composer
     * @param path Composer path 对应路径
     * @param key KEY
     * @param value strength 强度值，0～1
     * @return 成功返回BEF_RESULT_SUC， 其他返回值查看{@link BytedEffectConstants}
     *          success return BEF_RESULT_SUC，Otherwise the corresponding error code is returned
     */
    public int updateComposerNodes(String path, String key, float value) {
        return nativeUpdateComposer(path, key, value);
    }

    /**
     * 对纹理时间戳进行一定处理
     * @param textureTimeStamp 纹理时间戳
     * @return SDK特定的时间戳
     */
    public double getSurfaceTimeStamp(long textureTimeStamp) {
        long cur_time_nano = System.nanoTime();
        long delta_nano_time = Math.abs(cur_time_nano - textureTimeStamp);
        long delta_elapsed_nano_time = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? Math.abs(SystemClock.elapsedRealtimeNanos() -textureTimeStamp) : Long.MAX_VALUE;
        long delta_uptime_nano = Math.abs(SystemClock.uptimeMillis() * 1000000 - textureTimeStamp);
        double lastTimeStamp = cur_time_nano - Math.min(Math.min(delta_nano_time, delta_elapsed_nano_time), delta_uptime_nano);
        return lastTimeStamp / 1e9;
    }

    /**
     * 获取SDK版本号
     * @return 版本号字符串
     */
    public String getSDKVersion(){
        return nativeGetSDKVersion();
    }




    private native int nativeInit(Context context, String algorithmResourceDir, String license);

    private native String nativeGetSDKVersion();

    private native int nativeSetCameraPosition(boolean isFront);


    private native void nativeRelease();

    private native int nativeSetBeauty(String beautyType);

    private native int nativeSetReshape(String reshapeType);

    private native int nativeSetFilter(String filterPath);

    private native int nativeSetMakeUp(String filterPath);

    private native int nativeSetSticker(String filterPath);

    private native int nativeUpdateIntensity(int itype, float intensity);

    private native int nativeUpdateReshape(float cheekintensity, float eyeintensity);

    private native int nativeSetComposer(String composerPath);

    private native int nativeSetImageMode(boolean flag);

    private native int nativeSetComposerMode(int mode, int orderType);

    private native int nativeSetComposerNodes(String[] nodes);

    private native int nativeUpdateComposer(String path, String key, float value);

    private native int nativeProcess(int srcTextureId, int dstTextureId, int width, int height, int rotation, double timeStamp);

    private native int nativeProcessBuffer(ByteBuffer inputdata, int rotation, int in_pixformat, int imagew, int imageh, int imagestride, byte[] outdata, int out_pixformat, double timestamp);

    private native int nativeGetAvailableFeatures(String[] features);

}
