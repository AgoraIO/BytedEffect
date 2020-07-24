package io.agora.rtcwithbyte;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.Constant;
import io.agora.capture.video.camera.VideoCapture;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST = 1;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    private CameraVideoManager mCameraVideoManager;
    private PreprocessorByteDance mByteDanceEffect;
    private TextureView mVideoSurface;
    private boolean mPermissionGranted;
    private boolean mFinished;
    private boolean mIsMirrored = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoSurface = findViewById(R.id.local_video_surface);
        checkCameraPermission();
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
        mByteDanceEffect = new PreprocessorByteDance(this);
        mCameraVideoManager = new CameraVideoManager(this, mByteDanceEffect);

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
        });

        // Set camera capture configuration
        mCameraVideoManager.setPictureSize(640, 480);
        mCameraVideoManager.setFrameRate(24);
        mCameraVideoManager.setFacing(Constant.CAMERA_FACING_FRONT);
        mCameraVideoManager.setLocalPreviewMirror(toMirrorMode(mIsMirrored));

        // The preview surface is actually considered as
        // an on-screen consumer under the hood.
        mCameraVideoManager.setLocalPreview(mVideoSurface, "Surface1");

        // Can attach other consumers here,
        // For example, rtc consumer or rtmp module
        mCameraVideoManager.startCapture();
    }

    public void onCameraChange(View view) {
        if (mCameraVideoManager != null) {
            mCameraVideoManager.switchCamera();
        }
    }

    public void onEffectEnabled(View view) {
        if (mByteDanceEffect != null && mByteDanceEffect.initialized()) {
            boolean enabled = mByteDanceEffect.isEnabled();
            mByteDanceEffect.enablePreProcess(!enabled);
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
}
