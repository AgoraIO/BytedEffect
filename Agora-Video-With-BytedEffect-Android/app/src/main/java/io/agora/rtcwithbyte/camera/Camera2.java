package io.agora.rtcwithbyte.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangcihang on 2018/2/22.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2 implements CameraInterface {
    private static final String TAG = "Camera2";
    private CameraManager manager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private CaptureRequest.Builder mPreviewBuilder;
    private int cameraRotate;
    private int sWidth;
    private int sHeight;
    private Size[] outputSizes;
    private CameraListener listener;
    private int currentCameraPosition = -1;
    private Surface mPreviewSurface;

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            if (listener != null) {
                listener.onOpenSuccess();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            close();
            resetCameraVariables();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            if (listener != null) {
                listener.onOpenFail();
                listener = null;
            }
            mCameraDevice = camera;
            close();
            resetCameraVariables();
        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };
    private CameraCharacteristics cameraInfo;
    private CaptureRequest mPreviewRequest;

    @Override
    public void init(Context context) {
        if (manager == null) {
            manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
    }

    private void resetCameraVariables() {
        mCameraDevice = null;
        mPreviewBuilder = null;
        mPreviewSession = null;
        cameraInfo = null;
        mPreviewRequest = null;
    }

    @Override
    public boolean open(int position, CameraListener aListener) {
        this.listener = aListener;
        try {
            String[] cameraList = manager.getCameraIdList();
            if (position < 0 || position > 2) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onOpenFail();
                        }
                    }
                });
                return false;
            }
            // TODO: 2018/2/22
            if (position >= cameraList.length)
                position = CAMERA_FRONT;
            currentCameraPosition = position;
            String currentCameraId = cameraList[position];
            cameraInfo = manager.getCameraCharacteristics(currentCameraId);
            StreamConfigurationMap streamConfigurationMap = cameraInfo.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            cameraRotate = cameraInfo.get(CameraCharacteristics.SENSOR_ORIENTATION);
            outputSizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            getBestMatchCameraPreviewSize(outputSizes);
            manager.openCamera(currentCameraId, mStateCallback, mainHandler);
            return true;
        } catch (Throwable e) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onOpenFail();
                        listener = null;
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void enableTorch(boolean enable) {
        if (mPreviewBuilder == null || mPreviewSession == null) {
            return;
        }
        try {
            mPreviewBuilder.set(CaptureRequest.FLASH_MODE,
                    enable ? CameraMetadata.FLASH_MODE_TORCH : CameraMetadata.FLASH_MODE_OFF);
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            if (mPreviewSurface != null) {
                mPreviewSurface.release();
                mPreviewSurface = null;
            }
        } catch (Throwable e) {
            // Just ignore any errors
        }
        listener = null;
    }

    /**
     * 设置预览大小(通过surfaceTexture)
     */
    @Override
    public void startPreview(@NonNull SurfaceTexture surfaceTexture) {
        if (null == mCameraDevice || surfaceTexture == null) {
            return;
        }
        try {
            surfaceTexture.setDefaultBufferSize(sWidth, sHeight);

            //有TEMPLATE_RECORD TEMPLATE_PREVIEW 等模式进行选择
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            mPreviewSurface = new Surface(surfaceTexture);
            surfaces.add(mPreviewSurface);
            mPreviewBuilder.addTarget(mPreviewSurface);
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                }
            }, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the camera preview. {@link #startPreview(SurfaceTexture surfaceTexture)} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice || mPreviewBuilder == null) {
            return;
        }
        try {
            mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mPreviewRequest = mPreviewBuilder.build();
            mPreviewSession.setRepeatingRequest(mPreviewRequest, captureCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void changeCamera(int cameraPosition, CameraListener listener) {
        close();
        open(cameraPosition, listener);
    }

    @Override
    public int[] initCameraParam() {
        return new int[]{sWidth, sHeight};
    }

    @Override
    public int[] getPreviewWH() {
        return new int[]{sWidth, sHeight};
    }

    @Override
    public void setZoom(float scaleFactor) {

    }

    @Override
    public boolean isTorchSupported() {
        boolean flashAvailable = false;
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics("0");
            flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        } catch (CameraAccessException e) {
            // Just ignore
        }

        return flashAvailable;
    }

    @Override
    public void cancelAutoFocus() {
        if (!isMeteringAreaAFSupported() || mPreviewBuilder == null || mCameraDevice == null) {
            return;
        }
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);

        mPreviewRequest = mPreviewBuilder.build();
        try {
            mPreviewSession.setRepeatingRequest(mPreviewRequest, null, mainHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "setRepeatingRequest failed, errMsg: " + e.getMessage());
        }
    }

    @Override
    public boolean currentValid() {
        return mCameraDevice != null;
    }

    @Override
    public boolean setFocusAreas(View view, float[] pos, int rotation) {
        if (!isMeteringAreaAFSupported() || mPreviewBuilder == null || mCameraDevice == null) {
            return false;
        }
        float x = pos[0];
        float y = pos[1];
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        int realPreviewWidth = sWidth;
        int realPreviewHeight = sHeight;
        float tmp;

        if (90 == cameraRotate || 270 == cameraRotate) {
            realPreviewWidth = sHeight;
            realPreviewHeight = sWidth;
        }
        // 计算摄像头取出的图像相对于view放大了多少，以及有多少偏移
        float imgScale = 1.0f, verticalOffset = 0, horizontalOffset = 0;
        if (realPreviewHeight * viewWidth > realPreviewWidth * viewHeight) {
            imgScale = viewWidth * 1.0f / realPreviewWidth;
            verticalOffset = (realPreviewHeight - viewHeight / imgScale) / 2;
        } else {
            imgScale = viewHeight * 1.0f / realPreviewHeight;
            horizontalOffset = (realPreviewWidth - viewWidth / imgScale) / 2;
        }
        // 将点击的坐标转换为图像上的坐标
        x = x / imgScale + horizontalOffset;
        y = y / imgScale + verticalOffset;
        if (90 == rotation) {
            tmp = x;
            x = y;
            y = sHeight - tmp;
        } else if (270 == rotation) {
            tmp = x;
            x = sWidth - y;
            y = tmp;
        }

        // 计算取到的图像相对于裁剪区域的缩放系数，以及位移
        Rect cropRegion = mPreviewRequest.get(CaptureRequest.SCALER_CROP_REGION);
        if (null == cropRegion) {
            Log.e(TAG, "can't get crop region");
            cropRegion = new Rect(0, 0, 1, 1);
        }

        int cropWidth = cropRegion.width(), cropHeight = cropRegion.height();
        if (sHeight * cropWidth > sWidth * cropHeight) {
            imgScale = cropHeight * 1.0f / sHeight;
            verticalOffset = 0;
            horizontalOffset = (cropWidth - imgScale * sWidth) / 2;
        } else {
            imgScale = cropWidth * 1.0f / sWidth;
            horizontalOffset = 0;
            verticalOffset = (cropHeight - imgScale * sHeight) / 2;
        }

        // 将点击区域相对于图像的坐标，转化为相对于成像区域的坐标
        x = x * imgScale + horizontalOffset + cropRegion.left;
        y = y * imgScale + verticalOffset + cropRegion.top;

        double tapAreaRatio = 0.1;
        Rect rect = new Rect();
        rect.left = clamp((int) (x - tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
        rect.right = clamp((int) (x + tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
        rect.top = clamp((int) (y - tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());
        rect.bottom = clamp((int) (y + tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());


        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[]{new MeteringRectangle(rect, 1000)});
        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        mPreviewRequest = mPreviewBuilder.build();
        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), captureCallback, mainHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "setRepeatingRequest failed, " + e.getMessage());
            return false;
        }
        return true;
    }

    private int clamp(int x, int min, int max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }

    @Override
    public List<int[]> getSupportedPreviewSizes() {
        List<int[]> retSizes = new ArrayList<>();
        for (Size size : outputSizes) {
            retSizes.add(new int[]{size.getWidth(), size.getHeight()});
        }

        return retSizes;
    }

    private void getBestMatchCameraPreviewSize(Size[] ret) {
        if (ret != null) {
            int exactWidth = -1, exactHeight = -1;
            int bestWidth = -1, bestHeight = -1;
            for (Size size : ret) {
                int width = size.getWidth();
                int height = size.getHeight();
                if (width == 1280 && height == 720) {
                    exactHeight = height;
                    exactWidth = width;
                    break;
                }
                /*
                 * 所有手机都支持 4:3 和16:9的分辨率
                 * 如果没有1280*720的, 选取1000以下的最大的(包括720*480)
                 */
                if (Math.abs(width * 9 - height * 16) < 16 * 2) {
                    if (bestHeight < height) {
                        bestWidth = width;
                        bestHeight = height;
                    }
                }

                if (Math.abs(width * 3 - height * 4) < 16 * 2) {
                    if (bestHeight < height) {
                        bestWidth = width;
                        bestHeight = height;
                    }
                }
            }
            if (exactHeight != -1) {
                sWidth = exactWidth;
                sHeight = exactHeight;
            } else {
                sWidth = bestWidth;
                sHeight = bestHeight;
            }
        }
    }

    private boolean isMeteringAreaAFSupported() {
        boolean result = false;
        if (cameraInfo != null) {
            result = cameraInfo.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
        } else {
            CameraCharacteristics cameraCharacteristics = null;
            try {
                cameraCharacteristics = manager.getCameraCharacteristics("0");
                result = cameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public int getCameraPosition() {
        return currentCameraPosition;
    }

    @Override
    public boolean isVideoStabilizationSupported() {
        int[] list = null;
        if (cameraInfo != null) {
            list = cameraInfo.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
            return list != null && list.length > 0;
        }
        return false;
    }

    @Override
    public boolean setVideoStabilization(boolean toggle) {
        if (isVideoStabilizationSupported() && mPreviewBuilder != null) {
            mPreviewBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, toggle ?
                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON : CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF);
            mPreviewRequest = mPreviewBuilder.build();
            try {
                mPreviewSession.setRepeatingRequest(mPreviewRequest, null, mainHandler);
                return true;
            } catch (CameraAccessException e) {
                Log.e(TAG, "setRepeatingRequest failed, errMsg: " + e.getMessage());
            }
        }
        return false;
    }


    @Override
    public int getOrientation() {
        if (cameraInfo == null) return 0;
        return cameraInfo.get(CameraCharacteristics.SENSOR_ORIENTATION);
    }

    @Override
    public boolean isFlipHorizontal() {
        if (cameraInfo == null) return true;
        int cameraId = cameraInfo.get(CameraCharacteristics.LENS_FACING);
        return cameraId == CameraCharacteristics.LENS_FACING_FRONT;
    }
}
