package io.agora.rtcwithbyte.camera.focus;

import android.os.Build;

/**
 * Created by yangcihang on 2018/2/22.
 */

public final class FocusStrategyFactory {
    private FocusStrategyFactory() {
    }

    public static FocusStrategy getFocusStrategy() {
        if (Build.MANUFACTURER != null
                && (Build.MANUFACTURER.compareToIgnoreCase("Samsung") == 0
                || Build.MANUFACTURER.compareToIgnoreCase("meizu") == 0)) {
            return new AutoFocusStrategy();
        }
        return new MacroFocusStrategy();
    }
}
