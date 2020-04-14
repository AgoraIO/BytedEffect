package io.agora.rtcwithbyte.contract;

import io.agora.rtcwithbyte.base.BasePresenter;
import io.agora.rtcwithbyte.base.IView;
import io.agora.rtcwithbyte.model.StickerItem;

import java.util.List;

/**
 * Created by QunZhang on 2019-07-21 12:24
 */
public interface StickerContract {
    int TYPE_STICKER = 1;
    int TYPE_ANIMOJI = 2;

    interface View extends IView {

    }

    abstract class Presenter extends BasePresenter<View> {
//        public abstract List<StickerItem> getItems();
        public abstract List<StickerItem> getItems(int type);
    }
}
