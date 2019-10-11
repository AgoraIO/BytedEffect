package io.agora.rtcwithbyte.task.decode;


import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import com.google.zxing.Result;

import library.LogUtils;

/**
 * qrcode decode handler
 */
public class RepeatedScannerHandler extends Handler {

    private RepeatedScannerCallback mCallback;
    private Messenger mMessenger;
    private DecodeThreadHandler mThreadHandler;

    public RepeatedScannerHandler(RepeatedScannerCallback callback) {
        mMessenger = new Messenger(this);
        this.mCallback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        int what = msg.what;
        switch (what) {
            case DecodeThreadHandler.SUCCEED:
                LogUtils.d("RepeatedScannerHandler SUCCESS ");
                if (null != mCallback) {
                    Result result = (Result) msg.obj;
                    LogUtils.d("result ="+result.toString());
                    if (null != result) {
                        mCallback.onDecodeSuccess(result);
                    }
                }
                break;
            case DecodeThreadHandler.FAIL:
                LogUtils.d("RepeatedScannerHandler FAIL ");

                requestOneFrame();
                break;
        }
    }

    private void requestOneFrame() {
        if (mThreadHandler == null || mMessenger == null || mCallback == null) {
            return;
        }
        Message msg = mThreadHandler.obtainMessage(DecodeThreadHandler.DECODE);
        msg.replyTo = mMessenger;

        mCallback.requestDecodeFrame(msg);
    }

    public void pause(){
        if (mThreadHandler != null) {
            mThreadHandler.pause();
        }
        removeCallbacksAndMessages(null);
    }

    public void resume(){
        if (mThreadHandler == null) {
            mThreadHandler = DecodeThreadHandler.createThreadHandler();
        }
        mThreadHandler.resume();
        requestOneFrame();
    }

    public void release(){
        if (mThreadHandler != null) {
            mThreadHandler.quit();
            mThreadHandler = null;
        }
        if (mMessenger != null) {
            mMessenger = null;
        }
        removeCallbacksAndMessages(null);
    }

    public interface RepeatedScannerCallback {
        void onDecodeSuccess(Result result);
        void requestDecodeFrame(Message msg);
    }
}
