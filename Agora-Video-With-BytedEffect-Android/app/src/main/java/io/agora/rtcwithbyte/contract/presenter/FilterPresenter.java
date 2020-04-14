package io.agora.rtcwithbyte.contract.presenter;

import android.content.Context;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.ResourceHelper;
import io.agora.rtcwithbyte.contract.FilterContract;
import io.agora.rtcwithbyte.model.FilterItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by QunZhang on 2019-07-21 13:58
 */
public class FilterPresenter extends FilterContract.Presenter {
    private static final int[] IMAGES = new int[] {
            R.drawable.zhengchang,
            R.drawable.baixi,
            R.drawable.naiyou,
            R.drawable.yangqi,
            R.drawable.jugeng,
            R.drawable.luolita,
            R.drawable.mitao,
            R.drawable.makalong,
            R.drawable.paomo,
            R.drawable.yinhua,
            R.drawable.musi,
            R.drawable.wuyu,
            R.drawable.beihaidao,
            R.drawable.riza,
            R.drawable.xiyatu,
            R.drawable.jingmi,
            R.drawable.jiaopian,
            R.drawable.nuanyang,
            R.drawable.jiuri,
            R.drawable.hongchun,
            R.drawable.julandiao,
            R.drawable.tuise,
            R.drawable.heibai,
            R.drawable.wenrou,
            R.drawable.lianaichaotian,
            R.drawable.chujian,
            R.drawable.andiao,
            R.drawable.naicha,
            R.drawable.soft,
            R.drawable.xiyang,
            R.drawable.lengyang,
            R.drawable.haibianrenxiang,
            R.drawable.gaojihui,
            R.drawable.haidao,
            R.drawable.qianxia,
            R.drawable.yese,
            R.drawable.hongzong,
            R.drawable.qingtou,
            R.drawable.ziran2,
            R.drawable.suda,
            R.drawable.jiazhou,
            R.drawable.shise,
            R.drawable.chuanwei,
            R.drawable.meishijiaopian,
            R.drawable.hongsefugu,
            R.drawable.lutu,
            R.drawable.nuanhuang,
            R.drawable.landiaojiaopian

    };

    private List<FilterItem> mItems;

    @Override
    public List<FilterItem> getItems() {
        if (mItems != null) {
            return mItems;
        }
        mItems = new ArrayList<>();
        Context context = getView().getContext();
        String[] FILTER_TITLE = new String[]{
                context.getString(R.string.filter_normal),
                context.getString(R.string.filter_chalk),
                context.getString(R.string.filter_cream),
                context.getString(R.string.filter_oxgen),
                context.getString(R.string.filter_campan),
                context.getString(R.string.filter_lolita),
                context.getString(R.string.filter_mitao),
                context.getString(R.string.filter_makalong),
                context.getString(R.string.filter_paomo),
                context.getString(R.string.filter_yinhua),
                context.getString(R.string.filter_musi),
                context.getString(R.string.filter_wuyu),
                context.getString(R.string.filter_beihaidao),
                context.getString(R.string.filter_riza),
                context.getString(R.string.filter_xiyatu),
                context.getString(R.string.filter_jingmi),
                context.getString(R.string.filter_jiaopian),
                context.getString(R.string.filter_nuanyang),
                context.getString(R.string.filter_jiuri),
                context.getString(R.string.filter_hongchun),
                context.getString(R.string.filter_julandiao),
                context.getString(R.string.filter_tuise),
                context.getString(R.string.filter_heibai),
                context.getString(R.string.filter_wenrou),
                context.getString(R.string.filter_lianaichaotian),
                context.getString(R.string.filter_chujian),
                context.getString(R.string.filter_andiao),
                context.getString(R.string.filter_naicha),
                context.getString(R.string.filter_soft),
                context.getString(R.string.filter_xiyang),
                context.getString(R.string.filter_lengyang),
                context.getString(R.string.filter_haibianrenxiang),
                context.getString(R.string.filter_gaojihui),
                context.getString(R.string.filter_haidao),
                context.getString(R.string.filter_qianxia),
                context.getString(R.string.filter_yese),
                context.getString(R.string.filter_hongzong),
                context.getString(R.string.filter_qingtou),
                context.getString(R.string.filter_ziran2),
                context.getString(R.string.filter_suda),
                context.getString(R.string.filter_jiazhou),
                context.getString(R.string.filter_shise),
                context.getString(R.string.filter_chuanwei),
                context.getString(R.string.filter_meishijiaopian),
                context.getString(R.string.filter_hongsefugu),
                context.getString(R.string.filter_lvtu),
                context.getString(R.string.filter_nuanhuang),
                context.getString(R.string.filter_landiaojiaopian),

        };
        List<File> mFileList = new ArrayList<>();
        mFileList.add(null);  // normal 正常
        mFileList.addAll(Arrays.asList(ResourceHelper.getFilterResources(context)));
//        mFileList = mFileList.subList(0, 23);
        Collections.sort(mFileList, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                if (file == null)
                    return -1;
                if (t1 == null)
                    return 1;

                String s = "";
                String s1 = "";

                String[] arrays =  file.getName().split("/");
                String[] arrays1 =  t1.getName().split("/");

                arrays = arrays[arrays.length - 1 ].split("_");
                arrays1 = arrays1[arrays1.length - 1 ].split("_");

                s =  arrays[1];
                s1 =  arrays1[1];


                return Integer.valueOf(s) - Integer.valueOf(s1);
            }
        });

        for (int i = 0; i < mFileList.size(); i++) {
            mItems.add(new FilterItem(FILTER_TITLE[i], IMAGES[i], mFileList.get(i) == null ? "" : mFileList.get(i).getAbsolutePath()));
        }
        return mItems;
    }
}
