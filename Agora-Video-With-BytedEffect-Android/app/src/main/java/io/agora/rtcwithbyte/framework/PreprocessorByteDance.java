package io.agora.rtcwithbyte.framework;

import android.content.Context;

import io.agora.capture.framework.modules.channels.VideoChannel;
import io.agora.capture.framework.modules.processors.IPreprocessor;
import io.agora.capture.video.camera.VideoCaptureFrame;

public class PreprocessorByteDance implements IPreprocessor {
    private final static String TAG = PreprocessorByteDance.class.getSimpleName();

    private Context mContext;
    private boolean mEnabled;

    public PreprocessorByteDance(Context context) {
        mContext = context;
        mEnabled = true;
    }

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame outFrame, VideoChannel.ChannelContext context) {
        return outFrame;
    }

    @Override
    public void initPreprocessor() {
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext context) {
    }
}