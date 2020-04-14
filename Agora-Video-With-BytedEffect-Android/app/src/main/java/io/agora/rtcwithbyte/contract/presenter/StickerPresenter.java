package io.agora.rtcwithbyte.contract.presenter;

import android.content.Context;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.ResourceHelper;
import io.agora.rtcwithbyte.contract.StickerContract;
import io.agora.rtcwithbyte.model.StickerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.agora.rtcwithbyte.contract.StickerContract.TYPE_ANIMOJI;
import static io.agora.rtcwithbyte.contract.StickerContract.TYPE_STICKER;

/**
 * Created by QunZhang on 2019-07-21 14:09
 */
public class StickerPresenter extends StickerContract.Presenter {
    private List<StickerItem> mStickerItems;
    private List<StickerItem> mAnimojiItems;

    @Override
    public List<StickerItem> getItems(int type) {
        switch (type) {
            case TYPE_STICKER:
                return getStickersItems();
            case TYPE_ANIMOJI:
                return getAnimojiItems();
                default:
                    return Collections.emptyList();
        }
    }

    private List<StickerItem> getStickersItems() {
        if (mStickerItems != null) {
            return mStickerItems;
        }

        Context context = getView().getContext();
        mStickerItems = new ArrayList<>();
        mStickerItems.add(new StickerItem(context.getString(R.string.filter_normal), R.drawable.clear, null));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_change_face), R.drawable.icon_change_face, ResourceHelper.getStickerPath(context,"change_face"), context.getString(R.string.sticker_change_face_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_line_dance), R.drawable.icon_line_dance, ResourceHelper.getStickerPath(context,"line_dance")));

        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_xiyue), R.drawable.icon_xiyue, ResourceHelper.getStickerPath(context,"739661e875e3086700024d34eb5ee92c")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_huaxianzi), R.drawable.icon_huaxian, ResourceHelper.getStickerPath(context,"6bc53e0a429951da45d55f91f01a9403"), context.getString(R.string.sticker_huaxianzi_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_caixiaomao), R.drawable.icon_xiaomao, ResourceHelper.getStickerPath(context,"e31f163f969a35655b1953c4cdf49d77"), context.getString(R.string.sticker_caixiaomao_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_xiaoemo), R.drawable.icon_emo, ResourceHelper.getStickerPath(context,"01dd809c056708f5ad97a1327ea2ae95")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_shengrikuaile), R.drawable.icon_happy_birthday, ResourceHelper.getStickerPath(context,"95a9aeb2c7f99d3d8f7931ea2cbe11ce"), context.getString(R.string.sticker_shengrikuaile_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_maomao), R.drawable.icon_maomao, ResourceHelper.getStickerPath(context,"12dc4ebc9802e812dea2a64ddb0e03d8")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_wumeiniang), R.drawable.iocn_wumeiniang, ResourceHelper.getStickerPath(context,"6eeefb66259e73e5b10090271a278d21"), context.getString(R.string.sticker_wumeiniang_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_liuxing), R.drawable.icon_liuxing, ResourceHelper.getStickerPath(context,"d765b60b9c046618ccf95973212bcb52"), context.getString(R.string.sticker_liuxing_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_yinghua), R.drawable.icon_yinghuazhuang, ResourceHelper.getStickerPath(context,"788130eaaa4093084d9683ff3d349a18")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_tujie), R.drawable.icon_tujie, ResourceHelper.getStickerPath(context,"80c48bd4e99eb65cb2085029de50e7ca")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_dielianjin), R.drawable.icon_dienianjin, ResourceHelper.getStickerPath(context,"14fc8ac081ffea4c797a30693906e604"), context.getString(R.string.sticker_dielianjin_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_dielianfen), R.drawable.iocn_dienianfen, ResourceHelper.getStickerPath(context,"e8dc3a7dda4872dd3e87ba62a13da865"), context.getString(R.string.sticker_dielianfen_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_huahai), R.drawable.icon_huahai, ResourceHelper.getStickerPath(context,"f24643fd56b51e1b73052b12e6228e4c"), context.getString(R.string.sticker_huahai_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_miluzhuang), R.drawable.icon_milu, ResourceHelper.getStickerPath(context,"ba8dd4129cfbdaa5d3fbc20f2440ed27")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_nyekong), R.drawable.icon_yekongyanhuo, ResourceHelper.getStickerPath(context,"d2158449000686c8387035d73194c16c"), context.getString(R.string.sticker_yekong_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_haiwang), R.drawable.icon_haiwang, ResourceHelper.getStickerPath(context,"b36de00c4a0159ee21026293284b4b3d"), context.getString(R.string.sticker_haiwang_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_tanchizhu), R.drawable.icon_zhubizi, ResourceHelper.getStickerPath(context,"c2babd85f326bd7606c306466fb8c68f")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_xinxing), R.drawable.icon_fashexinxin, ResourceHelper.getStickerPath(context,"959c694d54d12b7564e841cc414e9fce"), context.getString(R.string.sticker_xinxing_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_milutoushi), R.drawable.icon_milutou, ResourceHelper.getStickerPath(context,"9142ffa02be322ec339ac7dc76f7d0f3")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_chelianmao), R.drawable.icon_chelianmao, ResourceHelper.getStickerPath(context,"47127c515e75a6198c17d9833403de06")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_chuipaopao), R.drawable.icon_chuipaopao, ResourceHelper.getStickerPath(context,"3c9b2bd6b54272e61db451314b102eff")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_mengmengxiaolu), R.drawable.icon_mengmengxiaolu, ResourceHelper.getStickerPath(context,"3465b8b40b1c45476d1570656a632bea"), context.getString(R.string.sticker_mengmengxiaolu_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_duiwohaqi), R.drawable.icon_duiwohaqi, ResourceHelper.getStickerPath(context,"ca46adae688d22d885cbc5bd0b4ab595"), context.getString(R.string.sticker_duiwohaqi_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_tuaixin), R.drawable.icon_chuiaixin, ResourceHelper.getStickerPath(context,"4d0ca76cbb4b967cc9f8b6447c6470d8"), context.getString(R.string.sticker_tuaixin_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_xiaohuanggou), R.drawable.icon_xiaohuanggou, ResourceHelper.getStickerPath(context,"7841f11c0ac01478044e3f4bea3ced9d")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_woshishui), R.drawable.icon_woshishui, ResourceHelper.getStickerPath(context,"725b308b77aa3349a73d72a73f4cc786"), context.getString(R.string.sticker_woshishui_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_nvjingling), R.drawable.icon_nvjingling, ResourceHelper.getStickerPath(context,"170283d9c2f6b7a282f843e88520b117"), context.getString(R.string.sticker_nvjingling_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_sanzhilanmao), R.drawable.icon_sanzhilanmao, ResourceHelper.getStickerPath(context,"623a287f5dd0bc5e914716778edf5834")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_huaxin), R.drawable.icon_aixinxin, ResourceHelper.getStickerPath(context,"2e500c659d4ca514ca144f619add02f7")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_yanzhisaomiao), R.drawable.icon_saomiao, ResourceHelper.getStickerPath(context,"1ada96a8bdfe03333a8192b32163e7b2"), context.getString(R.string.sticker_yanzhisaomiao_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_tiezhi), R.drawable.icon_tiezhizhuang, ResourceHelper.getStickerPath(context,"b6cc340e0e089e2fb96c4a9f9d6ee238")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_caomei), R.drawable.icon_caomeizhuang, ResourceHelper.getStickerPath(context,"fd5bbc5eae69875246ddde4ebe107132")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_xinhua), R.drawable.icon_xinhualufang, ResourceHelper.getStickerPath(context,"fe664d3d2cccf9acc524885508b0ea0a"), context.getString(R.string.sticker_xinhua_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_wpikaqiu), R.drawable.icon_pikaqiu, ResourceHelper.getStickerPath(context,"973855381b6e36fc862848be4eb2d209"), context.getString(R.string.sticker_pikaqiu_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_pangliang), R.drawable.icon_shaonvlian, ResourceHelper.getStickerPath(context,"ec7672de92b66fdcca6a0755df0ed199"), context.getString(R.string.sticker_panglian_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_pikaqiu2), R.drawable.icon_pikaqweiba, ResourceHelper.getStickerPath(context,"d8399c8dfcf73e829cd608e549de4d7d"), context.getString(R.string.sticker_pikaqiu2_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_bixintu), R.drawable.icon_bixintu, ResourceHelper.getStickerPath(context,"752cb99a67bc396852e95d86e5da0d66")));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_xiaohuahua), R.drawable.icon_fenhongxiaohua, ResourceHelper.getStickerPath(context,"c1b490a853b627a0e0b99f3f0638d89d"), context.getString(R.string.sticker_xiaohuahua_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_lentu), R.drawable.icon_lentubaby, ResourceHelper.getStickerPath(context,"d679ae8fbb673d0133909a67faf1423b"), context.getString(R.string.sticker_lengtu_tip)));
        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_yanjingli), R.drawable.icon_yanjlimdaidongxi, ResourceHelper.getStickerPath(context,"006baecf13b35f5f27d099b138383484"), context.getString(R.string.sticker_yanjingli_tip)));
//        mStickerItems.add(new StickerItem(context.getString(R.string.sticker_happy_birth_day), R.drawable.icon_happy_birth_day, ResourceHelper.getStickerPath(context, "1cb7ee9fec82f44b9fa24f5f4d8ff97b")));
        return mStickerItems;
    }

    private List<StickerItem> getAnimojiItems() {
        if (mAnimojiItems != null) {
            return mAnimojiItems;
        }
        mAnimojiItems = new ArrayList<>();
        Context context = getView().getContext();
        mAnimojiItems.add(new StickerItem(context.getString(R.string.filter_normal), R.drawable.clear, null));
        mAnimojiItems.add(new StickerItem(context.getString(R.string.animoji_boy), R.drawable.icon_change_face, ResourceHelper.getAnimojiPath(context, "animoji_boy")));
        mAnimojiItems.add(new StickerItem(context.getString(R.string.animoji_girl), R.drawable.icon_change_face, ResourceHelper.getAnimojiPath(context, "mm")));
//        mAnimojiItems.add(new StickerItem("mm", R.drawable.icon_change_face, ResourceHelper.getStickerPath(context, "mm")));
        return mAnimojiItems;
    }
}
