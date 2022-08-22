package io.agora.rtcwithbyte.activities;

import static io.agora.rtcwithbyte.framework.ItemGetContract.NODE_ALL_SLIM;
import static io.agora.rtcwithbyte.framework.ItemGetContract.NODE_BEAUTY_LIVE;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_BEAUTY_BODY_LONG_LEG;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_BEAUTY_BODY_SHRINK_HEAD;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_BEAUTY_BODY_THIN;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_BEAUTY_FACE_SHARPEN;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_BEAUTY_FACE_SMOOTH;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_BEAUTY_FACE_WHITEN;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_MAKEUP_BLUSHER;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_MAKEUP_EYEBROW;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_MAKEUP_EYESHADOW;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_MAKEUP_LIP;
import static io.agora.rtcwithbyte.framework.ItemGetContract.TYPE_MAKEUP_PUPIL;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.byteddance.effect.ResourceHelper;
import com.byteddance.model.ComposerNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.framework.ExternParam;
import io.agora.rtcwithbyte.utils.UnzipTask;

public class ByteChatActivity extends RtcBasedActivity implements UnzipTask.IUnzipViewCallback {
    private static final String TAG = ByteChatActivity.class.getSimpleName();
    private static final int REQUEST = 1;
    private EffectOptionContainer mEffectContainer;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private TextureView mVideoSurface;
    private boolean mPermissionGranted;
    private int mRemoteUid = -1;
    private FrameLayout mRemoteViewContainer;

    private ExternParam externParam;
    private String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UnzipTask mTask = new UnzipTask(this);
        mTask.execute(ResourceHelper.RESOURCE);
        Intent intent =getIntent();
        channelName = intent.getStringExtra(io.agora.rtcwithbyte.utils.Constant.ACTION_KEY_ROOM_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtcEngine().leaveChannel();
        rtcEngine().stopPreview();
        preProcessor().disposeOnStopPreview();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        mVideoSurface = findViewById(R.id.local_video_surface);
        mEffectContainer = findViewById(R.id.effect_container);
        mEffectContainer.setEffectOptionItemListener(new EffectListener());
        externParam = new ExternParam();
        initRemoteViewLayout();
    }

    private void initRoom() {
        joinChannel();
    }

    private void joinChannel() {
        rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        rtcEngine().setClientRole(io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER);

        rtcEngine().joinChannel(null, channelName==null?"BytedDemoChannel":channelName, null, 0);
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
        rtcEngine().setupLocalVideo(new VideoCanvas(mVideoSurface));
        rtcEngine().startPreview();
        updateEffectOptionPanel();
    }

    public void onCameraChange(View view) {
        rtcEngine().switchCamera();
    }


    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.i(TAG, "onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL));
    }

