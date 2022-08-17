package io.agora.rtcwithbyte.framework;

import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.byteddance.effect.EffectHelper;
import com.byteddance.effect.ResourceHelper;
import com.byteddance.model.ComposerNode;

import java.io.File;
import java.util.concurrent.Callable;

import io.agora.base.TextureBufferHelper;
import io.agora.base.VideoFrame;
import io.agora.rtc2.video.IVideoFrameObserver;

public class PreprocessorByteDance implements IVideoFrameObserver {
    private Context mContext;
    private EffectHelper mEffectHelper;
    private boolean mEnabled = false;

    // This path is an example filter
    private String mDefaultFilterPath;

    private TextureBufferHelper textureBufferHelper;

    public PreprocessorByteDance(Context context) {
        mContext = context;
        mDefaultFilterPath = getDefaultFilterPath();
    }

    @Override
    public boolean onCaptureVideoFrame(VideoFrame videoFrame) {
        VideoFrame.Buffer buffer = videoFrame.getBuffer();
        if(!(buffer instanceof VideoFrame.TextureBuffer)){
            return false;
        }
        VideoFrame.TextureBuffer textureBuffer = (VideoFrame.TextureBuffer) buffer;
        if(textureBufferHelper == null){
            textureBufferHelper = TextureBufferHelper.create("Render", textureBuffer.getEglBaseContext());
            textureBufferHelper.invoke((Callable<Void>) () -> {
                mEffectHelper = new EffectHelper(mContext);
                mEffectHelper.initEffectSDK();
                mEnabled = true;
                setFilter(getDefaultFilterPath());
                return null;
            });
        }

        Integer retTexId = textureBufferHelper.invoke((Callable<Integer>) () -> mEffectHelper.processTexture(
                textureBuffer.getTextureId(),
                textureBuffer.getType() == VideoFrame.TextureBuffer.Type.OES ? GLES11Ext.GL_TEXTURE_EXTERNAL_OES : GLES20.GL_TEXTURE_2D,
                textureBuffer.getWidth(),
                textureBuffer.getHeight(),
                videoFrame.getRotation(),
                System.currentTimeMillis()
        ));
        VideoFrame.TextureBuffer retBuffer = textureBufferHelper.wrapTextureBuffer(textureBuffer.getWidth(), textureBuffer.getHeight(), VideoFrame.TextureBuffer.Type.RGB,
                retTexId, new Matrix());
        videoFrame.replaceBuffer(retBuffer, videoFrame.getRotation(), videoFrame.getTimestampNs());

        return true;
    }

    public void disposeOnStopPreview(){
        if(textureBufferHelper != null){
            if(mEffectHelper != null){
                textureBufferHelper.invoke((Callable<Void>) () -> {
                    mEffectHelper.destroyEffectSDK();
                    mEnabled = false;
                    return null;
                });
            }
            textureBufferHelper.dispose();
            textureBufferHelper = null;
        }
    }

    private String getDefaultFilterPath() {
        File[] filters = ResourceHelper.getFilterResources(mContext);
        if (filters != null && filters.length > 0) {
            File def = filters[0];
            return def.getAbsolutePath();
        } else {
            return "";
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void enablePreProcess(boolean enabled) {
        if(initialized()){
            mEffectHelper.setEffectOn(enabled);
            mEnabled = enabled;
        }
    }

    public void setFilter(String path) {
        if(initialized()){
            mEffectHelper.setFilter(path);
        }
    }

    public void updateFilterIntensity(float intensity) {
        mEffectHelper.updateFilterIntensity(intensity);
    }

    public boolean initialized() {
        return mEffectHelper != null && mEffectHelper.initialized();
    }

    public boolean updateComposeNode(ComposerNode node, boolean update){
        return mEffectHelper.updateComposeNode(node, update);
    }

    public boolean setComposeNodes(String[] nodes) {
        return mEffectHelper.setComposeNodes(nodes);
    }

    public boolean setSticker(String sticker){
        return mEffectHelper.setSticker(sticker);
    }



    @Override
    public boolean onPreEncodeVideoFrame(VideoFrame videoFrame) {
        return false;
    }

    @Override
    public boolean onScreenCaptureVideoFrame(VideoFrame videoFrame) {
        return false;
    }

    @Override
    public boolean onPreEncodeScreenVideoFrame(VideoFrame videoFrame) {
        return false;
    }

    @Override
    public boolean onMediaPlayerVideoFrame(VideoFrame videoFrame, int mediaPlayerId) {
        return false;
    }

    @Override
    public boolean onRenderVideoFrame(String channelId, int uid, VideoFrame videoFrame) {
        return false;
    }

    @Override
    public int getVideoFrameProcessMode() {
        return IVideoFrameObserver.PROCESS_MODE_READ_WRITE;
    }

    @Override
    public int getVideoFormatPreference() {
        return IVideoFrameObserver.VIDEO_PIXEL_DEFAULT;
    }

    @Override
    public boolean getRotationApplied() {
        return false;
    }

    @Override
    public boolean getMirrorApplied() {
        return true;
    }

    @Override
    public int getObservedFramePosition() {
        return IVideoFrameObserver.POSITION_POST_CAPTURER;
    }
}
