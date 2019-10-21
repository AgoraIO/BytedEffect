// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package io.agora.rtcwithbyte.renderer;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.Toast;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.RenderManager;

import java.util.HashSet;
import java.util.Set;

import io.agora.rtcwithbyte.ResourceHelper;
import io.agora.rtcwithbyte.model.CaptureResult;
import io.agora.rtcwithbyte.model.ComposerNode;
import library.LogUtils;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BytedResultCode.BEF_RESULT_FAIL;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC;


public class EffectRenderHelper {

    private RenderManager mRenderManager;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mImageWidth;
    private int mImageHeight;

    private EffectRender mEffectRender;

    // 设置了贴纸后会与 Composer 冲突，所以再使用 Composer 功能的时候
    // 需要重新设置 Composer 路径，以 isShouldResetComposer 标志
    // After setting the sticker, it will conflict with Composer,
    // so You need to reset the Composer path by using the isShouldResetComposer flag when using the Composer function
    private boolean isShouldResetComposer = false;
    private String mFilterResource;
    private String[] mComposeNodes = new String[0];
    private String mStickerResource;
    private Set<ComposerNode> mSavedComposerNodes = new HashSet<>();
    private ArrayMap<BytedEffectConstants.IntensityType, Float> storedIntensities = new ArrayMap<>();


    public volatile boolean isEffectOn = true;

    private Context mContext;

    public EffectRenderHelper(Context context) {
        mContext = context;
        mRenderManager = new RenderManager();
        mEffectRender = new EffectRender();
    }

    /**
     * 特效初始化入口
     * @param context 应用上下文
     * @param imageWidth 输入图片的宽  注意是人脸转正后的宽度
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
        int ret = mRenderManager.init(context, ResourceHelper.getModelDir(context), ResourceHelper.getLicensePath(context), imageWidth, imageHeight);
        if (ret != BEF_RESULT_SUC) {
            LogUtils.e("mRenderManager.init failed!! ret =" + ret);
            return ret;
        }
        ret = mRenderManager.setComposer(ResourceHelper.getComposeMakeupComposerPath(context));
        if (ret != BEF_RESULT_SUC) {
            LogUtils.e("mRenderManager.setComposer failed!! ret =" + ret);
        }
        return ret;
    }

    public int processTexure(int srcTextureId, BytedEffectConstants.TextureFormat srcTetxureFormat, int srcTextureWidth, int srcTextureHeight, int cameraRotation, boolean frontCamera, BytedEffectConstants.Rotation sensorRotation, long timestamp){
        int tempWidth = srcTextureWidth;
        int tempheight = srcTextureHeight;

        if (cameraRotation %180 == 90){
            tempWidth = srcTextureHeight;
            tempheight = srcTextureWidth;
        }
        long start = System.currentTimeMillis();
        int tempTexureId = mEffectRender.drawFrameOffScreen(srcTextureId, srcTetxureFormat, tempWidth, tempheight, -cameraRotation , frontCamera, true);

        int dstTextureId = mEffectRender.prepareTexture(tempWidth, tempheight);
        if (!isEffectOn || !mRenderManager.processTexture(tempTexureId, dstTextureId, tempWidth, tempheight, sensorRotation, timestamp)){
            dstTextureId = tempTexureId;
        }

        long middle = System.currentTimeMillis();
        int tt = mEffectRender.drawFrameOffScreen(dstTextureId, BytedEffectConstants.TextureFormat.Texure2D, srcTextureWidth, srcTextureHeight,   frontCamera?-cameraRotation:cameraRotation, frontCamera, true);
        long end = System.currentTimeMillis();
//        LogUtils.e("1阶段 =" +(middle - start));
//        LogUtils.e("2阶段 =" +(end - middle));
//        if (AppUtils.isDebug() && mainView.mImageView.getVisibility() == View.VISIBLE) {
//            ByteBuffer byteBuffer = mEffectRender.readPixlesBuffer(tempTexureId,tempWidth, tempheight  );
//            if (null != byteBuffer){
//                final Bitmap bitmap = BitmapUtils.getBitmapFromPixels(byteBuffer,tempWidth, tempheight);
//
//                mainView.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mainView.mImageView.setImageBitmap(bitmap);
//                    }
//                });
//            }
//
//        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return tt;

    }


    public void drawFrame(int textureId, BytedEffectConstants.TextureFormat textureFormat, int srcTextureWidth, int srcTextureHeight , int cameraRotation, boolean flipHorizontal, boolean flipVertical) {
        mEffectRender.drawFrameOnScreen(textureId, textureFormat, srcTextureWidth, srcTextureHeight, cameraRotation, flipHorizontal, flipVertical);
    }

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

    public void initEffectSDK(int imageWidth, int imageHeight) {
        if (initedEffectSDK)
            return;
        int ret = initEffect(mContext,imageWidth,  imageHeight);
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


    public CaptureResult capture(){
        if (null == mEffectRender){
            return null;
        }
        if (0 == mImageWidth * mImageHeight){
            return null;
        }
        return new CaptureResult(mEffectRender.captureRenderResult(mImageWidth,mImageHeight),mImageWidth, mImageHeight);
    }


    /**
     * 设置特效组合，目前仅支持美颜 美妆 两种特效的任意叠加
     * Set special effects combination
     * Currently only support the arbitrary superposition of two special effects, beauty and beauty makeup
     *
     * @param nodes
     * @return
     */
    public boolean setComposeNodes(String[] nodes) {
        if (isShouldResetComposer) {
            int ret = mRenderManager.setComposer(ResourceHelper.getComposeMakeupComposerPath(mContext));
            if (ret != BEF_RESULT_SUC) {
                return false;
            }
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
     * 更新组合特效中某个节点的强度
     * Updates the strength of a node in a composite effect
     *
     * @param node The ComposerNode corresponding to the special effects material
     *             特效素材对应的 ComposerNode
     * @return
     */
    public boolean updateComposeNode(ComposerNode node) {
        mSavedComposerNodes.add(node);
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

    /**
     * 设置美颜/滤镜(除塑形)强度
     * Set the intensity of the beauty/filter (except shaping)
     *
     * @param intensitytype intensity type参数类型
     * @param intensity     intensity 参数值
     * @return 是否成功  if it is successful
     */
    public boolean updateIntensity(BytedEffectConstants.IntensityType intensitytype, float intensity) {
        boolean result = mRenderManager.updateIntensity(intensitytype.getId(), intensity);
        if (result) {
            storedIntensities.put(intensitytype, intensity);
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
                updateComposeNode(node);
            }
        }
        for (BytedEffectConstants.IntensityType entryk : storedIntensities.keySet()) {
            updateIntensity(entryk, storedIntensities.get(entryk));
        }

    }


}
