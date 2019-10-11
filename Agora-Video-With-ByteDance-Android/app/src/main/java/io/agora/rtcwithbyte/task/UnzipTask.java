package io.agora.rtcwithbyte.task;

import android.content.Context;
import android.os.AsyncTask;


import java.io.File;
import java.lang.ref.WeakReference;

import library.FileUtils;

/**
 * Created by QunZhang on 2019-07-20 13:05
 */
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
        String zipPath = strings[0];
        File dstFile = mCallback.get().getContext().getExternalFilesDir("assets");

        return FileUtils.unzipAssetFile(mCallback.get().getContext(), zipPath, dstFile);
    }

    @Override
    protected void onPreExecute() {
        mCallback.get().onStartTask();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallback.get().onEndTask(result);
    }
}
