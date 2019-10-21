package io.agora.rtcwithbyte.activities;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.io.File;
import java.util.Arrays;

import io.agora.kit.media.constant.Constant;
import io.agora.rtc.mediaio.AgoraTextureView;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.video.VideoEncoderConfiguration; // 2.3.0 and later
import io.agora.rtcwithbyte.Constants;
import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.RtcEngineEventHandler;
import io.agora.kit.media.VideoManager;
import io.agora.kit.media.capture.VideoCaptureFrame;
import io.agora.kit.media.connector.SinkConnector;
import io.agora.rtcwithbyte.fragment.EffectFragment;
import io.agora.rtcwithbyte.fragment.StickerFragment;
import io.agora.rtcwithbyte.model.ComposerNode;
import io.agora.rtcwithbyte.renderer.EffectRenderHelper;
import io.agora.rtcwithbyte.utils.BitmapUtils;
import library.LogUtils;

/**
 * This activity demonstrates how to make BytedEffect and Agora RTC SDK work together
 * <p>
 * The activity which possesses remote video chatting ability.
 */
@SuppressWarnings("deprecation")
public class ByteChatActivity extends ByteBaseActivity implements RtcEngineEventHandler {

    private final static String TAG = ByteChatActivity.class.getSimpleName();

    public static final String TAG_EFFECT = "effect";
    public static final String TAG_STICKER = "sticker";

    private EffectFragment mEffectFragment;
    private StickerFragment mStickerFragment;
    private OnCloseListener mWorkingFragment;
    private EffectRenderHelper mEffectRenderHelper;
    private GLSurfaceView mGLSurfaceViewLocal;

    private FrameLayout mLocalViewContainer;
    private AgoraTextureView mRemoteView;
    private boolean mLocalViewIsBig = true;
    private float x_position;
    private float y_position;

    private TextView mDescriptionText;

    private View mEffectSwitch;

    private int showNum = 0;

    private int mSmallHeight;
    private int mSmallWidth;

    private VideoManager mVideoManager;

    private int mImageWidth;
    private int mImageHeight;

    private volatile boolean initGL = false;
    private boolean mIsFrontCamera = true;
    private SinkConnector<VideoCaptureFrame> mEffectHandler = new SinkConnector<VideoCaptureFrame>() {
        @Override
        public int onDataAvailable(VideoCaptureFrame data) {

            mImageWidth =  data.mFormat.getWidth();
            mImageHeight = data.mFormat.getHeight();

            if (!initGL) {
                mEffectRenderHelper.initEffectSDK(mImageWidth, mImageHeight);
                initGL = true;
            }


            int tex = mEffectRenderHelper.processTexure(
                    data.mTextureId,
                    BytedEffectConstants.TextureFormat.Texture_Oes,
                    mImageWidth, mImageHeight,
                    270, mIsFrontCamera,
                    BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0,
                    data.mSurfaceTexture.getTimestamp());

            //debug purpose
            Bitmap bm = BitmapUtils.bitmapFromGLTexture(tex, mImageWidth, mImageHeight, true);

            return tex;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEffectRenderHelper = new EffectRenderHelper(this);
//        mEffectRenderHelper.setMainActivity(this);
        initUIAndEvent();
    }

    protected void initUIAndEvent() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d(TAG, "width: " + width + ", height: " + height);
        mSmallHeight = height / 3;
        mSmallWidth = width / 3;
        x_position = width - mSmallWidth - convert(16);
        y_position = convert(70);

        mDescriptionText = findViewById(R.id.effect_desc_text);
        mEffectSwitch = findViewById(R.id.ll_feature);

        mGLSurfaceViewLocal = new GLSurfaceView(this);

        mLocalViewContainer = findViewById(R.id.local_video_view_container);
        if (mLocalViewContainer.getChildCount() > 0) {
            mLocalViewContainer.removeAllViews();
        }
        mLocalViewContainer.addView(mGLSurfaceViewLocal,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        mVideoManager = VideoManager.createInstance(this);

        mVideoManager.allocate(1920, 1080, 30, mIsFrontCamera ? Constant.CAMERA_FACING_FRONT : Constant.CAMERA_FACING_BACK);
        mVideoManager.setRenderView(mGLSurfaceViewLocal);
        mVideoManager.connectEffectHandler(mEffectHandler);
        mVideoManager.attachToRTCEngine(getWorker().getRtcEngine());

        mRemoteView = findViewById(R.id.remote_video_view);
        RelativeLayout.LayoutParams remoteParams = (RelativeLayout.LayoutParams) mRemoteView.getLayoutParams();
        remoteParams.height = mSmallHeight;
        remoteParams.width = mSmallWidth;
        remoteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        remoteParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        remoteParams.rightMargin = convert(16);
        remoteParams.topMargin = convert(70);
        mRemoteView.setLayoutParams(remoteParams);
        mRemoteView.setOnTouchListener(this);

        getEventHandler().addEventHandler(this);
        joinChannel();
    }

    private void joinChannel() {
        getWorker().configEngine(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER, new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24, 800,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));

        String roomName = getIntent().getStringExtra(Constants.ACTION_KEY_ROOM_NAME);
        getWorker().joinChannel(roomName, getConfig().mUid);
    }

