package io.agora.rtcwithbyte;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.extension.ExtensionManager;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtcwithbyte.framework.PreprocessorByteDance;

import static android.content.ContentValues.TAG;

public class MyApplication extends Application {
    private CameraVideoManager mVideoManager;
    private RtcEngine mRtcEngine;
    private RtcEngineEventHandlerProxy mRtcEventHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        initRtcEngine();
        initVideoCapture();
    }

    private void initRtcEngine() {
        String appId = getString(R.string.agora_app_id);
        if (TextUtils.isEmpty(appId)) {
            throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
        }

        mRtcEventHandler = new RtcEngineEventHandlerProxy();

        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = this;
        config.mAppId = appId;
        config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        config.mEventHandler = mRtcEventHandler;
        config.mAudioScenario = Constants.AudioScenario
                .getValue(Constants.AudioScenario.HIGH_DEFINITION);
        long videoProvider = ExtensionManager.nativeGetExtensionProvider(this, ExtensionManager.VENDOR_NAME_VIDEO,
                ExtensionManager.PROVIDER_TYPE.LOCAL_VIDEO_FILTER.ordinal());
        long audioProvider = ExtensionManager.nativeGetExtensionProvider(this, ExtensionManager.VENDOR_NAME_AUDIO,
                ExtensionManager.PROVIDER_TYPE.LOCAL_AUDIO_FILTER.ordinal());
        config.addExtension(ExtensionManager.VENDOR_NAME_VIDEO, videoProvider);
        config.addExtension(ExtensionManager.VENDOR_NAME_AUDIO, audioProvider);
        config.mExtensionObserver = new IMediaExtensionObserver() {
            @Override
            public void onEvent(String s, String s1, String s2) {

            }
        };

        try {
            mRtcEngine = RtcEngine.create(config);
            mRtcEngine.enableVideo();
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void initVideoCapture() {
        Context application = getApplicationContext();
        mVideoManager = new CameraVideoManager(application, new PreprocessorByteDance(application));
        Log.i(TAG, mVideoManager.toString());
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public void addRtcHandler(RtcEngineEventHandler handler) {
        mRtcEventHandler.addEventHandler(handler);
    }

    public void removeRtcHandler(RtcEngineEventHandler handler) {
        mRtcEventHandler.removeEventHandler(handler);
    }

    public CameraVideoManager videoManager() {
        return mVideoManager;
    }

}
