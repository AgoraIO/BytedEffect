package io.agora.rtcwithbyte.core;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import io.agora.rtcwithbyte.camera.CameraListener;
import io.agora.rtcwithbyte.camera.CameraProxy;
import io.agora.rtcwithbyte.opengl.GlUtil;
import io.agora.rtcwithbyte.utils.AppUtils;
import io.agora.rtcwithbyte.utils.FrameRator;
import library.LogUtils;
import library.OrientationSensor;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRenderView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private volatile boolean mCameraChanging = false;

    private volatile boolean mIsPaused = false;
    private EffectRenderHelper mEffectRenderHelper;

    private FrameRator mFrameRator;

    private Context mContext;

    //cameraId（前后）
    private int mCameraID = android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;


    private int dstTexture = GlUtil.NO_TEXTURE;

    private CameraProxy mCameraProxy;

    public CameraRenderView(Context context) {
        super(context);
        init(context);
    }

    public CameraRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.d("onSurfaceCreated: ");
        GLES20.glEnable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        LogUtils.d("previewSize =" + mCameraProxy.getPreviewWidth() + "   " + mCameraProxy.getPreviewHeight());
        if (mCameraProxy.getOrientation() % 180 == 90) {
            mEffectRenderHelper.initEffectSDK(mCameraProxy.getPreviewHeight(), mCameraProxy.getPreviewWidth());
        } else {
            mEffectRenderHelper.initEffectSDK(mCameraProxy.getPreviewWidth(), mCameraProxy.getPreviewHeight());
        }
        mEffectRenderHelper.recoverStatus();
        mFrameRator.start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mIsPaused) {
            return;
        }
        mEffectRenderHelper.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mCameraChanging || mIsPaused) {
            return;
        }
        //清空缓冲区颜色
        //Clear buffer color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (!mCameraProxy.isCameraValid()) return;
        mCameraProxy.updateTexture();

        BytedEffectConstants.Rotation rotation = OrientationSensor.getOrientation();
        // tv sensor get an 270 ……
        if (AppUtils.isTv(mContext)) {
            rotation = BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0;
        }
        dstTexture = mEffectRenderHelper.processTexure(mCameraProxy.getPreviewTexture(), BytedEffectConstants.TextureFormat.Texture_Oes, mCameraProxy.getPreviewWidth(), mCameraProxy.getPreviewHeight(), mCameraProxy.getOrientation(), mCameraProxy.isFrontCamera(), rotation, mCameraProxy.getTimeStamp());
        if (dstTexture != GlUtil.NO_TEXTURE) {
            mEffectRenderHelper.drawFrame(dstTexture, BytedEffectConstants.TextureFormat.Texure2D, mCameraProxy.getPreviewWidth(), mCameraProxy.getPreviewHeight(), 360 - mCameraProxy.getOrientation(), mCameraProxy.isFrontCamera(), false);

        }

        mFrameRator.addFrameStamp();
    }

    @Override
    public void onResume() {
        LogUtils.d("onResume");
        mIsPaused = false;
        if (!mCameraProxy.isCameraValid()) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (!mCameraProxy.isCameraValid()) {
                        mCameraProxy.openCamera(mCameraID, new CameraListener() {
                            @Override
                            public void onOpenSuccess() {
                                queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        LogUtils.d("onOpenSuccess");
                                        onCameraOpen();

                                    }
                                });
                            }

                            @Override
                            public void onOpenFail() {

                            }
                        });
                    }
                }
            });
        }

        super.onResume();
    }


    @Override
    public void onPause() {
        LogUtils.d("onPause");
        mIsPaused = true;
        mFrameRator.stop();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraProxy.releaseCamera();
                mCameraProxy.deleteTexture();
                mEffectRenderHelper.destroyEffectSDK();
            }
        });
        super.onPause();
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mCameraProxy = new CameraProxy(context);
        mEffectRenderHelper = new EffectRenderHelper(context);
        mFrameRator = new FrameRator();
        mContext = context;
    }

    /**
     * 相机打开成功时回调，初始化特效SDK
     * Initialize camera information (texture, etc.)
     */
    private void onCameraOpen() {
        LogUtils.d("CameraSurfaceView onCameraOpen");
        mCameraProxy.startPreview(CameraRenderView.this);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (!mCameraChanging) {
            requestRender();
        }
    }


    /**
     * 切换前后置相机
     */
    public void switchCamera() {
        LogUtils.d("switchCamera");

        if (Camera.getNumberOfCameras() == 1
                || mCameraChanging) {
            return;
        }
        mCameraID = 1 - mCameraID;
        mCameraChanging = true;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraProxy.changeCamera(mCameraID, new CameraListener() {
                    @Override
                    public void onOpenSuccess() {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.d("onOpenSuccess");
                                deleteCameraPreviewTexture();
                                onCameraOpen();
                                mCameraChanging = false;
                                requestRender();
                            }
                        });

                    }

                    @Override
                    public void onOpenFail() {
                        LogUtils.e("camera openFail!!");


                    }
                });
            }
        });


    }

    /**
     * 删除camera的纹理
     */
    private void deleteCameraPreviewTexture() {
        mCameraProxy.deleteTexture();

    }

    public EffectRenderHelper getEffectRenderHelper() {
        return mEffectRenderHelper;
    }

    public int getFrameRate() {
        return mFrameRator.getFrameRate();
    }


}
