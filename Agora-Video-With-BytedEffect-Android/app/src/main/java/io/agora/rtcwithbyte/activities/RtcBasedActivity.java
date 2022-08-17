package io.agora.rtcwithbyte.activities;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc2.RtcEngine;
import io.agora.rtcwithbyte.MyApplication;
import io.agora.rtcwithbyte.RtcEngineEventHandler;
import io.agora.rtcwithbyte.framework.PreprocessorByteDance;

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

    protected final PreprocessorByteDance preProcessor() {
        return application().preProcessor();
    }

    private void addRtcHandler(RtcEngineEventHandler handler) {
        application().addRtcHandler(handler);
    }

    private void removeRtcHandler(RtcEngineEventHandler handler) {
        application().removeRtcHandler(handler);
    }
}
