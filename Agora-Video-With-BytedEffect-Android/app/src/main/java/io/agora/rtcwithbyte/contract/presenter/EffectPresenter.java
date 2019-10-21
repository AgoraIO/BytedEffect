package io.agora.rtcwithbyte.contract.presenter;

import android.annotation.SuppressLint;
import android.util.SparseArray;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.agora.rtcwithbyte.contract.EffectContract;
import io.agora.rtcwithbyte.contract.ItemGetContract;
import io.agora.rtcwithbyte.model.ButtonItem;
import io.agora.rtcwithbyte.model.ComposerNode;

import static io.agora.rtcwithbyte.contract.ItemGetContract.MASK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_BODY_LONG_LEG;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_BODY_THIN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_SHARPEN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_SMOOTH;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_WHITEN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_CHEEK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_CHIN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_DARK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_DECREE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_EYE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_EYE_ROTATE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_FACE_CUT;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_FACE_OVERALL;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_FACE_SMALL;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_FOREHEAD;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_JAW;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_MOUTH_SMILE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_NOSE_LEAN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_NOSE_LONG;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_CLOSE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_FILTER;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_OPTION;

/**
 * Created by QunZhang on 2019-07-22 13:57
 */
public class EffectPresenter extends EffectContract.Presenter {
    public static final Map<Integer, Float> DEFAULT_VALUE;
    static {
        @SuppressLint("UseSparseArrays") Map<Integer, Float> map = new HashMap<>();
        // 美颜
        // beauty face
        map.put(TYPE_BEAUTY_FACE_SMOOTH, 0.6F);
        map.put(TYPE_BEAUTY_FACE_WHITEN, 0.0F);
        map.put(TYPE_BEAUTY_FACE_SHARPEN, 0.4F);
        // 美型
        // beaury reshape
        map.put(TYPE_BEAUTY_RESHAPE_FACE_OVERALL, 0.5F);
        map.put(TYPE_BEAUTY_RESHAPE_FACE_SMALL, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_FACE_CUT, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_EYE, 0.3F);
        map.put(TYPE_BEAUTY_RESHAPE_EYE_ROTATE, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_CHEEK, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_JAW, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_NOSE_LEAN, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_NOSE_LONG, 0.25F);
        map.put(TYPE_BEAUTY_RESHAPE_CHIN, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_FOREHEAD, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_MOUTH_SMILE, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_DECREE, 0.0F);
        map.put(TYPE_BEAUTY_RESHAPE_DARK, 0.5F);
        map.put(TYPE_BEAUTY_BODY_THIN, 0.4f);
        map.put(TYPE_BEAUTY_BODY_LONG_LEG, 0.4f);
        // 滤镜
        // filter
        map.put(TYPE_FILTER, 0.8F);
        DEFAULT_VALUE = Collections.unmodifiableMap(map);
    }

    private ItemGetContract.Presenter mItemGet;

    @Override
    public void removeNodesOfType(SparseArray<ComposerNode> composerNodeMap, int type) {
        removeNodesWithMakAndType(composerNodeMap, MASK, type & MASK);
    }

    private void removeNodesWithMakAndType(SparseArray<ComposerNode> map, int mask, int type) {
        int i = 0;
        ComposerNode node;
        while ((map.valueAt(i) instanceof ComposerNode) && (node = map.valueAt(i)) != null) {
            if ((node.getId() & mask) == type) {
                map.removeAt(i);
            } else {
                i++;
            }
        }
    }

    @Override
    public String[] generateComposerNodes(SparseArray<ComposerNode> composerNodeMap) {
        List<String> list = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < composerNodeMap.size(); i++) {
            ComposerNode node = composerNodeMap.valueAt(i);
            if (set.contains(node.getNode())) {
                continue;
            } else {
                set.add(node.getNode());
            }
            if (isAhead(node)) {
                list.add(0, node.getNode());
            } else {
                list.add(node.getNode());
            }
        }

        return list.toArray(new String[0]);
    }

    @Override
    public void generateDefaultBeautyNodes(SparseArray<ComposerNode> composerNodeMap) {
        if (mItemGet == null) {
            mItemGet = new ItemGetPresenter();
            mItemGet.attachView(getView());
        }
        List<ButtonItem> beautyItems = new ArrayList<>();
        beautyItems.addAll(mItemGet.getItems(TYPE_BEAUTY_FACE));
        beautyItems.addAll(mItemGet.getItems(TYPE_BEAUTY_RESHAPE));

        for (ButtonItem item : beautyItems) {
            if (item.getNode().getId() == TYPE_CLOSE) {
                continue;
            }
            item.getNode().setValue(DEFAULT_VALUE.get(item.getNode().getId()));
            composerNodeMap.put(item.getNode().getId(), item.getNode());
        }
    }

    @Override
    public float getDefaultValue(int type) {
        return DEFAULT_VALUE.get(type);
    }

    @Override
    public boolean hasIntensity(int type) {
        int parent = type & MASK;
        return parent == TYPE_BEAUTY_FACE || parent == TYPE_BEAUTY_RESHAPE;
    }

    private boolean isAhead(ComposerNode node) {
        return (node.getId() & MASK) == TYPE_MAKEUP_OPTION;
    }
}