    private void swapLocalRemoteDisplay() {
        if (mLocalViewIsBig) {
            RelativeLayout.LayoutParams localParams = (RelativeLayout.LayoutParams) mLocalViewContainer.getLayoutParams();
            localParams.height = mSmallHeight;
            localParams.width = mSmallWidth;
            localParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            localParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            localParams.rightMargin = convert(16);
            localParams.topMargin = convert(70);
            mLocalViewContainer.setLayoutParams(localParams);
            mLocalViewContainer.bringToFront();
            mLocalViewContainer.setOnTouchListener(this);

            RelativeLayout.LayoutParams remoteParams = (RelativeLayout.LayoutParams) mRemoteView.getLayoutParams();
            remoteParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            remoteParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            remoteParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            remoteParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            remoteParams.rightMargin = 0;
            remoteParams.topMargin = 0;
            mRemoteView.setLayoutParams(remoteParams);
            mRemoteView.setX(x_position);
            mRemoteView.setY(y_position);
            mRemoteView.getParent().requestLayout();
            mRemoteView.setOnTouchListener(null);
        } else {
            RelativeLayout.LayoutParams localParams = (RelativeLayout.LayoutParams) mLocalViewContainer.getLayoutParams();
            localParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            localParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            localParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            localParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            localParams.rightMargin = 0;
            localParams.topMargin = 0;
            mLocalViewContainer.setLayoutParams(localParams);
            mLocalViewContainer.setX(x_position);
            mLocalViewContainer.setY(y_position);
            mLocalViewContainer.getParent().requestLayout();
            mLocalViewContainer.setOnTouchListener(null);

            RelativeLayout.LayoutParams remoteParams = (RelativeLayout.LayoutParams) mRemoteView.getLayoutParams();
            remoteParams.height = mSmallHeight;
            remoteParams.width = mSmallWidth;
            remoteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            remoteParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            remoteParams.rightMargin = convert(16);
            remoteParams.topMargin = convert(70);
            mRemoteView.setLayoutParams(remoteParams);
            mRemoteView.bringToFront();
            mRemoteView.setOnTouchListener(this);
        }
        mLocalViewIsBig = !mLocalViewIsBig;
    }

    private int convert(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
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
        super.onDestroy();
        mVideoManager.stopCapture();
        mVideoManager.deallocate();

        mEffectRenderHelper.destroyEffectSDK();
        initGL = false;
    }

