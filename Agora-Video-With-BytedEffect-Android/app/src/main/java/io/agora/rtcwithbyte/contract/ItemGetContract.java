package io.agora.rtcwithbyte.contract;


import java.util.List;

import io.agora.rtcwithbyte.base.BasePresenter;
import io.agora.rtcwithbyte.base.IView;
import io.agora.rtcwithbyte.model.ButtonItem;

/**
 * Created by QunZhang on 2019-07-20 23:39
 * 将一个 int 分为两段，前 16 位存储一级菜单，分别是 美颜、美妆、滤镜、美妆选项
 * 第 17～24 位存储二级菜单，如美颜的 大眼、瘦脸等，美妆选项的 美瞳、眉毛等
 * 最后 8 位存储三级菜单，目前只有美妆选项使用到，如存储美瞳的几种种类
 *
 * Divide an int into two segments, and use the first 16 bits to store the first-level menu, which are beauty, makeup, filter and makeup options respectively,
 * 17 ~ 24 bits to store the secondary menu, such as big eyes, thin face, etc., eye contact, eyebrows, etc ,
 * the last 8 bits to store the three-level menu, which is currently only used for cosmetic options, such as several types of contact lenses
 */
public interface ItemGetContract {
    int OFFSET = 16;
    int MASK = ~0xFFFF;
    int SUB_OFFSET = 8;
    int SUB_MASK = ~0xFF;

    // 一级菜单
    //The second menu

    int TYPE_CLOSE = -1;
    // Beautify face 美颜
    int TYPE_BEAUTY_FACE = 1 << OFFSET;
    // Beautify reshape 美型
    int TYPE_BEAUTY_RESHAPE = 2 << OFFSET;
    // Beautify body 美体
    int TYPE_BEAUTY_BODY = 3 << OFFSET;
    // Makeup 美妆
    int TYPE_MAKEUP = 4 << OFFSET;
    // Filiter 滤镜
    int TYPE_FILTER = 5 << OFFSET;
    // Makeup option 美妆类型
    int TYPE_MAKEUP_OPTION = 6 << OFFSET;

    // 二级菜单
    //The secondary menu

    // Beautify face 美颜
    int TYPE_BEAUTY_FACE_SMOOTH = TYPE_BEAUTY_FACE + (1 << SUB_OFFSET);
    int TYPE_BEAUTY_FACE_WHITEN = TYPE_BEAUTY_FACE + (2 << SUB_OFFSET);
    int TYPE_BEAUTY_FACE_SHARPEN = TYPE_BEAUTY_FACE + (3 << SUB_OFFSET);

    // Beautify reshape 美形
    int TYPE_BEAUTY_RESHAPE_FACE_OVERALL = TYPE_BEAUTY_RESHAPE + (1 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_EYE = TYPE_BEAUTY_RESHAPE + (2 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_FACE_SMALL = TYPE_BEAUTY_RESHAPE + (3 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_FACE_CUT = TYPE_BEAUTY_RESHAPE + (4 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_CHEEK = TYPE_BEAUTY_RESHAPE + (5 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_JAW = TYPE_BEAUTY_RESHAPE + (6 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_NOSE_LEAN = TYPE_BEAUTY_RESHAPE + (7 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_NOSE_LONG = TYPE_BEAUTY_RESHAPE + (8 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_CHIN = TYPE_BEAUTY_RESHAPE + (9 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_FOREHEAD = TYPE_BEAUTY_RESHAPE + (10 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_EYE_ROTATE = TYPE_BEAUTY_RESHAPE + (11 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM = TYPE_BEAUTY_RESHAPE + (12 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_MOUTH_SMILE = TYPE_BEAUTY_RESHAPE + (13 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_DECREE = TYPE_BEAUTY_RESHAPE + (14 << SUB_OFFSET);
    int TYPE_BEAUTY_RESHAPE_DARK = TYPE_BEAUTY_RESHAPE + (15 << SUB_OFFSET);

    // Beautify body 美体
    int TYPE_BEAUTY_BODY_THIN = TYPE_BEAUTY_BODY + (1 << SUB_OFFSET);
    int TYPE_BEAUTY_BODY_LONG_LEG = TYPE_BEAUTY_BODY + (2 << SUB_OFFSET);


    // Makeup 美妆
    int TYPE_MAKEUP_LIP = TYPE_MAKEUP_OPTION + (1 << SUB_OFFSET);
    int TYPE_MAKEUP_BLUSHER = TYPE_MAKEUP_OPTION + (2 << SUB_OFFSET);
    int TYPE_MAKEUP_EYELASH = TYPE_MAKEUP_OPTION + (3 << SUB_OFFSET);
    int TYPE_MAKEUP_PUPIL = TYPE_MAKEUP_OPTION + (4 << SUB_OFFSET);
    int TYPE_MAKEUP_HAIR = TYPE_MAKEUP_OPTION + (5 << SUB_OFFSET);
    int TYPE_MAKEUP_EYESHADOW = TYPE_MAKEUP_OPTION + (6 << SUB_OFFSET);
    int TYPE_MAKEUP_EYEBROW = TYPE_MAKEUP_OPTION + (7 << SUB_OFFSET);


    // Node name 结点名称
    String NODE_BEAUTY = "beauty";
    String NODE_RESHAPE = "reshape";
    String NODE_LONG_LEG = "body/longleg";
    String NODE_THIN = "body/thin";


    interface View extends IView {

    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 根据类型返回一个 {@link ButtonItem} 列表
         * Returns a list of {@link ButtonItem} based on type
         * @param type menu type，such as {@link this#TYPE_BEAUTY_FACE}{@link this#TYPE_MAKEUP}{@link this#TYPE_MAKEUP_PUPIL}
         * @return ButtonItem list
         */
        public abstract List<ButtonItem> getItems(int type);
    }
}