    @Override
    public void onUserOffline(int uid, int reason) {
//        Log.i(TAG, "onUserJoined " + (uid & 0xFFFFFFFFL));
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.i(TAG, "onUserJoined " + (uid & 0xFFFFFFFFL));
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onStartTask() {

    }

    @Override
    public void onEndTask(boolean result) {
        initUI();
        checkCameraPermission();
        initRoom();
    }

    private class EffectListener implements EffectOptionContainer.OnEffectOptionContainerItemClickListener {
        @Override
        public void onEffectOptionItemClicked(int index, int textResource, boolean selected) {
            Log.i(TAG, "onEffectOptionItemClicked " + index + " " + selected);
            if (preProcessor().initialized()) {
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
        List<ComposerNode> list = new ArrayList<>();
        if(!selected){
            list.add(new ComposerNode(TYPE_BEAUTY_BODY_THIN, NODE_ALL_SLIM, "BEF_BEAUTY_BODY_THIN", 0));
            list.add(new ComposerNode(TYPE_BEAUTY_BODY_LONG_LEG, NODE_ALL_SLIM, "BEF_BEAUTY_BODY_LONG_LEG", 0));
            list.add(new ComposerNode(TYPE_BEAUTY_BODY_SHRINK_HEAD, NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SHRINK_HEAD", 0));
        }
        else{
            if(externParam.getNodes()!=null)
                list.addAll(Arrays.asList(externParam.getNodes()));
            list.add(new ComposerNode(TYPE_BEAUTY_BODY_THIN, NODE_ALL_SLIM, "BEF_BEAUTY_BODY_THIN", 1));
            list.add(new ComposerNode(TYPE_BEAUTY_BODY_LONG_LEG, NODE_ALL_SLIM, "BEF_BEAUTY_BODY_LONG_LEG", 1));
            list.add(new ComposerNode(TYPE_BEAUTY_BODY_SHRINK_HEAD, NODE_ALL_SLIM, "BEF_BEAUTY_BODY_SHRINK_HEAD", 1));
        }
        externParam.setNodes(list.toArray(new ComposerNode[list.size()]));
        updateEffectsByParam();
    }

    private void setStickerItem(boolean selected) {
        if(!selected){
            externParam.setSticker(null);
        }
        else {
            externParam.setSticker(ResourceHelper.getStickerPath(getApplicationContext(), "zhutouzhuer"));
        }
        updateEffectsByParam();
    }

    private void setBeautificationOn(boolean selected) {
        List<ComposerNode> list = new ArrayList<>();
        if(!selected){
            list.add(new ComposerNode(TYPE_BEAUTY_FACE_SMOOTH, NODE_BEAUTY_LIVE, "smooth", 0));
            list.add(new ComposerNode(TYPE_BEAUTY_FACE_WHITEN, NODE_BEAUTY_LIVE, "whiten", 0));
            list.add(new ComposerNode(TYPE_BEAUTY_FACE_SHARPEN, NODE_BEAUTY_LIVE, "sharp", 0));
            externParam.setFilter(null);
        }
        else{
            if(externParam.getNodes()!=null)
                list.addAll(Arrays.asList(externParam.getNodes()));
            list.add(new ComposerNode(TYPE_BEAUTY_FACE_SMOOTH, NODE_BEAUTY_LIVE, "smooth", 1));
            list.add(new ComposerNode(TYPE_BEAUTY_FACE_WHITEN, NODE_BEAUTY_LIVE, "whiten", 1));
            list.add(new ComposerNode(TYPE_BEAUTY_FACE_SHARPEN, NODE_BEAUTY_LIVE, "sharp", 1));
            ExternParam.FilterItem filterItem = new ExternParam.FilterItem();
            filterItem.setKey("Filter_01_38");
            filterItem.setValue(0.6f);
            externParam.setFilter(filterItem);
        }
        externParam.setNodes(list.toArray(new ComposerNode[list.size()]));
        updateEffectsByParam();
    }

    private void setMakeupItemParam(boolean selected) {
        List<ComposerNode> list = new ArrayList<>();
        if(!selected){
            list.add(new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/richang", "Internal_Makeup_Blusher", 0));
            list.add(new ComposerNode(TYPE_MAKEUP_LIP, "lip/sironghong", "Internal_Makeup_Lips", 0));
            list.add(new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/hunxuezong", "Internal_Makeup_Pupil", 0));
            list.add(new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/dadizong", "Internal_Makeup_Eye", 0));
            list.add(new ComposerNode(TYPE_MAKEUP_EYEBROW, "eyebrow/BK01", "Internal_Makeup_Brow", 0));
        }
        else{
            if(externParam.getNodes()!=null)
                list.addAll(Arrays.asList(externParam.getNodes()));
            list.add(new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/richang", "Internal_Makeup_Blusher", 1));
            list.add(new ComposerNode(TYPE_MAKEUP_LIP, "lip/sironghong", "Internal_Makeup_Lips", 1));
            list.add(new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/hunxuezong", "Internal_Makeup_Pupil", 1));
            list.add(new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/dadizong", "Internal_Makeup_Eye", 1));
            list.add(new ComposerNode(TYPE_MAKEUP_EYEBROW, "eyebrow/BK01", "Internal_Makeup_Brow", 1));
        }
        externParam.setNodes(list.toArray(new ComposerNode[list.size()]));
        updateEffectsByParam();
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
        if (mRemoteUid == -1 && state == Constants.REMOTE_VIDEO_STATE_PLAYING) {
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

    private void updateEffectsByParam() {
        if(externParam != null ){
            if(externParam.getNodeArray()!=null && externParam.getNodeArray().length > 0){
                preProcessor().setComposeNodes(externParam.getNodeArray());
                for(ComposerNode node : externParam.getNodes()){
                    preProcessor().updateComposeNode(node, true);
                }
            }
            preProcessor().setSticker(externParam.getSticker());
            if (null != externParam.getFilter() && !TextUtils.isEmpty(externParam.getFilter().getKey())) {
                preProcessor().setFilter(ResourceHelper.getFilterResourcePathByName(getContext(), externParam.getFilter().getKey()));
                preProcessor().updateFilterIntensity(externParam.getFilter().getValue());
            }
        }
    }
}