    @Override
    protected void deInitUIAndEvent() {
        getEventHandler().removeEventHandler(this);
        getWorker().leaveChannel(getConfig().mChannel);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onRemoteUserLeft();
            }
        });
    }

    private void onRemoteUserLeft() {
//        mRemoteUid = -1;
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupRemoteVideo(uid);
            }
        });
    }

    private void setupRemoteVideo(int uid) {
//        mRemoteUid = uid;
        mRemoteView.setBufferType(MediaIO.BufferType.BYTE_ARRAY);
        mRemoteView.setPixelFormat(MediaIO.PixelFormat.I420);
        getRtcEngine().setRemoteVideoRenderer(uid, mRemoteView);
        mRemoteView.setMirror(true);
    }

    protected void showDescription(int str, int time) {
        if (str == 0) {
            return;
        }
        mDescriptionText.removeCallbacks(effectDescriptionHide);
        mDescriptionText.setVisibility(View.VISIBLE);
        mDescriptionText.setText(str);
        mDescriptionText.postDelayed(effectDescriptionHide, time);
    }

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mDescriptionText.setText("");
            mDescriptionText.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onViewSwitchRequested() {
        swapLocalRemoteDisplay();
    }

    @Override
    protected void onMirrorPreviewRequested(boolean mirror) {
        Log.i(TAG, "onMirrorPreviewRequested " + mirror);

        mVideoManager.setMirrorMode(mirror);
    }

    @Override
    protected void onChangedToBroadcaster(boolean broadcaster) {
        Log.i(TAG, "onChangedToBroadcaster " + broadcaster);

        if (broadcaster) {
            mEffectRenderHelper = new EffectRenderHelper(this);
            getRtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);

            mGLSurfaceViewLocal = new GLSurfaceView(this);
            mLocalViewContainer.addView(mGLSurfaceViewLocal,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            mVideoManager.allocate(1920, 1080, 30, io.agora.kit.media.constant.Constant.CAMERA_FACING_FRONT);
            mVideoManager.setRenderView(mGLSurfaceViewLocal);
            mVideoManager.connectEffectHandler(mEffectHandler);
            mVideoManager.attachToRTCEngine(getWorker().getRtcEngine());
            mVideoManager.startCapture();

        } else {
            mVideoManager.stopCapture();

            mLocalViewContainer.removeAllViews();

            getRtcEngine().setClientRole(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);

            mVideoManager.deallocate();

            mEffectRenderHelper.destroyEffectSDK();

            initGL = false;

            System.gc();
        }

    }

    private Fragment generateFragment(String tag) {
        switch (tag) {
            case TAG_EFFECT:
                final EffectFragment effectFragment = new EffectFragment();
                effectFragment.setCallback(new EffectFragment.IEffectCallback() {

                    @Override
                    public void updateComposeNodes(final String[] nodes) {
                        LogUtils.e("update composer nodes: " + Arrays.toString(nodes));
                        if (nodes.length > 0) {
                            onFragmentWorking(mEffectFragment);
                        }
                        if (mGLSurfaceViewLocal != null) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.setComposeNodes(nodes);
                                }
                            });
                        }
                    }


                    @Override
                    public void updateComposeNodeIntensity(final ComposerNode node) {
                        LogUtils.e("update composer node intensity: node: " + node.getNode() + ", key: " + node.getKey() + ", value: " + node.getValue());
                        if (mGLSurfaceViewLocal != null && node.getKey() != null) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.updateComposeNode(node);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFilterSelected(final File file) {
                        if (null != mGLSurfaceViewLocal) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.setFilter(file != null ? file.getAbsolutePath() : "");

                                }
                            });
                        }
                        if (file != null) {
                            onFragmentWorking(mEffectFragment);
                        }
                    }


                    @Override
                    public void onFilterValueChanged(final float cur) {
                        if (null != mGLSurfaceViewLocal) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.updateIntensity(BytedEffectConstants.IntensityType.Filter, cur);
                                }
                            });
                        }
                    }

                    @Override
                    public void setEffectOn(final boolean isOn) {
                        if (mGLSurfaceViewLocal != null) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.setEffectOn(isOn);
                                }
                            });
                        }
                    }
                });
                mEffectFragment = effectFragment;
                return effectFragment;
            case TAG_STICKER:
                StickerFragment stickerFragment = new StickerFragment();
                stickerFragment.setCallback(new StickerFragment.IStickerCallback() {
                    @Override
                    public void onStickerSelected(final File file) {
                        if (file != null) {
                            onFragmentWorking(mStickerFragment);
                        }
                        if (null != mGLSurfaceViewLocal) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.setSticker(file != null ? file.getAbsolutePath() : "");
                                }
                            });
                        }
                    }
                });
                mStickerFragment = stickerFragment;
                return stickerFragment;
            default:
                return null;
        }
    }

    /**
     * 展示某一个 feature 面板
     * Show a feature panel
     * @param tag tag use to mark Fragment 用于标志 Fragment 的 tag {@value TAG_EFFECT}
     */
    private void showFeature(String tag) {
        if (mGLSurfaceViewLocal == null) return;
        if (mEffectRenderHelper == null) return;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {
            fragment = generateFragment(tag);
            ft.add(R.id.board_container, fragment, tag).commit();
        } else {
            ft.show(fragment).commit();
        }
        mWorkingFragment = (OnCloseListener) fragment;
//        showOrHideBoard(false);
        mEffectSwitch.setVisibility(View.GONE);
    }

    /**
     * 关闭所有的 feature 面板
     * close all feature panel
     * @return whether close panel successfully 是否成功关闭某个面板，即是否有面板正在开启中
     */
    private boolean closeFeature() {
        boolean hasFeature = false;

        Fragment showedFragment = null;
        if (mWorkingFragment != null && !((Fragment)mWorkingFragment).isHidden()) {
            showedFragment = (Fragment) mWorkingFragment;
            hasFeature = true;
        }

        if (hasFeature) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.board_enter, R.anim.board_exit)
                    .hide(showedFragment)
                    .commit();
        }

