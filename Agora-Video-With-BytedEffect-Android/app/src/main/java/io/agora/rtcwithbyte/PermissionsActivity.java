// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package io.agora.rtcwithbyte;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import io.agora.rtcwithbyte.activities.MainActivity;
import io.agora.rtcwithbyte.utils.Config;

public class PermissionsActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkCameraPermission();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (null == grantResults || grantResults.length < 1) return;
        if (requestCode == Config.PERMISSION_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkStoragePermission();
            } else {
                Toast.makeText(this, "Camera权限被拒绝", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == Config.PERMISSION_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkMicrophonePermission();
            } else {
                Toast.makeText(this, "存储卡读写权限被拒绝", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == Config.PERMISSION_CODE_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMainActivity();
            } else {
                Toast.makeText(this, "麦克风权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Config.PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, Config.PERMISSION_CODE_CAMERA);
            } else {
                checkStoragePermission();
            }
        } else {
            startMainActivity();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Config.PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Config.PERMISSION_STORAGE}, Config.PERMISSION_CODE_STORAGE);
            } else {
                checkMicrophonePermission();
            }
        }

    }

    private void checkMicrophonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, Config.PERMISSION_CODE_AUDIO);
            }
        }
    }
}
