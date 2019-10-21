// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package io.agora.rtcwithbyte;


import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;


import java.io.IOException;
import java.util.List;

import io.agora.rtcwithbyte.activities.MainActivity;
import io.agora.rtcwithbyte.utils.AppUtils;
import library.LogUtils;

/**
 * 相机管理类
 * Camera management class
 */
public class CameraDevice {
    public static final int RETRY_OPEN_CAMERA = 3;
    private static CameraDevice mCameraDevice = new CameraDevice();

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Camera mCamera = null;
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();

    private boolean mSupprot720p = false;
    private boolean mSupprot480p = false;


    private int[] mPreviewSize = new int[2];
    private MainActivity mainActivity;

    public void setMainActivity(MainActivity a) {
        this.mainActivity = a;
    }

    public static CameraDevice get() {
        return mCameraDevice;
    }

    public boolean isFront() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    private Rect screenRect;
    private Rect frameRect;

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080

    private Point screenResolution;

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
    }

    public void setPreviewSize(int width, int height) {
        mPreviewSize[0] = Math.min(width, height);
        mPreviewSize[1] = Math.max(width, height);
    }

    public void openCamera() {
        openCamera(mPreviewSize);
    }

    public void openCamera(int[] previewSize) {
        // use task to open camera
        LogUtils.v("openCamera");
        for (int i = 0; i < CameraDevice.RETRY_OPEN_CAMERA; i++) {
            try {
                if (Camera.getNumberOfCameras() < 2) {
                    mCameraId = 0;
                }
                mCamera = Camera.open(mCameraId);
                setDefaultParameters(previewSize);
                mCamera.getCameraInfo(mCameraId, mCameraInfo);
                break;
            } catch (Exception e) {
                // wait a while to try again
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();

            }
        }
    }




    /**
     * 必须先调用prepareSurfaceTexture
     * must call prepareSurfaceTexture first
     */
    public void startPreview(SurfaceTexture surfaceTexture, Camera.PreviewCallback previewCallback) {
        try {
            if (mCamera == null) {
                return;
            }
            LogUtils.v("CameraDevice startPreview");
            mCamera.setPreviewTexture(surfaceTexture);
            setPreviewCallbackWithBuffer(previewCallback);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback) {
        if (mCamera == null || previewCallback == null) {
            return;
        }
        mCamera.setPreviewCallbackWithBuffer(previewCallback);
        mCamera.addCallbackBuffer(new byte[getPreviewHeight() * getPreviewWidth() * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]);

    }


    /**
     * 必须先调用prepareSurfaceTexture
     * must call prepareSurfaceTexture first
     */
    public void startPreview(SurfaceTexture surfaceTexture) {
        startPreview(surfaceTexture, null);
    }

    public Camera.Size  getPreviewSize() {
        if (mCamera != null) {
            return  mCamera.getParameters().getPreviewSize();
        }
        return null;
    }

    public Camera getCamera() {
        return mCamera;
    }

    /**
     * 竖屏
     * Vertical screen
     */
    public int getPreviewWidth() {
        Camera.Size size = getPreviewSize();
        return size != null?size.width:0;
    }

    public int getPreviewHeight() {
        Camera.Size  size = getPreviewSize();
        return size != null?size.height:0;
    }

    /**
     * 必须之后调用deleteSurfaceTexture
     * must call deleteSurfaceTexture later
     */
    public void closeCamera() {
        if (mCamera != null) {
            LogUtils.v("CameraDevice close");
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    public int getOrientation() {
        return mCameraInfo.orientation;
    }

    public boolean isFlipHorizontal() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT || getCameraNum() == 1 ? true : false;
    }

    public int getCameraNum(){
        return Camera.getNumberOfCameras();
    }

    public void setPreviewFormat(int format){
        Camera.Parameters parameters = mCamera.getParameters();
        List<Integer> formatsList = parameters.getSupportedPreviewFormats();
        for (Integer item:formatsList){
            LogUtils.d("SupportedPreviewFormat："+item);

        }
        if (formatsList.contains(format)){
            parameters.setPreviewFormat(format);
        }
    }

    public int getPreviewFormat(){
        return mCamera.getParameters().getPreviewFormat();
    }

    private void setDefaultParameters(int[] previewSize) {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }

        Point point = null;
        if (previewSize != null && previewSize[0] > 0 && previewSize[1] > 0) {
            point = new Point(previewSize[1], previewSize[0]);
        } else {
            point = getSuitablePreviewSize();
        }

        parameters.setPreviewSize(point.x, point.y);
        mCamera.setParameters(parameters);
    }

    private Point getSuitablePreviewSize() {
        Point size720p = new Point(1280, 720);
        Point size480p = new Point(640, 480);
        Point size1080p = new Point(1920, 1080);

        if (mCamera != null) {
            List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
            for (Camera.Size s : sizes) {
//                if (AppUtils.isDebug()) {
//                    if (null == mainActivity.cameraInfo) {
//                        mainActivity.cameraInfo = new StringBuilder();
//                    }
//                    mainActivity.cameraInfo.append("s.width ="+s.width + " s.height = "+s.height + "||");
//                }

                if ((s.width == size720p.x) && (s.height == size720p.y)) {
                    mSupprot720p = true;
                } else if ((s.width == size480p.x) && (s.height == size480p.y)) {
                    mSupprot480p = true;
                } else if ((s.width == size1080p.x) && (s.height == size1080p.y)) {

                }
            }

//            if (AppUtils.isDebug()) {
//                mainActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mainActivity.tvcameraInfo.setText(mainActivity.cameraInfo.toString());
//                    }
//                });
//            }


            if (mSupprot720p) {
                return size720p;
            } else if (mSupprot480p) {
                return size480p;
            } else {
                return new Point(sizes.get(0).width, sizes.get(0).height);
            }
        }
        return null;
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public Rect getScreenRect(Context context) {
        if (screenRect == null){
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            screenResolution = new Point();
            display.getSize(screenResolution);
            int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
            int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);
             width = Math.min(width, height);
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - width) / 2;
            screenRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + width);
        }
        return screenRect;

    }

    public Rect getFrameRect(int frameWidth, int frameHeight) {
        if (frameRect == null) {
            if (null == screenRect || null == screenResolution){
                return null;
            }
            frameRect = new Rect();
            frameRect.left = screenRect.left * frameWidth / screenResolution.x;
            frameRect.right = screenRect.right * frameWidth / screenResolution.x;
            frameRect.top = screenRect.top * frameHeight / screenResolution.y;
            frameRect.bottom = screenRect.bottom * frameHeight / screenResolution.y;
        }
        return frameRect;

    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dim = 5 * resolution / 8; // Target 5/8 of each dimension
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }


}
