package io.agora.rtcwithbyte.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.Constant;
import io.agora.capture.video.camera.VideoCapture;
import io.agora.extension.ResourceHelper;
import io.agora.extension.UtilsAsyncTask;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.framework.RtcVideoConsumer;

public class ByteChatActivity extends RtcBasedActivity implements UtilsAsyncTask.OnUtilsAsyncTaskEvents, io.agora.rtc2.IMediaExtensionObserver {
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

        checkResource();
        iniRoom();
    }

    private void checkResource() {
        if (!ResourceHelper.isResourceReady(this, 1)) {
            onPrepareStatus();
            new UtilsAsyncTask(this, this).execute();
        } else {
            onCompletedStatus();
        }
    }

    private void onPrepareStatus() {
    }

    private void onCompletedStatus() {
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

        joinChannel();
    }

    private void joinChannel() {
        rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_960x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        String roomName = getIntent().getStringExtra(io.agora.rtcwithbyte.utils.Constant.ACTION_KEY_ROOM_NAME);
        rtcEngine().setClientRole(io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER);

        rtcEngine().joinChannel(null, roomName, null, 0);
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
    protected void onResume() {
        super.onResume();
        mVideoManager.startCapture();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoManager.stopCapture();
    }

    @Override
    protected void onDestroy() {
        mRtcVideoConsumer.onDispose();
        super.onDestroy();
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute() {
        ResourceHelper.setResourceReady(this, true, 1);
        onCompletedStatus();
    }

    @Override
    public void onEvent(String s, String s1, String s2) {

    }
}
