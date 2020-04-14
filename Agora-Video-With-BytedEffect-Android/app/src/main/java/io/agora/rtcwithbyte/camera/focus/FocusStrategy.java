package io.agora.rtcwithbyte.camera.focus;

import android.hardware.Camera;
import android.support.annotation.NonNull;

public interface FocusStrategy {
    void focusCamera(@NonNull Camera camera, @NonNull Camera.Parameters parameters);
}