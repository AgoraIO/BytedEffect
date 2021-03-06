package io.agora.rtcwithbyte.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.rtc.RtcEngine;
import io.agora.rtcwithbyte.MyApplication;
import io.agora.rtcwithbyte.RtcEngineEventHandler;

public abstract class RtcBasedActivity extends AppCompatActivity implements RtcEngineEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addRtcHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeRtcHandler(this);
    }

    protected MyApplication application() {
        return (MyApplication) getApplication();
    }

    protected RtcEngine rtcEngine() {
        return application().rtcEngine();
    }

    protected final CameraVideoManager videoManager() {
        return application().videoManager();
    }

    private void addRtcHandler(RtcEngineEventHandler handler) {
        application().addRtcHandler(handler);
    }

    private void removeRtcHandler(RtcEngineEventHandler handler) {
        application().removeRtcHandler(handler);
    }
}
