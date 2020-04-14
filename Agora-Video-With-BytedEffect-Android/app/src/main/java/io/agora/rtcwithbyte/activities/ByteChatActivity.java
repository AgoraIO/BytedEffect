package io.agora.rtcwithbyte.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import io.agora.kit.media.constant.Constant;
import io.agora.rtc.mediaio.AgoraTextureView;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.video.VideoEncoderConfiguration; // 2.3.0 and later
import io.agora.rtcwithbyte.Constants;
import io.agora.rtcwithbyte.PermissionsActivity;
import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.RtcEngineEventHandler;
import io.agora.kit.media.VideoManager;
import io.agora.kit.media.capture.VideoCaptureFrame;
import io.agora.kit.media.connector.SinkConnector;
import io.agora.rtcwithbyte.core.EffectRenderHelper;
import io.agora.rtcwithbyte.fragment.EffectFragment;
import io.agora.rtcwithbyte.fragment.StickerFragment;
import io.agora.rtcwithbyte.model.CaptureResult;
import io.agora.rtcwithbyte.model.ComposerNode;
import io.agora.rtcwithbyte.utils.BitmapUtils;
import io.agora.rtcwithbyte.utils.CommonUtils;
import io.agora.rtcwithbyte.utils.Config;
import io.agora.rtcwithbyte.utils.ToasUtils;
import io.agora.rtcwithbyte.utils.UserData;
import library.LogUtils;

import static io.agora.rtcwithbyte.contract.StickerContract.TYPE_ANIMOJI;
import static io.agora.rtcwithbyte.contract.StickerContract.TYPE_STICKER;

/**
 * This activity demonstrates how to make BytedEffect and Agora RTC SDK work together
 * <p>
 * The activity which possesses remote video chatting ability.
 */
@SuppressWarnings("deprecation")
public class ByteChatActivity extends ByteBaseActivity implements RtcEngineEventHandler, io.agora.rtcwithbyte.core.EffectRenderHelper.OnEffectListener, View.OnClickListener {

    private final static String TAG = ByteChatActivity.class.getSimpleName();
    public static final String TAG_EFFECT = "effect";
    public static final String TAG_STICKER = "sticker";
    public static final String TAG_ANIMOJI = "animoji";

    public static final int ANIMATOR_DURATION = 400;

    private EffectFragment mEffectFragment;
    private StickerFragment mStickerFragment;
    private StickerFragment mAnimojiFragment;
    // 正在处于功能可用状态的面板
    // current panel
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
        mEffectRenderHelper.setOnEffectListener(this);
//        mEffectRenderHelper.setMainActivity(this);
        initUIAndEvent();
        initViews();
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