//        showOrHideBoard(true);
        mEffectSwitch.setVisibility(View.VISIBLE);
        return hasFeature;
    }

    /**
     * 当用户选择贴纸时，利用回调接口，关闭对应的开关
     * When the user selects the sticker
     * Use the callback interface to turn off the corresponding switch
     */
    private void onFragmentWorking(Fragment fragment) {
        if (fragment instanceof OnCloseListener) {
            if (fragment != mWorkingFragment) {
                if (mWorkingFragment != null) {
                    mWorkingFragment.onClose();
                }
                mWorkingFragment = (OnCloseListener) fragment;
            }
        } else {
            throw new IllegalArgumentException("fragment " + fragment + " must implement " + OnCloseListener.class);
        }
    }

    @Override
    protected void onChangeToEffectPanel(boolean show, String tag) {
        if (show) {
            showFeature(tag);
        }
        else {
            closeFeature();
        }
    }

    @Override
    protected void onCameraChangeRequested() {

        // TODO Reset options when camera changed
        mVideoManager.switchCamera();
        mIsFrontCamera = !mIsFrontCamera;
    }

    @Override
    protected void onStartRecordingRequested() {

    }

    @Override
    protected void onStopRecordingRequested() {
    }


    private Runnable mCalibratingRunnable = new Runnable() {
        @Override
        public void run() {
            showNum++;
            StringBuilder builder = new StringBuilder();
//            builder.append(getResources().getString(R.string.expression_calibrating));
            for (int i = 0; i < showNum; i++) {
                builder.append(".");
            }
            isCalibratingText.setText(builder);
            if (showNum < 6) {
                isCalibratingText.postDelayed(mCalibratingRunnable, 500);
            } else {
                isCalibratingText.setVisibility(View.INVISIBLE);
            }
        }
    };
}
