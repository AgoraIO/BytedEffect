package io.agora.rtcwithbyte;

import android.content.Context;
import android.opengl.GLES20;

import com.byteddance.effect.EffectHelper;
import com.byteddance.effect.ResourceHelper;

import java.io.File;

import io.agora.capture.framework.modules.channels.VideoChannel;
import io.agora.capture.framework.modules.processors.IPreprocessor;
import io.agora.capture.video.camera.VideoCaptureFrame;

public class PreprocessorByteDance implements IPreprocessor {
    private Context mContext;
    private EffectHelper mEffectHelper;
    private boolean mEnabled = false;

    // This path is an example filter
    private String mDefaultFilterPath;

    public PreprocessorByteDance(Context context) {
        mContext = context;
        mDefaultFilterPath = getDefaultFilterPath();
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

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame frame, VideoChannel.ChannelContext channelContext) {
        if (!mEnabled) return frame;
        int result = mEffectHelper.processTexture(
                frame.textureId,
                frame.format.getTexFormat(),
                frame.format.getWidth(),
                frame.format.getHeight(),
                frame.rotation,
                frame.timestamp);

        if (result != -1 && result != frame.textureId) {
            frame.textureId = result;
            frame.format.setTexFormat(GLES20.GL_TEXTURE_2D);
        }
        return frame;
    }

    @Override
    public void initPreprocessor() {
        mEffectHelper = new EffectHelper(mContext);
        mEffectHelper.initEffectSDK();
        mEnabled = true;
        setFilter(mDefaultFilterPath);
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEffectHelper.setEffectOn(enabled);
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext channelContext) {
        mEffectHelper.destroyEffectSDK();
    }

    public void setFilter(String path) {
        mEffectHelper.setFilter(path);
    }

    public void updateFilterIntensity(float intensity) {
        mEffectHelper.updateFilterIntensity(intensity);
    }

    public boolean initialized() {
        return mEffectHelper != null && mEffectHelper.initialized();
    }
}
