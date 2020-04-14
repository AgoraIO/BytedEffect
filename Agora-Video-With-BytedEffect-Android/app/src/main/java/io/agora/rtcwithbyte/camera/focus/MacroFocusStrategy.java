package io.agora.rtcwithbyte.camera.focus;

import android.hardware.Camera;
import android.support.annotation.NonNull;

/**
 * Created by yangcihang on 2018/2/22.
 */
class MacroFocusStrategy implements FocusStrategy {
    private static final String TAG = "FocusStrategy";

    @Override
    public void focusCamera(@NonNull Camera camera, @NonNull Camera.Parameters parameters) {
        try {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            camera.setParameters(parameters);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // https://fabric.io/bytedance/android/apps/com.ss.android.ugc.aweme/issues/5966f6a4be077a4dcc78dc51?time=last-ninety-days
                    try {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        // ignore
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
