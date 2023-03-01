package io.agora.rtcwithbyte.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import io.agora.beauty.bytedance.IBeautyByteDance;
import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.Constant;
import io.agora.capture.video.camera.VideoCapture;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.framework.PreprocessorByteDance;
import io.agora.rtcwithbyte.framework.RtcVideoConsumer;

public class ByteChatActivity extends RtcBasedActivity {
    private static final String TAG = ByteChatActivity.class.getSimpleName();
    private static final int REQUEST = 1;
    private EffectOptionContainer mEffectContainer;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private CameraVideoManager mCameraVideoManager;
    private TextureView mVideoSurface;
    private boolean mPermissionGranted;
    private boolean mFinished;
    private boolean mIsMirrored = false;
    private int mRemoteUid = -1;
    private FrameLayout mRemoteViewContainer;

    private PreprocessorByteDance preprocessor;
    private String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        channelName = intent.getStringExtra(io.agora.rtcwithbyte.utils.Constant.ACTION_KEY_ROOM_NAME);
        initUI();
        checkCameraPermission();
        initRoom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtcEngine().setVideoSource(null);
        rtcEngine().leaveChannel();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        mVideoSurface = findViewById(R.id.local_video_surface);
        mEffectContainer = findViewById(R.id.effect_container);
        mEffectContainer.setEffectOptionItemListener(new EffectListener());
        initRemoteViewLayout();
    }

    private void initRoom() {
        rtcEngine().setVideoSource(new RtcVideoConsumer());
        joinChannel();
    }

    private void joinChannel() {
        rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));
        rtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);

        rtcEngine().joinChannel(null, channelName == null ? "ByteDemoChannel" : channelName, null, 0);
    }

    private void checkCameraPermission() {
        if (permissionGranted(Manifest.permission.CAMERA)) {
            onPermissionGranted();
            mPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
        }
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean granted = true;
        if (requestCode == REQUEST) {
            for (String permission : permissions) {
                if (!permissionGranted(permission)) {
                    granted = false;
                }
            }
        }

        if (granted) {
            onPermissionGranted();
            mPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST);
        }
    }

    private void onPermissionGranted() {
        initCamera();
    }

    private void initCamera() {
        mCameraVideoManager = videoManager();
        preprocessor = (PreprocessorByteDance) mCameraVideoManager.getPreprocessor();
        mCameraVideoManager.setCameraStateListener(new VideoCapture.VideoCaptureStateListener() {
            @Override
            public void onFirstCapturedFrame(int width, int height) {
                Log.i(TAG, "onFirstCapturedFrame: " + width + "x" + height);
            }

            @Override
            public void onCameraCaptureError(int error, String message) {
                Log.i(TAG, "onCameraCaptureError: error:" + error + " " + message);
                if (mCameraVideoManager != null) {
                    // When there is a camera error, the capture should
                    // be stopped to reset the internal states.
                    mCameraVideoManager.stopCapture();
                }
            }

            @Override
            public void onCameraOpen() {

            }

            @Override
            public void onCameraClosed() {

            }

            @Override
            public VideoCapture.FrameRateRange onSelectCameraFpsRange(List<VideoCapture.FrameRateRange> list, VideoCapture.FrameRateRange frameRateRange) {
                return null;
            }
        });

        // Set camera capture configuration
        mCameraVideoManager.setPictureSize(1280, 720);
        mCameraVideoManager.setFrameRate(15);
        mCameraVideoManager.setFacing(Constant.CAMERA_FACING_FRONT);
        mCameraVideoManager.setLocalPreviewMirror(toMirrorMode(mIsMirrored));

        // The preview surface is actually considered as
        // an on-screen consumer under the hood.
        mCameraVideoManager.setLocalPreview(mVideoSurface, "Surface1");

        // Can attach other consumers here,
        // For example, rtc consumer or rtmp module
        mCameraVideoManager.startCapture();
        updateEffectOptionPanel();
    }

    public void onCameraChange(View view) {
        if (mCameraVideoManager != null) {
            mCameraVideoManager.switchCamera();
        }
    }

    public void onEffectEnabled(View view) {
        if (preprocessor != null) {
            boolean enabled = preprocessor.isEnabled();
            preprocessor.enablePreProcess(!enabled);
        }
    }

    public void onMirrorModeChanged(View view) {
        if (mCameraVideoManager != null) {
            mIsMirrored = !mIsMirrored;
            mCameraVideoManager.setLocalPreviewMirror(toMirrorMode(mIsMirrored));
        }
    }

    private int toMirrorMode(boolean isMirrored) {
        return isMirrored ? Constant.MIRROR_MODE_ENABLED : Constant.MIRROR_MODE_DISABLED;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPermissionGranted && mCameraVideoManager != null) {
            mCameraVideoManager.startCapture();
        }
    }

    @Override
    public void finish() {
        super.finish();
        mFinished = true;
        if (mCameraVideoManager != null) mCameraVideoManager.stopCapture();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mFinished && mCameraVideoManager != null) mCameraVideoManager.stopCapture();
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i(TAG, "onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL));
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.i(TAG, "onUserJoined " + (uid & 0xFFFFFFFFL));
        runOnUiThread(() -> {
            if (mRemoteViewContainer.getChildCount() > 0
                    && mRemoteViewContainer.getTag() instanceof Integer
                    && (int) mRemoteViewContainer.getTag() == uid) {
                mRemoteViewContainer.removeAllViews();
            }
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.i(TAG, "onUserJoined " + (uid & 0xFFFFFFFFL));
        runOnUiThread(() -> {
            if (mRemoteViewContainer.getChildCount() > 0) {
                mRemoteViewContainer.removeAllViews();
            }
            SurfaceView surfaceView = RtcEngine.CreateRendererView(ByteChatActivity.this);
            rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, Constants.RENDER_MODE_FIT, uid));
            mRemoteViewContainer.addView(surfaceView);
            mRemoteViewContainer.setTag(uid);
        });
    }


    private class EffectListener implements EffectOptionContainer.OnEffectOptionContainerItemClickListener {
        @Override
        public void onEffectOptionItemClicked(int index, int textResource, boolean selected) {
            Log.i(TAG, "onEffectOptionItemClicked " + index + " " + selected);
            if (preprocessor != null) {
                switch (index) {
                    case 0:
                        setBeautificationOn(selected);
                        break;
                    case 1:
                        setMakeupItemParam(selected);
                        break;
                    case 2:
                        setStickerItem(selected);
                        break;
                    case 3:
                        setBeautyBody(selected);
                        break;
                }
            }
        }

        @Override
        public void onEffectNotSupported(int index, int textResource) {
            Toast.makeText(ByteChatActivity.this, R.string.sorry_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    private void setBeautyBody(boolean selected) {
        IBeautyByteDance beautyByteDance = preprocessor.getBeautyByteDance();
        if (beautyByteDance != null) {
            beautyByteDance.setBodyBeautifyEnable(selected);
        }
    }

    private void setStickerItem(boolean selected) {
        IBeautyByteDance beautyByteDance = preprocessor.getBeautyByteDance();
        if (beautyByteDance != null) {
            beautyByteDance.setStickerEnable(selected);
        }
    }

    private void setBeautificationOn(boolean selected) {
        IBeautyByteDance beautyByteDance = preprocessor.getBeautyByteDance();
        if (beautyByteDance != null) {
            beautyByteDance.setFaceBeautifyEnable(selected);
        }
    }

    private void setMakeupItemParam(boolean selected) {
        IBeautyByteDance beautyByteDance = preprocessor.getBeautyByteDance();
        if (beautyByteDance != null) {
            beautyByteDance.setMakeUpEnable(selected);
        }
    }

    private void updateEffectOptionPanel() {
        // Beautification
        mEffectContainer.setItemViewStyles(0, false, true);
        // Sticker
        mEffectContainer.setItemViewStyles(2, false, true);
    }

    private void initRemoteViewLayout() {
        mRemoteViewContainer = findViewById(R.id.remote_video_layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) mRemoteViewContainer.getLayoutParams();
        params.width = displayMetrics.widthPixels / 3;
        params.height = displayMetrics.heightPixels / 3;
        mRemoteViewContainer.setLayoutParams(params);
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        Log.i(TAG, "onRemoteVideoStateChanged " + (uid & 0xFFFFFFFFL) + " " + state + " " + reason);
        if (mRemoteUid == -1 && state == io.agora.rtc.Constants.REMOTE_VIDEO_STATE_DECODING) {
            runOnUiThread(() -> {
                mRemoteUid = uid;
                setRemoteVideoView(uid);
            });
        }
    }

    private void setRemoteVideoView(int uid) {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(this);
        rtcEngine().setupRemoteVideo(new VideoCanvas(
                surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteViewContainer.addView(surfaceView);
    }

}
