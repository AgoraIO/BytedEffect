package io.agora.rtcwithbyte.contract;


import java.util.List;

import io.agora.rtcwithbyte.base.BasePresenter;
import io.agora.rtcwithbyte.base.IView;
import io.agora.rtcwithbyte.model.FilterItem;

/**
 * Created by QunZhang on 2019-07-21 12:22
 */
public interface FilterContract {
    interface View extends IView {

    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract List<FilterItem> getItems();
    }
}
