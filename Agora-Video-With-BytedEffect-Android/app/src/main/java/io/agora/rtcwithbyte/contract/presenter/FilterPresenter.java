package io.agora.rtcwithbyte.contract.presenter;

import android.content.Context;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.agora.rtcwithbyte.ResourceHelper;
import io.agora.rtcwithbyte.contract.FilterContract;
import io.agora.rtcwithbyte.model.FilterItem;
import io.agora.rtcwithbyte.R;

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
                context.getString(R.string.filter_heibai)
        };
        List<File> mFileList = new ArrayList<>();
        mFileList.add(null);  // normal 正常
        mFileList.addAll(Arrays.asList(ResourceHelper.getFilterResources(context)));
        mFileList = mFileList.subList(0, 23);
        Collections.sort(mFileList, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                if (file == null)
                    return -1;
                if (t1 == null)
                    return 1;
                String s = file.getName();
                String s1 = t1.getName();
                s = s.substring(s.length() - 5, s.length() - 3);
                s1 = s1.substring(s1.length() - 5, s1.length() - 3);
                return Integer.valueOf(s) - Integer.valueOf(s1);
            }
        });

        for (int i = 0; i < 23; i++) {
            mItems.add(new FilterItem(FILTER_TITLE[i], IMAGES[i], mFileList.get(i) == null ? "" : mFileList.get(i).getAbsolutePath()));
        }
        return mItems;
    }
}
