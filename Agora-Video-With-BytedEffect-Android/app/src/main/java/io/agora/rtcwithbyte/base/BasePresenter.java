package io.agora.rtcwithbyte.base;

import java.lang.ref.WeakReference;

public class BasePresenter<T extends IView> implements IPresenter {
    private WeakReference<T> mViewRef;

    @Override
    public void attachView(IView view) {
        mViewRef = new WeakReference<>((T) view);
    }

    @Override
    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    protected T getView(){
        if (mViewRef != null) {
            return mViewRef.get();
        }else {
            return null;
        }
    }

    public boolean isAvailable() {
        return getView() != null;
    }
}