    @Override
    protected void onChangeToEffectPanel(boolean show, String tag) {
        if (show) {
            showFeature(tag, true);
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

    // byted


    private TextView mFpsTextView;

//    private CameraRenderView mSurfaceView;
//    private EffectRenderHelper effectRenderHelper;

//    private FrameLayout mSurfaceContainer;

    private View rootView;

    private LinearLayout llFeature;
    private LinearLayout llEffect;
    private LinearLayout llSticker;
    private LinearLayout llAnimoji;

    //  below UI elements are for debug
//    public StringBuilder cameraInfo;
//    public TextView tvInfo;

//    public TextView tvcameraInfo;

//    public ImageView mImageView;
//    private VideoButton vbTakePic;

//    private SwitchCompat scExclusive;

    private boolean mFirstEnter = true;
    private String mSavedStickerPath;
    private String mSavedAnimojiPath;
    private ICheckAvailableCallback mCheckAvailableCallback = new ICheckAvailableCallback() {
        @Override
        public boolean checkAvailable(int id) {
            if (mSavedAnimojiPath != null && !mSavedAnimojiPath.equals("")) {
                ToasUtils.show(getString(R.string.tip_close_animoji_first));
                return false;
            }
            if (isExclusive() && id != TYPE_STICKER && mSavedStickerPath != null && !mSavedStickerPath.equals("")) {
                ToasUtils.show(getString(R.string.tip_close_sticker_first));
                return false;
            }
            return true;
        }
    };


    private static final int UPDATE_INFO = 1;
    // 拍照失败
    private static final int CAPTURE_FAIL = 9;
    // 拍照成功
    private static final int CAPTURE_SUCCESS = 10;



    private static final int UPDATE_INFO_INTERVAL = 1000;



    private void initViews() {
        llFeature = findViewById(R.id.ll_feature);
        llEffect = findViewById(R.id.ll_effect);
        llSticker = findViewById(R.id.ll_sticker);
        llAnimoji = findViewById(R.id.ll_animoji);
//        effectRenderHelper = mSurfaceView.getEffectRenderHelper();
//        effectRenderHelper.setOnEffectListener(this);

//        vbTakePic.setOnClickListener(this);
        llEffect.setOnClickListener(this);
        llSticker.setOnClickListener(this);
        llAnimoji.setOnClickListener(this);
//        scExclusive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked == UserData.getExclusive(getApplicationContext(), false)) {
//                    return;
//                }
//                UserData.setExclusive(getApplicationContext(), isChecked);
//                ToasUtils.show(getString(R.string.exclusive_tip));
//            }
//        });

//        rootView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                closeFeature(true);
//                return false;
//            }
//        });
    }





    /**
     * 根据 TAG 创建对应的 Fragment
     * Create the corresponding Fragment based on TAG
     * @param tag  tag
     * @return  Fragment
     */
    private Fragment generateFragment(String tag) {
        switch (tag) {
            case TAG_EFFECT:
                if (mEffectFragment != null) return mEffectFragment;

                final EffectFragment effectFragment = new EffectFragment();
                effectFragment.setCheckAvailableCallback(mCheckAvailableCallback)
                        .setCallback(new EffectFragment.IEffectCallback() {

                            @Override
                            public void updateComposeNodes(final String[] nodes) {
                                LogUtils.e("update composer nodes: " + Arrays.toString(nodes));
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
                                    mEffectRenderHelper.updateComposeNode(node, true);
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
                            }


                    @Override
                    public void onFilterValueChanged(final float cur) {
                        if (null != mGLSurfaceViewLocal) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.updateFilterIntensity(cur);
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

                            @Override
                            public void onDefaultClick() {
                                onFragmentWorking(mEffectFragment);
                            }
                        });
                mEffectFragment = effectFragment;
                return effectFragment;
            case TAG_STICKER:
                if (mStickerFragment != null) return mStickerFragment;

                StickerFragment stickerFragment = new StickerFragment()
                        .setCheckAvailableCallback(mCheckAvailableCallback)
                        .setType(TYPE_STICKER);
                stickerFragment.setCallback(new StickerFragment.IStickerCallback() {
                    @Override
                    public void onStickerSelected(final File file) {
                        mSavedStickerPath = file == null ? null : file.getAbsolutePath();
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
                        if (isExclusive() && file == null && mEffectFragment != null) {
                            mEffectFragment.recoverState();
                        }
                    }
                });
                mStickerFragment = stickerFragment;
                return stickerFragment;
            case TAG_ANIMOJI:
                if (mAnimojiFragment != null) return mAnimojiFragment;

                StickerFragment animojiFragment = new StickerFragment().setType(TYPE_ANIMOJI);
                animojiFragment.setCallback(new StickerFragment.IStickerCallback() {
                    @Override
                    public void onStickerSelected(final File file) {
                        mSavedAnimojiPath = file == null ? null : file.getAbsolutePath();
                        if (file != null) {
                            onFragmentWorking(mAnimojiFragment);
                        }
                        if (null != mGLSurfaceViewLocal) {
                            mGLSurfaceViewLocal.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    mEffectRenderHelper.setSticker(file != null ? file.getAbsolutePath() : "");
                                }
                            });
                        }
                        if (file == null) {
                            if (mStickerFragment != null && mSavedStickerPath != null && !mSavedStickerPath.equals("")) {
                                mStickerFragment.recoverState(mSavedStickerPath);
                            }
                            if ((!isExclusive() || mSavedStickerPath == null || mSavedStickerPath.equals(""))
                                    && mEffectFragment != null) {
                                mEffectFragment.recoverState();
                            }
                        }
//                        if (file == null && mEffectFragment != null) {
//                            mEffectFragment.recoverState();
//                        }
//                        if (file == null && mSavedStickerPath != null &&
//                                !mSavedStickerPath.equals("") && mStickerFragment != null) {
//                            mStickerFragment.recoverState(mSavedStickerPath);
//                        }
                    }
                });
                mAnimojiFragment = animojiFragment;
                return animojiFragment;
            default:
                return null;
        }
    }

