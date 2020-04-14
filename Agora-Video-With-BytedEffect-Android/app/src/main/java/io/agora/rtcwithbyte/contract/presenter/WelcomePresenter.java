package io.agora.rtcwithbyte.contract.presenter;

import android.content.Context;
import android.content.pm.PackageManager;

import io.agora.rtcwithbyte.ResourceHelper;
import io.agora.rtcwithbyte.contract.WelcomeContract;
import io.agora.rtcwithbyte.task.UnzipTask;

/**
 * Created by QunZhang on 2019-07-20 17:30
 */
public class WelcomePresenter extends WelcomeContract.Presenter implements UnzipTask.IUnzipViewCallback {

    @Override
    public void startTask() {
        UnzipTask mTask = new UnzipTask(this);
        mTask.execute(ResourceHelper.RESOURCE);
    }

    @Override
    public int getVersionCode() {
        Context context = getView().getContext();
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String getVersionName() {
        Context context = getView().getContext();
        try {
            return "v " + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean resourceReady() {
        return ResourceHelper.isResourceReady(getView().getContext(), getVersionCode());
    }

    @Override
    public Context getContext() {
        return getView().getContext();
    }

    @Override
    public void onStartTask() {
        if (isAvailable()) {
            getView().onStartTask();
        }
    }

    @Override
    public void onEndTask(boolean result) {
        if (result) {
            ResourceHelper.setResourceReady(getView().getContext(), result, getVersionCode());
        }
        if (isAvailable()) {
            getView().onEndTask(result);
        }
    }
}
