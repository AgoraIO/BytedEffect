package io.agora.rtcwithbyte.contract;


import java.util.List;

import io.agora.rtcwithbyte.base.BasePresenter;
import io.agora.rtcwithbyte.base.IView;
import io.agora.rtcwithbyte.model.StickerItem;

/**
 * Created by QunZhang on 2019-07-21 12:24
 */
public interface StickerContract {
    interface View extends IView {

    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract List<StickerItem> getItems();
    }
}