    /**
     * 展示某一个 feature 面板
     * Show a feature panel
     * @param tag tag use to mark Fragment 用于标志 Fragment 的 tag
     */
    private void showFeature(String tag, boolean hideBoard) {
        if (mGLSurfaceViewLocal == null) return;
        if (mEffectRenderHelper == null) return;

        if (showingFragment() != null) {
            getSupportFragmentManager().beginTransaction().hide(showingFragment()).commit();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (hideBoard) {
            ft.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
        }
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {
            fragment = generateFragment(tag);
            ft.add(R.id.board_container, fragment, tag).commit();
        } else {
            ft.show(fragment).commit();
        }
        if (hideBoard) {
            showOrHideBoard(false);
        }
    }

    /**
     * 关闭所有的 feature 面板
     * close all feature panel
     * @return whether close panel successfully 是否成功关闭某个面板，即是否有面板正在开启中
     */
    private boolean closeFeature(boolean animation) {
        Fragment showingFragment = showingFragment();
        if (showingFragment != null) {
            FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
            if (animation) {
                ft.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
            }
            ft.hide(showingFragment).commit();
        }

        showOrHideBoard(true);
        return showingFragment != null;
    }

    private Fragment showingFragment() {
        if (mEffectFragment != null && !mEffectFragment.isHidden()) {
            return mEffectFragment;
        } else if (mStickerFragment != null && !mStickerFragment.isHidden()) {
            return mStickerFragment;
        } else if (mAnimojiFragment != null && !mAnimojiFragment.isHidden()) {
            return mAnimojiFragment;
        }
        return null;
    }

    /**
     * 展示或关闭菜单面板
     * show board
     * @param show 展示
     */
    private void showOrHideBoard(boolean show) {
        if (show) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (showingFragment() == null) {
//                        vbTakePic.setVisibility(View.VISIBLE);
                        llFeature.setVisibility(View.VISIBLE);
                    }
                }
            }, ANIMATOR_DURATION);
        } else {
//            vbTakePic.setVisibility(View.GONE);
            llFeature.setVisibility(View.GONE);
        }
    }




//    @Override
//    protected void onPause() {
//        super.onPause();
//        // release device
//        mHandler.removeCallbacksAndMessages(null);
//        mSurfaceView.onPause();
//    }


