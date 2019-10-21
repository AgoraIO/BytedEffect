package io.agora.rtcwithbyte.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Created by QunZhang on 2019-07-20 17:22
 */
abstract public class BaseActivity<T extends IPresenter> extends FragmentActivity implements IView {
    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setPresenter(T presenter) {
        assert presenter != null;
        mPresenter = presenter;
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }
}
