package io.agora.rtcwithbyte.activities;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.HashMap;

import io.agora.rtcwithbyte.R;

/**
 * Abstract activity which has Byte UI, waiting sub activities
 * to implements how to deal with Byte rendering parameters.
 */
public abstract class ByteBaseActivity extends RTCBaseActivity
        implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = "ByteBaseActivity";

    private int mRecordStatus = 0;

    private int mBroadcastingStatus = 1;

    private int mEffectPanelStatus = 0;

    private int mMirrorVideoPreviewStatus = 0;

    protected TextView isCalibratingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getWindow().setAttributes(params);
    }

    private HashMap<View, int[]> mTouchPointMap = new HashMap<>();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int last_X = (int) event.getRawX();
                int last_Y = (int) event.getRawY();
                mTouchPointMap.put(v, new int[]{last_X, last_Y});
                break;
            case MotionEvent.ACTION_MOVE:
                int[] lastPoint = mTouchPointMap.get(v);
                if (lastPoint != null) {
                    int dx = (int) event.getRawX() - lastPoint[0];
                    int dy = (int) event.getRawY() - lastPoint[1];

                    int left = (int) v.getX() + dx;
                    int top = (int) v.getY() + dy;
                    v.setX(left);
                    v.setY(top);
                    lastPoint[0] = (int) event.getRawX();
                    lastPoint[1] = (int) event.getRawY();

                    mTouchPointMap.put(v, lastPoint);
                    v.getParent().requestLayout();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_camera:
                onCameraChangeRequested();
                break;
            case R.id.btn_recording:
                mRecordStatus ^= 1;
                if (mRecordStatus == 0) {
//                    ((Button) v).setText(R.string.btn_start_recording);
                    onStopRecordingRequested();
                } else {
//                    ((Button) v).setText(R.string.btn_stop_recording);
                    onStartRecordingRequested();
                }
                break;
            case R.id.btn_switch_view:
                onViewSwitchRequested();
                break;
            case R.id.btn_mirror_video_preview:
                mMirrorVideoPreviewStatus ^= 1;
                onMirrorPreviewRequested(mMirrorVideoPreviewStatus > 0);
                break;
            case R.id.btn_switch_client_role:
                mBroadcastingStatus ^= 1;
                onChangedToBroadcaster(mBroadcastingStatus > 0);
                if (mBroadcastingStatus > 0) {
//                    ((Button) v).setText(R.string.btn_switch_client_role_audience);
                } else {
//                    ((Button) v).setText(R.string.btn_switch_client_role_broadcaster);
                }
                break;
            case R.id.ll_effect:
                mEffectPanelStatus ^= 1;
                onChangeToEffectPanel(mEffectPanelStatus > 0, "effect");
                break;
            case R.id.ll_sticker:
                mEffectPanelStatus ^= 1;
                onChangeToEffectPanel(mEffectPanelStatus > 0, "sticker");
                break;

            default:
                if (mEffectPanelStatus == 1) {
                    mEffectPanelStatus = 0;
                    onChangeToEffectPanel(false, "");
                }
        }
    }

    abstract protected void onCameraChangeRequested();

    abstract protected void onViewSwitchRequested();

    abstract protected void onMirrorPreviewRequested(boolean mirror);

    abstract protected void onChangedToBroadcaster(boolean broadcaster);

    abstract protected void onStartRecordingRequested();

    abstract protected void onStopRecordingRequested();

    abstract protected void onChangeToEffectPanel(boolean show, String tag);


    /**
     * 定义一个回调接口，用于当用户选择其中一个面板时，
     * 关闭其他面板的回调，此接口由各 Fragment 实现，
     * 在 onClose() 方法中要完成各 Fragment 中 UI 的初始化，
     * 即关闭用户已经开启的开关
     *
     * Define a callback interface for when a user selects one of the panels，
     * close the callback of the other panel, which is implemented by each Fragment
     * In the onClose() method, initialize the UI of each Fragment:
     * turn off the switch that the user has already turned on
     */
    public interface OnCloseListener {
        void onClose();
    }

    public interface ICheckAvailableCallback {
        boolean checkAvailable(int id);
    }
}
