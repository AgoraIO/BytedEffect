package io.agora.rtcwithbyte.fragment;

import io.agora.rtcwithbyte.base.BaseFragment;
import io.agora.rtcwithbyte.base.IPresenter;

/**
 * 每个功能fragemnt的基类
 * base class of each fragment
 * @param <T>
 */
public abstract class BaseFeatureFragment<T extends IPresenter, Callback> extends BaseFragment<T> {
    private Callback mCallback;

    public BaseFeatureFragment setCallback(Callback t){
        this.mCallback =t;
        return this;
    }

    public Callback getCallback() {
        return mCallback;
    }
}
