// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package io.agora.rtcwithbyte.core;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.Toast;
import android.util.Log;

import io.agora.rtcwithbyte.ResourceHelper;
import io.agora.rtcwithbyte.model.CaptureResult;
import io.agora.rtcwithbyte.model.ComposerNode;
import io.agora.rtcwithbyte.utils.AppUtils;
import library.LogUtils;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.RenderManager;

import java.util.HashSet;
import java.util.Set;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BytedResultCode.BEF_RESULT_FAIL;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC;


public class EffectRenderHelper {
    public static final String ProfileTAG = "Profile ";

    private RenderManager mRenderManager;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mImageWidth;
    private int mImageHeight;

    private OnEffectListener mOnEffectListener;
    private EffectRender mEffectRender;

    // 设置了贴纸后会与 Composer 冲突，所以再使用 Composer 功能的时候
    // 需要重新设置 Composer 路径，以 isShouldResetComposer 标志
    // After setting the sticker, it will conflict with Composer,
    // so You need to reset the Composer path by using the isShouldResetComposer flag when using the Composer function
    private volatile boolean isShouldResetComposer = true;
    private volatile int mComposerMode;
    private String mFilterResource;
    private String[] mComposeNodes = new String[0];
    private String mStickerResource;
    private Set<ComposerNode> mSavedComposerNodes = new HashSet<>();
    private float mFilterIntensity = 0f;
    private volatile boolean isEffectOn = true;
    private Context mContext;

    public EffectRenderHelper(Context context) {
        mContext = context;
        mRenderManager = new RenderManager();
        mEffectRender = new EffectRender();
    }

    public void setOnEffectListener(OnEffectListener listener) {
        mOnEffectListener = listener;
    }

    /**
     * 特效初始化入口
     *
     * @param context     应用上下文
     * @param imageWidth  输入图片的宽  注意是人脸转正后的宽度
     * @param imageHeight 输入图片的高 注意是人脸转正后的高度
     * @return 如果成功返回BEF_RESULT_SUC， 否则返回对应的错误码
     */
    public int initEffect(Context context, int imageWidth, int imageHeight) {
        if (imageHeight == 0 || imageHeight == 0) {
            LogUtils.e("image width or height equal to 0!!");
            return BEF_RESULT_FAIL;
        }
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        LogUtils.d("Effect SDK version =" + mRenderManager.getSDKVersion());
        int ret = mRenderManager.init(context, ResourceHelper.getModelDir(context), ResourceHelper.getLicensePath(context), imageWidth, imageHeight);
        if (ret != BEF_RESULT_SUC) {
            LogUtils.e("mRenderManager.init failed!! ret =" + ret);
        }

        if (mOnEffectListener != null) {
            mOnEffectListener.onEffectInitialized();
        }
        return ret;
    }

