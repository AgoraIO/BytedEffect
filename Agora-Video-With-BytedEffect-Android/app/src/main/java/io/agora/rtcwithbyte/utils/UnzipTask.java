package io.agora.rtcwithbyte.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class UnzipTask extends AsyncTask<String, Void, Boolean> {

    public interface IUnzipViewCallback {
        Context getContext();
        void onStartTask();
        void onEndTask(boolean result);
    }

    private WeakReference<IUnzipViewCallback> mCallback;

    public UnzipTask(IUnzipViewCallback callback) {
        mCallback = new WeakReference<>(callback);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        IUnzipViewCallback callback = mCallback.get();
        if (callback == null) return false;
        String path = strings[0];
        File dstFile = callback.getContext().getExternalFilesDir("assets");
        if(new File(dstFile, path).exists())
            return true;
        FileUtils.clearDir(new File(dstFile, path));

        try {
            FileUtils.copyAssets(callback.getContext().getAssets(), path, dstFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        IUnzipViewCallback callback = mCallback.get();
        if (callback == null) return;
        callback.onStartTask();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        IUnzipViewCallback callback = mCallback.get();
        if (callback == null) return;
        callback.onEndTask(result);
    }
}
