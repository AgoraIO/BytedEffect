package io.agora.rtcwithbyte.framework;

import android.content.Context;
import android.opengl.GLES20;

import io.agora.beauty.bytedance.IBeautyByteDance;
import io.agora.capture.framework.modules.channels.VideoChannel;
import io.agora.capture.framework.modules.processors.IPreprocessor;
import io.agora.capture.video.camera.VideoCaptureFrame;

public class PreprocessorByteDance implements IPreprocessor {
    private final Context mContext;
    private boolean mEnabled = false;

    private IBeautyByteDance beautyByteDance;

    public PreprocessorByteDance(Context context) {
        mContext = context;
    }

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame frame, VideoChannel.ChannelContext channelContext) {
        if (!mEnabled || beautyByteDance == null) return frame;
        frame.textureId = beautyByteDance.process(
                frame.textureId,
                frame.format.getTexFormat(),
                frame.format.getWidth(),
                frame.format.getHeight(),
                frame.rotation
        );
        frame.format.setTexFormat(GLES20.GL_TEXTURE_2D);
        return frame;
    }

    @Override
    public void initPreprocessor() {
        mEnabled = true;
        if (beautyByteDance == null) {
            beautyByteDance = IBeautyByteDance.create(mContext);
        }
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext channelContext) {
        if (beautyByteDance != null) {
            beautyByteDance.release();
        }
    }

    public IBeautyByteDance getBeautyByteDance() {
        return beautyByteDance;
    }
}