    /**
     * 特效处理接口
     * 步骤1：将纹理做旋转&翻转操作，将人脸转正（如果是前置摄像头，会加左右镜像），
     * 步骤2：然后执行特效处理，
     * 步骤3：步骤1的逆操作，将纹理处理成原始输出的角度、镜像状态
     * 客户可以根据自己输入的纹理状态自行选择执行上述部分步骤，比如部分推流SDK采集到的纹理已经做了人脸转正操作，只需要执行步骤2即可
     * @param srcTextureId 输入纹理
     * @param srcTetxureFormat 输入纹理的格式
     * @param srcTextureWidth 输入纹理的宽度
     * @param srcTextureHeight 输入纹理的高度
     * @param cameraRotation 相机输出的图像旋转角度
     * @param frontCamera 是否是前置摄像头
     * @param sensorRotation 重力传感器的重力方向角
     * @param timestamp 时间戳，由SurfaceTexture的接口输出
     * @return 输出后的纹理
     */
    public int processTexure(int srcTextureId, BytedEffectConstants.TextureFormat srcTetxureFormat, int srcTextureWidth, int srcTextureHeight, int cameraRotation, boolean frontCamera, BytedEffectConstants.Rotation sensorRotation, long timestamp) {
        int tempWidth = srcTextureWidth;
        int tempheight = srcTextureHeight;

        if (cameraRotation % 180 == 90) {
            tempWidth = srcTextureHeight;
            tempheight = srcTextureWidth;
        }
        long start = System.currentTimeMillis();
        // 因为Android相机预览纹理中的人脸不是正，该函数将oes转为2D纹理，并将人脸转正，如果是前置摄像头，会同时做左右镜像
        int tempTexureId = mEffectRender.drawFrameOffScreen(srcTextureId, srcTetxureFormat, tempWidth, tempheight, -cameraRotation, frontCamera, true);

        // 准备帧缓冲区纹理
        int dstTextureId = mEffectRender.prepareTexture(tempWidth, tempheight);
        long detectnext = System.currentTimeMillis();

        // 执行特效处理
        if (!isEffectOn || !mRenderManager.processTexture(tempTexureId, dstTextureId, tempWidth, tempheight, sensorRotation, timestamp)) {
            dstTextureId = tempTexureId;
        }
        if (AppUtils.isProfile()) {
            Log.d(ProfileTAG, "effectprocess: " + String.valueOf(System.currentTimeMillis() - detectnext));
        }
        // 将特效处理后的纹理，转回相机原始的状态(包括旋转角度、左右镜像)，方便接入推流SDK
        int tt = mEffectRender.drawFrameOffScreen(dstTextureId, BytedEffectConstants.TextureFormat.Texure2D, srcTextureWidth, srcTextureHeight, frontCamera ? -cameraRotation : cameraRotation, frontCamera, true);

        return tt;

    }

    /**
     * 在屏幕上渲染
     * draw on screen
     * @param textureId
     * @param textureFormat
     * @param srcTextureWidth
     * @param srcTextureHeight
     * @param cameraRotation
     * @param flipHorizontal
     * @param flipVertical
     */
    public void drawFrame(int textureId, BytedEffectConstants.TextureFormat textureFormat, int srcTextureWidth, int srcTextureHeight, int cameraRotation, boolean flipHorizontal, boolean flipVertical) {
        mEffectRender.drawFrameOnScreen(textureId, textureFormat, srcTextureWidth, srcTextureHeight, cameraRotation, flipHorizontal, flipVertical);
    }

    /**
     * 根据suafceView的尺寸设置Render的参数
     * @param width
     * @param height
     */
    public void onSurfaceChanged(int width, int height) {
        if (width != 0 && height != 0) {
            this.mSurfaceWidth = width;
            this.mSurfaceHeight = height;
            mEffectRender.setViewSize(mSurfaceWidth, mSurfaceHeight);

        }
    }

    /**
     * 工作在渲染线程
     * Work on the render thread
     */
    public void destroyEffectSDK() {
        LogUtils.d("EffectRenderHelper destroyEffectSDK");
        mRenderManager.release();
        mEffectRender.release();

        initedEffectSDK = false;
        isShouldResetComposer = true;

        LogUtils.d("destroyEffectSDK finish");
    }

