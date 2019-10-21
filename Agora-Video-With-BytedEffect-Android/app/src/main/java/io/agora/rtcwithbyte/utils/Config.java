package io.agora.rtcwithbyte.utils;

import android.Manifest;

public class Config {
    // permissions
    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    // permission code
    public static final int PERMISSION_CODE_STORAGE = 1;
    public static final int PERMISSION_CODE_CAMERA = 2;
    public static final int PERMISSION_CODE_AUDIO = 3;
}
