package io.agora.rtcwithbyte.contract;

import android.util.SparseArray;

import io.agora.rtcwithbyte.base.BasePresenter;
import io.agora.rtcwithbyte.base.IView;
import io.agora.rtcwithbyte.model.ComposerNode;

/**
 * Created by QunZhang on 2019-07-22 13:45
 */
public interface EffectContract {

    interface View extends IView {

    }

    abstract class Presenter extends BasePresenter<View> {

        /**
         * 移除某一种类型的 composer node
         * Remove a certain type of composer node
         * @param composerNodeMap composer node map
         * @param type 某一种类型，如{@link ItemGetContract#TYPE_BEAUTY_FACE}，当
         *             type 为这个值时，会移除 composerNodeMap 中所有在此类目下的特效，
         *             如{@link ItemGetContract#TYPE_BEAUTY_FACE_SMOOTH}
         *             a certain type，such as {@link ItemGetContract#TYPE_BEAUTY_FACE} and
         *             {@link ItemGetContract#TYPE_BEAUTY_FACE_SMOOTH}
         *
         */
        abstract public void removeNodesOfType(SparseArray<ComposerNode> composerNodeMap, int type);

        /**
         * 移除某一种类型的 progress，逻辑同 removeNodesOfType
         * @param progressMap map
         * @param type type
         */
        abstract public void removeProgressInMap(SparseArray<Float> progressMap, int type);

        /**
         * 根据 composer node map 生成 composer nodes
         * Generate composer nodes based on the composer node map
         * @param composerNodeMap composer node map
         * @return 返回一个 String 数组，存储所有 composer node 的路径，即{@link ComposerNode#getNode()}
         *          Returns a String array that stores all the composer node's paths, namely {@link ComposerNode#getNode()}
         */
        abstract public String[] generateComposerNodes(SparseArray<ComposerNode> composerNodeMap);

        /**
         * 设置美颜默认值
         * @param composerNodeMap composer node map
         */
        abstract public void generateDefaultBeautyNodes(SparseArray<ComposerNode> composerNodeMap);

        /**
         * 获取某一功能默认值
         * @param type 功能 id
         * @return 默认值
         */
        abstract public float getDefaultValue(int type);

        /**
         * 判断某一个功能是否有强度调节
         * @param type 功能 id
         * @return true 可以调节强度 false 不可以调节
         */
        abstract public boolean hasIntensity(int type);
    }
}
