package io.agora.rtcwithbyte.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.View;

import java.util.List;



public interface CameraInterface {
    int CAMERA_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    int CAMERA_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    void init(Context context);

    boolean open(int position, CameraListener listener);

    void enableTorch(boolean enable);

    void close();

    void startPreview(SurfaceTexture surfaceTexture);

    void changeCamera(int cameraPosition, CameraListener cameraListener);
    //返回size
    int[] initCameraParam();

    int[] getPreviewWH();

    boolean isTorchSupported();

    void cancelAutoFocus();

    boolean currentValid();

    boolean setFocusAreas(View previewView, float[] pos, int rotation);

    List<int[]> getSupportedPreviewSizes();

    void setZoom(float scaleFactor);

    int getCameraPosition();

    boolean setVideoStabilization(boolean toggle);

    //检测video稳定性
    boolean isVideoStabilizationSupported();

    int getOrientation();

    boolean isFlipHorizontal();

}