    private void sendUIToastMsg(final String msg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private volatile boolean initedEffectSDK = false;

    /**
     * 初始化特效SDK，确保在gl线程中执行
     * @param imageWidth
     * @param imageHeight
     */
    public void initEffectSDK(int imageWidth, int imageHeight) {
        if (initedEffectSDK)
            return;
        int ret = initEffect(mContext, imageWidth, imageHeight);
        if (ret != BEF_RESULT_SUC) {
            LogUtils.e("initEffect ret =" + ret);
            sendUIToastMsg("Effect Initialization failed");
        }
        initedEffectSDK = true;
    }

    public void setEffectOn(boolean isOn) {
        isEffectOn = isOn;
    }

    /**
     * 开启或者关闭滤镜 如果path为空 关闭滤镜
     * Turn filters on or off
     * turn off filter if path is empty
     *
     * @param path path of filter file 滤镜资源文件路径
     */
    public boolean setFilter(String path) {
        mFilterResource = path;
        return mRenderManager.setFilter(path);
    }


    public CaptureResult capture() {
        if (null == mEffectRender) {
            return null;
        }
        if (0 == mImageWidth * mImageHeight) {
            return null;
        }
        return new CaptureResult(mEffectRender.captureRenderResult(mImageWidth, mImageHeight), mImageWidth, mImageHeight);
    }


    public void setComposerMode(final int mode) {
        int result = mRenderManager.setComposerMode(mode, 0);
        if (result == BEF_RESULT_SUC) {
            mComposerMode = mode;
        } else {
            LogUtils.e("set composer mode failed: " + result);
        }
    }

    public int getComposerMode() {
        return mComposerMode;
    }

    /**
     * 设置特效组合，目前支持美颜、美形、美体、 美妆特效的任意叠加
     * Set special effects combination
     * Currently only support the arbitrary superposition of two special effects, beauty and beauty makeup
     *
     * @param nodes
     * @return
     */
    public boolean setComposeNodes(String[] nodes) {
        if (isShouldResetComposer()) {
            int ret = mRenderManager.setComposer(ResourceHelper.getComposeMakeupComposerPath(mContext));
            if (ret != BEF_RESULT_SUC) {
                return false;
            }
            mStickerResource = null;
            isShouldResetComposer = false;
        }
        // clear mSavedComposerNodes cache when nodes length is 0
        if (nodes.length == 0) {
            mSavedComposerNodes.clear();
        }

        mComposeNodes = nodes;
        String prefix = ResourceHelper.getComposePath(mContext);
        String[] path = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            path[i] = prefix + nodes[i];
        }
        return mRenderManager.setComposerNodes(path) == BEF_RESULT_SUC;
    }

    /**
     * 更新组合特效(美颜、美形、美体、 美妆)中某个节点的强度
     * Updates the strength of a node in a composite effect
     *
     * @param node The ComposerNode corresponding to the special effects material
     *             特效素材对应的 ComposerNode
     * @return
     */
    public boolean updateComposeNode(ComposerNode node, boolean update) {
        if (update) {
            mSavedComposerNodes.remove(node);
            mSavedComposerNodes.add(node);
        }
        String path = ResourceHelper.getComposePath(mContext) + node.getNode();
        return mRenderManager.updateComposerNodes(path, node.getKey(), node.getValue()) == BEF_RESULT_SUC;
    }

    /**
     * 开启或者关闭贴纸 如果path为空 关闭贴纸
     * 注意 贴纸和Composer类型的特效（美颜、美妆）是互斥的，如果同时设置设置，后者会取消前者的效果
     * Turn on or off the sticker. If path is empty, turn off
     * Note that the stickers and Composer types of special effects (beauty, makeup) are mutually exclusive
     * If you set at the same the, the latter will cancel the effect of the former
     *
     * @param path 贴纸素材的文件路径
     */
    public boolean setSticker(String path) {
        isShouldResetComposer = true;
        mStickerResource = path;
        return mRenderManager.setSticker(path);
    }


    public boolean getAvailableFeatures(String[] features) {
        return mRenderManager.getAvailableFeatures(features);
    }

    /**
     * 设置滤镜强度
     * Set the intensity of the filter
     *
     * @param intensity intensity 参数值
     * @return 是否成功  if it is successful
     */
    public boolean updateFilterIntensity(float intensity) {
        boolean result = mRenderManager.updateIntensity(BytedEffectConstants.IntensityType.Filter.getId(), intensity);
        if (result) {
            mFilterIntensity = intensity;
        }
        return result;

    }

    /**
     * 切换摄像头后恢复特效设置
     * Restore beauty, filter and other Settings
     */
    public void recoverStatus() {
        if (!TextUtils.isEmpty(mFilterResource)) {
            setFilter(mFilterResource);

        }
        if (!TextUtils.isEmpty(mStickerResource)) {
            setSticker(mStickerResource);
        }

        if (mComposeNodes.length > 0) {
            setComposeNodes(mComposeNodes);

            for (ComposerNode node : mSavedComposerNodes) {
                updateComposeNode(node, false);
            }
        }
        updateFilterIntensity(mFilterIntensity);

        setComposerMode(mComposerMode);

    }


    private boolean isShouldResetComposer() {
        return mComposerMode == 0 && isShouldResetComposer;
    }

    public interface OnEffectListener {
        void onEffectInitialized();
    }
}
