package io.agora.rtcwithbyte.contract;


import io.agora.rtcwithbyte.base.BasePresenter;
import io.agora.rtcwithbyte.base.IView;

/**
 * Created by QunZhang on 2019-07-20 17:26
 */
public interface WelcomeContract {
    interface View extends IView {
        void onStartTask();
        void onEndTask(boolean result);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void startTask();
        public abstract int getVersionCode();
        public abstract String getVersionName();
        public abstract boolean resourceReady();
    }
}
