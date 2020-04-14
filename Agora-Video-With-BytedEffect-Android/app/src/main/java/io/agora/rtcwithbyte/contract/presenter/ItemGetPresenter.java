package io.agora.rtcwithbyte.contract.presenter;

import android.content.Context;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.contract.ItemGetContract;
import io.agora.rtcwithbyte.model.ButtonItem;
import io.agora.rtcwithbyte.model.ComposerNode;

import java.util.ArrayList;
import java.util.List;

import static io.agora.rtcwithbyte.contract.ItemGetContract.MASK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.NODE_BEAUTY;
import static io.agora.rtcwithbyte.contract.ItemGetContract.NODE_BEAUTY_4ITEMS;
import static io.agora.rtcwithbyte.contract.ItemGetContract.NODE_LONG_LEG;
import static io.agora.rtcwithbyte.contract.ItemGetContract.NODE_RESHAPE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.NODE_THIN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.SUB_MASK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_BODY;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_BODY_LONG_LEG;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_BODY_THIN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_SHARPEN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_SMOOTH;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_WHITEN;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_BRIGHTEN_EYE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_REMOVE_POUCH;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_SMILE_FOLDS;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE_WHITEN_TEETH;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_CHEEK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_CHIN;
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
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_EYE_SPACING;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_EYE_MOVE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE_MOUTH_MOVE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_CLOSE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_BLUSHER;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_EYEBROW;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_EYELASH;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_EYESHADOW;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_FACIAL;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_HAIR;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_LIP;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_OPTION;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_PUPIL;

/**
 * Created by QunZhang on 2019-07-21 12:27
 */
public class ItemGetPresenter extends ItemGetContract.Presenter {

    @Override
    public List<ButtonItem> getItems(int type) {
        List<ButtonItem> items = new ArrayList<>();
        switch (type & MASK) {
            case TYPE_BEAUTY_FACE:
                getBeautyFaceItems(items);
                break;
            case TYPE_BEAUTY_RESHAPE:
                getBeautyReshapeItems(items);
                break;
            case TYPE_BEAUTY_BODY:
                getBeautyBodyItems(items);
                break;
            case TYPE_MAKEUP:
                getMakeupItems(items);
                break;
            case TYPE_MAKEUP_OPTION:
                getMakeupOptionItems(items, type);
                break;
        }
        return items;
    }

