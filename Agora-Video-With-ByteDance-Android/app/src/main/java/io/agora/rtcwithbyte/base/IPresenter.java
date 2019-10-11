package io.agora.rtcwithbyte.base;

public interface IPresenter {
    void attachView(IView view);
    void detachView();
}
