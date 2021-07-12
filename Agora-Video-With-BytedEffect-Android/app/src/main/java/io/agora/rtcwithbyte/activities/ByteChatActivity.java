package io.agora.rtcwithbyte.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.Constant;
import io.agora.capture.video.camera.VideoCapture;
import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.framework.RtcVideoConsumer;

public class ByteChatActivity extends RtcBasedActivity {
    private final static String TAG = ByteChatActivity.class.getSimpleName();

    private static final int CAPTURE_WIDTH = 1280;
    private static final int CAPTURE_HEIGHT = 720;
    private static final int CAPTURE_FRAME_RATE = 24;

    private CameraVideoManager mVideoManager;

    private RtcVideoConsumer mRtcVideoConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniRoom();
    }

    private void initVideoModule() {
        mVideoManager = videoManager();
        mVideoManager.setCameraStateListener(new VideoCapture.VideoCaptureStateListener() {
            @Override
            public void onFirstCapturedFrame(int width, int height) {
                Log.i(TAG, "onFirstCapturedFrame: " + width + "x" + height);
            }

            @Override
            public void onCameraCaptureError(int error, String msg) {
                Log.i(TAG, "onCameraCaptureError: error:" + error + " " + msg);
                if (mVideoManager != null) {
                    // When there is a camera error, the capture should
                    // be stopped to reset the internal states.
                    mVideoManager.stopCapture();
                }
            }

            @Override
            public void onCameraClosed() {

            }
        });

        mVideoManager.setPictureSize(CAPTURE_WIDTH, CAPTURE_HEIGHT);
        mVideoManager.setFrameRate(CAPTURE_FRAME_RATE);
        mVideoManager.setFacing(Constant.CAMERA_FACING_FRONT);
        mVideoManager.setLocalPreviewMirror(Constant.MIRROR_MODE_AUTO);

        TextureView localVideo = findViewById(R.id.local_video_surface);
        mVideoManager.setLocalPreview(localVideo);
    }

    private void iniRoom() {
        initVideoModule();

        rtcEngine().setExternalVideoSource(true, false, false);
        mRtcVideoConsumer = new RtcVideoConsumer(rtcEngine());
        mRtcVideoConsumer.onStart();
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {

    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {

    }

    @Override
    protected void onDestroy() {
        mRtcVideoConsumer.onDispose();
        super.onDestroy();
    }
}