    private void getBeautyFaceItems(List<ButtonItem> items) {
        Context context = getView().getContext();
        items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
        items.add(new ButtonItem(R.drawable.ic_beauty_smooth, context.getString(R.string.beauty_face_smooth), new ComposerNode(TYPE_BEAUTY_FACE_SMOOTH, NODE_BEAUTY, "smooth")));
        items.add(new ButtonItem(R.drawable.ic_beauty_whiten, context.getString(R.string.beauty_face_whiten), new ComposerNode(TYPE_BEAUTY_FACE_WHITEN, NODE_BEAUTY, "whiten")));
        items.add(new ButtonItem(R.drawable.ic_beauty_sharpen, context.getString(R.string.beauty_face_sharpen), new ComposerNode(TYPE_BEAUTY_FACE_SHARPEN, NODE_BEAUTY, "sharp")));
        items.add(new ButtonItem(R.drawable.ic_beauty_smooth, context.getString(R.string.beauty_face_brighten_eye), new ComposerNode(TYPE_BEAUTY_FACE_BRIGHTEN_EYE, NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_BRIGHTEN_EYE")));
        items.add(new ButtonItem(R.drawable.ic_beauty_smooth, context.getString(R.string.beauty_face_remove_pouch), new ComposerNode(TYPE_BEAUTY_FACE_REMOVE_POUCH, NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_REMOVE_POUCH")));
        items.add(new ButtonItem(R.drawable.ic_beauty_sharpen, context.getString(R.string.beauty_face_smile_folds), new ComposerNode(TYPE_BEAUTY_FACE_SMILE_FOLDS, NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_SMILES_FOLDS")));
        items.add(new ButtonItem(R.drawable.ic_beauty_sharpen, context.getString(R.string.beauty_face_whiten_teeth), new ComposerNode(TYPE_BEAUTY_FACE_WHITEN_TEETH, NODE_BEAUTY_4ITEMS, "BEF_BEAUTY_WHITEN_TEETH")));
    }

    private void getBeautyReshapeItems(List<ButtonItem> items) {
        Context context = getView().getContext();
        items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
        items.add(new ButtonItem(R.drawable.ic_beauty_cheek_reshape, context.getString(R.string.beauty_reshape_face_overall), new ComposerNode(TYPE_BEAUTY_RESHAPE_FACE_OVERALL, NODE_RESHAPE, "Internal_Deform_Overall")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_face_cut, context.getString(R.string.beauty_reshape_face_cut), new ComposerNode(TYPE_BEAUTY_RESHAPE_FACE_CUT, NODE_RESHAPE, "Internal_Deform_CutFace")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_face_small, context.getString(R.string.beauty_reshape_face_small), new ComposerNode(TYPE_BEAUTY_RESHAPE_FACE_SMALL, NODE_RESHAPE, "Internal_Deform_Face")));
        items.add(new ButtonItem(R.drawable.ic_beauty_eye_reshape, context.getString(R.string.beauty_reshape_eye), new ComposerNode(TYPE_BEAUTY_RESHAPE_EYE, NODE_RESHAPE, "Internal_Deform_Eye")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_eye_rotate, context.getString(R.string.beauty_reshape_eye_rotate), new ComposerNode(TYPE_BEAUTY_RESHAPE_EYE_ROTATE, NODE_RESHAPE, "Internal_Deform_RotateEye")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_cheek, context.getString(R.string.beauty_reshape_cheek), new ComposerNode(TYPE_BEAUTY_RESHAPE_CHEEK, NODE_RESHAPE, "Internal_Deform_Zoom_Cheekbone")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_jaw, context.getString(R.string.beauty_reshape_jaw), new ComposerNode(TYPE_BEAUTY_RESHAPE_JAW, NODE_RESHAPE, "Internal_Deform_Zoom_Jawbone")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_nose_lean, context.getString(R.string.beauty_reshape_nose_lean), new ComposerNode(TYPE_BEAUTY_RESHAPE_NOSE_LEAN, NODE_RESHAPE, "Internal_Deform_Nose")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_nose_long, context.getString(R.string.beauty_reshape_nose_long), new ComposerNode(TYPE_BEAUTY_RESHAPE_NOSE_LONG, NODE_RESHAPE, "Internal_Deform_MovNose")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_chin, context.getString(R.string.beauty_reshape_chin), new ComposerNode(TYPE_BEAUTY_RESHAPE_CHIN, NODE_RESHAPE, "Internal_Deform_Chin")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_forehead, context.getString(R.string.beauty_reshape_forehead), new ComposerNode(TYPE_BEAUTY_RESHAPE_FOREHEAD, NODE_RESHAPE, "Internal_Deform_Forehead")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_mouth_zoom, context.getString(R.string.beauty_reshape_mouth_zoom), new ComposerNode(TYPE_BEAUTY_RESHAPE_MOUTH_ZOOM, NODE_RESHAPE, "Internal_Deform_ZoomMouth")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_mouth_smile, context.getString(R.string.beauty_reshape_mouth_smile), new ComposerNode(TYPE_BEAUTY_RESHAPE_MOUTH_SMILE, NODE_RESHAPE, "Internal_Deform_MouthCorner")));
//        items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.beauty_reshape_decree), new ComposerNode(TYPE_BEAUTY_RESHAPE_DECREE, NODE_RESHAPE_DECREE_DARK, "removePouch")));
//        items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.beauty_reshape_dark_circle), new ComposerNode(TYPE_BEAUTY_RESHAPE_DARK, NODE_RESHAPE_DECREE_DARK, "removeNasolabialFolds")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_eye_rotate, context.getString(R.string.beauty_reshape_eye_spacing), new ComposerNode(TYPE_BEAUTY_RESHAPE_EYE_SPACING, NODE_RESHAPE, "Internal_Eye_Spacing")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_eye_rotate, context.getString(R.string.beauty_reshape_eye_move), new ComposerNode(TYPE_BEAUTY_RESHAPE_EYE_MOVE, NODE_RESHAPE, "Internal_Deform_Eye_Move")));
        items.add(new ButtonItem(R.drawable.ic_beauty_reshape_mouth_zoom, context.getString(R.string.beauty_reshape_mouth_move), new ComposerNode(TYPE_BEAUTY_RESHAPE_MOUTH_MOVE, NODE_RESHAPE, "Internal_Deform_MovMouth")));
    }

    private void getBeautyBodyItems(List<ButtonItem> items) {
        Context context = getView().getContext();
        items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
        items.add(new ButtonItem(R.drawable.ic_beauty_body_thin, context.getString(R.string.beauty_body_thin), new ComposerNode(TYPE_BEAUTY_BODY_THIN, NODE_THIN, "")));
        items.add(new ButtonItem(R.drawable.ic_beauty_body_long_leg, context.getString(R.string.beauty_body_long_leg), new ComposerNode(TYPE_BEAUTY_BODY_LONG_LEG, NODE_LONG_LEG, "")));
    }

    private void getMakeupItems(List<ButtonItem> items) {
        Context context = getView().getContext();
        items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
        items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.makeup_blusher), new ComposerNode(TYPE_MAKEUP_BLUSHER)));
        items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.makeup_lip), new ComposerNode(TYPE_MAKEUP_LIP)));
        items.add(new ButtonItem(R.drawable.ic_makeup_facial, context.getString(R.string.makeup_facial), new ComposerNode(TYPE_MAKEUP_FACIAL)));
//        items.add(new ButtonItem(R.drawable.ic_makeup_eyelash, context.getString(R.string.makeup_eyelash), new ComposerNode(TYPE_MAKEUP_EYELASH)));
        items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.makeup_pupil), new ComposerNode(TYPE_MAKEUP_PUPIL)));
        items.add(new ButtonItem(R.drawable.ic_makeup_hair, context.getString(R.string.makeup_hair), new ComposerNode(TYPE_MAKEUP_HAIR)));
        items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.makeup_eye), new ComposerNode(TYPE_MAKEUP_EYESHADOW)));
        items.add(new ButtonItem(R.drawable.ic_makeup_eyebrow, context.getString(R.string.makeup_eyebrow), new ComposerNode(TYPE_MAKEUP_EYEBROW)));
    }

    private void getMakeupOptionItems(List<ButtonItem> items, int type) {
        Context context = getView().getContext();
        switch (type & SUB_MASK) {
            case TYPE_MAKEUP_LIP:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_fuguhong), new ComposerNode(TYPE_MAKEUP_LIP, "lip/xiyouse", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_shaonvfen), new ComposerNode(TYPE_MAKEUP_LIP, "lip/shaonvfen", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_yuanqiju), new ComposerNode(TYPE_MAKEUP_LIP, "lip/yuanqiju", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_xiyouse), new ComposerNode(TYPE_MAKEUP_LIP, "lip/xiyouse", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_xiguahong), new ComposerNode(TYPE_MAKEUP_LIP, "lip/xiguahong", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_sironghong), new ComposerNode(TYPE_MAKEUP_LIP, "lip/sironghong", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_zangjuse), new ComposerNode(TYPE_MAKEUP_LIP, "lip/zangjuse", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_meizise), new ComposerNode(TYPE_MAKEUP_LIP, "lip/meizise", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_shanhuse), new ComposerNode(TYPE_MAKEUP_LIP, "lip/shanhuse", "Internal_Makeup_Lips")));
                items.add(new ButtonItem(R.drawable.ic_makeup_lip, context.getString(R.string.lip_doushafen), new ComposerNode(TYPE_MAKEUP_LIP, "lip/doushafen", "Internal_Makeup_Lips")));
                break;
            case TYPE_MAKEUP_BLUSHER:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_weixunfen), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/weixun", "Internal_Makeup_Blusher")));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_richang), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/richang", "Internal_Makeup_Blusher")));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_mitao), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/mitao", "Internal_Makeup_Blusher")));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_tiancheng), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/tiancheng", "Internal_Makeup_Blusher")));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_qiaopi), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/qiaopi", "Internal_Makeup_Blusher")));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_xinji), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/xinji", "Internal_Makeup_Blusher")));
                items.add(new ButtonItem(R.drawable.ic_makeup_blusher, context.getString(R.string.blusher_shaishang), new ComposerNode(TYPE_MAKEUP_BLUSHER, "blush/shaishang", "Internal_Makeup_Blusher")));
                break;
            case TYPE_MAKEUP_EYELASH:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyelash, context.getString(R.string.eyelash_nongmi), new ComposerNode(TYPE_MAKEUP_EYELASH, "eyelash/nongmi")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyelash, context.getString(R.string.eyelash_shanxing), new ComposerNode(TYPE_MAKEUP_EYELASH, "eyelash/shanxing")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyelash, context.getString(R.string.eyelash_wumei), new ComposerNode(TYPE_MAKEUP_EYELASH, "eyelash/wumei")));
                break;
            case TYPE_MAKEUP_PUPIL:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.pupil_hunxuezong), new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/hunxuezong", "Internal_Makeup_Pupil")));
                items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.pupil_kekezong), new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/kekezong", "Internal_Makeup_Pupil")));
                items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.pupil_mitaofen), new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/mitaofen", "Internal_Makeup_Pupil")));
                items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.pupil_shuiguanghei), new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/shuiguanghei", "Internal_Makeup_Pupil")));
                items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.pupil_xingkonglan), new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/xingkonglan", "Internal_Makeup_Pupil")));
                items.add(new ButtonItem(R.drawable.ic_makeup_pupil, context.getString(R.string.pupil_chujianhui), new ComposerNode(TYPE_MAKEUP_PUPIL, "pupil/chujianhui", "Internal_Makeup_Pupil")));
                break;
            case TYPE_MAKEUP_HAIR:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_hair, context.getString(R.string.hair_anlan), new ComposerNode(TYPE_MAKEUP_HAIR, "hair/anlan", "")));
                items.add(new ButtonItem(R.drawable.ic_makeup_hair, context.getString(R.string.hair_molv), new ComposerNode(TYPE_MAKEUP_HAIR, "hair/molv", "")));
                items.add(new ButtonItem(R.drawable.ic_makeup_hair, context.getString(R.string.hair_shenzong), new ComposerNode(TYPE_MAKEUP_HAIR, "hair/shenzong", "")));
                break;
            case TYPE_MAKEUP_EYESHADOW:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_dadizong), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/dadizong", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_wanxiahong), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/wanxiahong", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_shaonvfen), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/shaonvfen", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_qizhifen), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/qizhifen", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_meizihong), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/meizihong", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_jiaotangzong), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/jiaotangzong", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_yuanqiju), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/yuanqiju", "Internal_Makeup_Eye")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eye, context.getString(R.string.eye_naichase), new ComposerNode(TYPE_MAKEUP_EYESHADOW, "eyeshadow/naichase", "Internal_Makeup_Eye")));
                break;
            case TYPE_MAKEUP_EYEBROW:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyebrow, context.getString(R.string.eyebrow_BRO1), new ComposerNode(TYPE_MAKEUP_EYEBROW, "eyebrow/BR01", "Internal_Makeup_Brow")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyebrow, context.getString(R.string.eyebrow_BKO1), new ComposerNode(TYPE_MAKEUP_EYEBROW, "eyebrow/BK01", "Internal_Makeup_Brow")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyebrow, context.getString(R.string.eyebrow_BKO2), new ComposerNode(TYPE_MAKEUP_EYEBROW, "eyebrow/BK02", "Internal_Makeup_Brow")));
                items.add(new ButtonItem(R.drawable.ic_makeup_eyebrow, context.getString(R.string.eyebrow_BKO3), new ComposerNode(TYPE_MAKEUP_EYEBROW, "eyebrow/BK03", "Internal_Makeup_Brow")));
                break;
            case TYPE_MAKEUP_FACIAL:
                items.add(new ButtonItem(R.drawable.ic_none, context.getString(R.string.close), new ComposerNode(TYPE_CLOSE)));
                items.add(new ButtonItem(R.drawable.ic_makeup_facial, context.getString(R.string.facial_1), new ComposerNode(TYPE_MAKEUP_FACIAL, "facial/xiurong01", "Internal_Makeup_Facial")));
                items.add(new ButtonItem(R.drawable.ic_makeup_facial, context.getString(R.string.facial_2), new ComposerNode(TYPE_MAKEUP_FACIAL, "facial/xiurong02", "Internal_Makeup_Facial")));
                items.add(new ButtonItem(R.drawable.ic_makeup_facial, context.getString(R.string.facial_3), new ComposerNode(TYPE_MAKEUP_FACIAL, "facial/xiurong03", "Internal_Makeup_Facial")));
                items.add(new ButtonItem(R.drawable.ic_makeup_facial, context.getString(R.string.facial_4), new ComposerNode(TYPE_MAKEUP_FACIAL, "facial/xiurong04", "Internal_Makeup_Facial")));
                break;
        }
    }
}
