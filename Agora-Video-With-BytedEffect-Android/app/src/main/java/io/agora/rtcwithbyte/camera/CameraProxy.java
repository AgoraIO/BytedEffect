package io.agora.rtcwithbyte.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import library.LogUtils;


public class CameraProxy {
    private static final String TAG = "CameraProxy";
    private boolean isDebug = true;
    private Context mContext;
    private int mCameraId;
    private CameraInterface mCamera;
    private TextureHolder textureHolder;

    public CameraProxy(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCamera = new Camera2();
        } else {
            mCamera = new Camera1();
        }
        textureHolder = new TextureHolder();
        mCamera.init(context);
    }

    public boolean isCameraValid() {
        return mCamera.currentValid();
    }

    public boolean openCamera(final int cameraId, final CameraListener listener) {
        try {
            mCamera.open(cameraId, new CameraListener() {
                @Override
                public void onOpenSuccess() {
                    mCamera.initCameraParam();
                    listener.onOpenSuccess();
                    mCameraId = cameraId;
                }

                @Override
                public void onOpenFail() {
                    listener.onOpenFail();

                }
            });
        } catch (Exception e) {
            mCamera = null;
            Log.i(TAG, "openCamera fail msg=" + e.getMessage());
            return false;
        }
        return true;
    }

    public void changeCamera(final int cameraId, final CameraListener listener) {
        try {
            mCamera.changeCamera(cameraId, new CameraListener() {
                @Override
                public void onOpenSuccess() {
                    mCamera.initCameraParam();
                    listener.onOpenSuccess();
                    mCameraId = cameraId;
                }

                @Override
                public void onOpenFail() {
                    listener.onOpenFail();
                }
            });
        } catch (Exception e) {
            mCamera = null;
            Log.i(TAG, "openCamera fail msg=" + e.getMessage());
        }
    }


    public void releaseCamera() {
        mCamera.close();
    }

    public void updateTexture() {
        textureHolder.updateTexImage();
    }

    public long getTimeStamp()
    {
        if (textureHolder.getSurfaceTexture() == null) {
            return System.currentTimeMillis();
        }
        return textureHolder.getSurfaceTexture().getTimestamp();
    }


    public void startPreview(SurfaceTexture.OnFrameAvailableListener listener) {
        LogUtils.d("startPreview");
        textureHolder.onCreate(listener);
        mCamera.startPreview(textureHolder.getSurfaceTexture());
    }

    public void deleteTexture() {
        textureHolder.onDestroy();
    }

    public int getOrientation() {
        return mCamera.getOrientation();
    }

    public boolean isFlipHorizontal() {
        return mCamera.isFlipHorizontal();
    }

    public int getCameraId() {
        return mCameraId;
    }

    public boolean isFrontCamera() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public int getPreviewHeight() {
        return mCamera.getPreviewWH()[1];
    }

    public int getPreviewWidth() {
        return mCamera.getPreviewWH()[0];
    }

    public int getPreviewTexture() {
        return textureHolder.getmSurfaceTextureID();
    }

}