//    @Override
//    protected void onDestroy() {
//        OrientationSensor.stop();
//        mSurfaceContainer.removeAllViews();
//        super.onDestroy();
//        mSurfaceView = null;
//        mEffectFragment = null;
//        mStickerFragment = null;
//
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // setup device
//        mSurfaceView.onResume();
//        mHandler.sendEmptyMessageDelayed(UPDATE_INFO, UPDATE_INFO_INTERVAL);
//    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Config.PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Config.PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Config.PERMISSION_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // start Permissions activity
                Intent intent = new Intent(this, PermissionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isFastClick()) {
            ToasUtils.show("too fast click");
            return;
        }
        switch (v.getId()) {
//            case R.id.iv_change_camera:
//                switchCamera();
//                break;
//            case R.id.btn_take_pic:
//                takePic();
//                break;
            case R.id.ll_effect:
                showFeature(TAG_EFFECT, true);
                break;
            case R.id.ll_sticker:
                showFeature(TAG_STICKER, true);
                break;
            case R.id.ll_animoji:
                showFeature(TAG_ANIMOJI, true);
                break;
        }
    }



    @Override
    public void onBackPressed() {
        if (closeFeature(true)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        finish();
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public InnerHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_INFO:
//                        activity.mFpsTextView.setText("" + activity.mSurfaceView.getFrameRate());
                        sendEmptyMessageDelayed(UPDATE_INFO, UPDATE_INFO_INTERVAL);
                        break;
                    case CAPTURE_SUCCESS:
                        CaptureResult captureResult = (CaptureResult) msg.obj;
                        SavePicTask task  = new SavePicTask(mActivity.get());
                        task.execute(captureResult);

                        break;


                }
            }
        }

    }

    static class SavePicTask extends AsyncTask<CaptureResult, Void,String> {
        private WeakReference<Context> mContext;

        public SavePicTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(CaptureResult... captureResults) {
            if (captureResults.length == 0) return "captureResult arrayLength is 0";
            Bitmap bitmap = Bitmap.createBitmap(captureResults[0].getWidth(), captureResults[0].getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(captureResults[0].getByteBuffer().position(0));
            File file = BitmapUtils.saveToLocal(bitmap);
            if (file.exists()){
                return file.getAbsolutePath();
            }else{
                return "";
            }
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (TextUtils.isEmpty(path)){
                ToasUtils.show("图片保存失败");
                return;
            }
            if (mContext.get() == null) {
                try {
                    new File(path).delete();
                } catch (Exception ignored) {
                }
                ToasUtils.show("图片保存失败");
            }
            try{
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
                mContext.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }catch (Exception e){
                e.printStackTrace();
            }
            ToasUtils.show("保存成功，路径："+path);
        }
    }

    /**
     * 当用户选择贴纸时，利用回调接口，关闭对应的开关
     * When the user selects the sticker
     * Use the callback interface to turn off the corresponding switch
     */
    private void onFragmentWorking(Fragment fragment) {
        if (fragment == mEffectFragment) {
            if (mStickerFragment != null) {
                if (mSavedStickerPath != null) {
                    mStickerFragment.onClose();
                    mSavedStickerPath = null;
                    mGLSurfaceViewLocal.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mEffectRenderHelper.setSticker(null);
                        }
                    });
                }
            }
            if (mAnimojiFragment != null) {
                if (mSavedAnimojiPath != null) {
                    mAnimojiFragment.onClose();
                    mSavedAnimojiPath = null;
                    mGLSurfaceViewLocal.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mEffectRenderHelper.setSticker(null);
                        }
                    });
                }
            }
        } else if (fragment == mStickerFragment) {
            if (isExclusive() && mEffectFragment != null) {
                mEffectFragment.onClose();
            }
        } else if (fragment == mAnimojiFragment) {
            if (mEffectFragment != null) {
                mEffectFragment.onClose();
            }
            if (mStickerFragment != null) {
                mStickerFragment.onClose();
            }
        }
//        if (fragment == mAnimojiFragment) {
//            if (mEffectFragment != null) {
//                mEffectFragment.onClose();
//            }
//            if (mStickerFragment != null) {
//                mStickerFragment.onClose();
//            }
//            return;
//        }
//        if (!isExclusive()) {
//            return;
//        }
//        if (fragment instanceof OnCloseListener) {
//            if (fragment != mWorkingFragment) {
//                // 开启贴纸会关闭美颜，反之不生效
//                if (mWorkingFragment != null) {
//                    mWorkingFragment.onClose();
//                }
//                mWorkingFragment = (OnCloseListener) fragment;
//            }
//        } else {
//            throw new IllegalArgumentException("fragment " + fragment + " must implement " + OnCloseListener.class);
//        }
    }

    @Override
    public void onEffectInitialized() {
        if (!mFirstEnter) {
            return;
        }
        mFirstEnter = false;
        final boolean exclusive = UserData.getExclusive(getApplicationContext(), false);
        mEffectRenderHelper.setComposerMode(exclusive ? 0 : 1);
        final String[] features = new String[30];
        mEffectRenderHelper.getAvailableFeatures(features);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                scExclusive.setChecked(exclusive);
                showFeature(TAG_EFFECT, false);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mEffectFragment != null) {
                            mEffectFragment.onDefaultClick();
                        }
                        closeFeature(false);
                    }
                });
                for (String feature : features) {
                    if (feature != null && feature.equals("3DStickerV3")) {
                        llAnimoji.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                llFeature.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isExclusive() {
        return mEffectRenderHelper.getComposerMode() == 0;
    }
}
