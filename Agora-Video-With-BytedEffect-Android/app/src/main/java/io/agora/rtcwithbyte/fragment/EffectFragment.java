package io.agora.rtcwithbyte.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.activities.ByteBaseActivity;
import io.agora.rtcwithbyte.activities.MainActivity;
import io.agora.rtcwithbyte.adapter.FragmentVPAdapter;
import io.agora.rtcwithbyte.adapter.OnPageChangeListenerAdapter;
import io.agora.rtcwithbyte.contract.EffectContract;
import io.agora.rtcwithbyte.contract.ItemGetContract;
import io.agora.rtcwithbyte.contract.presenter.EffectPresenter;
import io.agora.rtcwithbyte.model.ButtonItem;
import io.agora.rtcwithbyte.model.ComposerNode;
import io.agora.rtcwithbyte.view.ProgressBar;
import library.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.agora.rtcwithbyte.contract.ItemGetContract.MASK;
import static io.agora.rtcwithbyte.contract.ItemGetContract.OFFSET;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_BODY;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_FACE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_BEAUTY_RESHAPE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_CLOSE;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_FILTER;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_HAIR;
import static io.agora.rtcwithbyte.contract.ItemGetContract.TYPE_MAKEUP_OPTION;

public class EffectFragment extends
        BaseFeatureFragment<EffectContract.Presenter, EffectFragment.IEffectCallback>
        implements ByteBaseActivity.OnCloseListener, MakeupOptionFragment.IMakeupOptionCallback,
        EffectContract.View, ItemGetContract.View, View.OnClickListener {
    public static final int POSITION_BEAUTY = 0;
    public static final int POSITION_RESHAPE = 1;
    public static final int POSITION_BODY = 2;
    public static final int POSITION_MAKEUP = 3;
    public static final int POSITION_FILTER = 4;

    public static final String TAG_MAKEUP_OPTION_FRAGMENT = "makeup_option";
    public static final int ANIMATION_DURATION = 400;

    public static final float NO_VALUE = -1F;
    public static final int NO_POSITION = -1;

    // view
    private ProgressBar pb;
//    private ImageView ivNormal;
//    private ImageView ivDefault;
    private Button btnNormal;
    private Button btnDefault;
    private TabLayout tl;
    private TextView tvTitle;
    private ImageView ivCloseMakeupOption;
    private ViewPager vp;

    private List<Fragment> mFragmentList;
    // 当前选择的效果类型，如磨皮等
    // current effect type
    private int mSelectType = TYPE_CLOSE;
    private int mSavedSelectType;
    // current fragment
    private IProgressCallback mSelectFragment;
    // 效果强度表
    private SparseArray<Float> mProgressMap = new SparseArray<>();
    // 每一个 Fragment 中选中的效果
    // Selected effect in each Fragment
    private SparseIntArray mTypeMap = new SparseIntArray();
    // 每一个美妆效果选中的类型，如口红的胡萝卜红
    // Each selected makeup effect of selected type
    private SparseIntArray mMakeupOptionSelectMap = new SparseIntArray();
    // 所有选中的效果集合
    // all selected effect
    private SparseArray<ComposerNode> mComposerNodeMap = new SparseArray<>();

    private String mSavedFilterPath;
    private ByteBaseActivity.ICheckAvailableCallback mCheckAvailableCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effect, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setVisibility(View.GONE);
        setPresenter(new EffectPresenter());

        pb = view.findViewById(R.id.pb_effect);
//        ivNormal = view.findViewById(R.id.iv_normal_effect);
//        ivDefault = view.findViewById(R.id.iv_default_beauty);
        btnNormal = view.findViewById(R.id.btn_normal);
        btnDefault = view.findViewById(R.id.btn_default);
        tl = view.findViewById(R.id.tl_identify);
        tvTitle = view.findViewById(R.id.tv_title_identify);
        ivCloseMakeupOption = view.findViewById(R.id.iv_close_makeup_option);
        vp = view.findViewById(R.id.vp_identify);

        pb.setOnProgressChangedListener(new ProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(ProgressBar progressBar, float progress, boolean isFormUser) {
                if (isFormUser) {
                    dispatchProgress(progress);
                }
            }
        });

        btnNormal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onNormalDown();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        onNormalUp();
                        break;
                }
                return false;
            }
        });
        btnDefault.setOnClickListener(this);
        ivCloseMakeupOption.setOnClickListener(this);
        initVP();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (EffectFragment.this != null) {
                    EffectFragment.this.onDefaultClick();
                }
            }
        });
    }

    private void initVP() {
        mFragmentList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();

        // 美颜
        // beauty face
        mFragmentList.add(new BeautyFaceFragment().setType(TYPE_BEAUTY_FACE)
                .setCheckAvailableCallback(mCheckAvailableCallback).setCallback(new BeautyFaceFragment.IBeautyCallBack() {
            @Override
            public void onBeautySelect(ButtonItem item) {
                int type = item.getNode().getId();
                mSelectType = type;
                mTypeMap.put(POSITION_BEAUTY, mSelectType);
                if (type == TYPE_CLOSE) {
                    closeBeautyFace();
                    return;
                }

                if (mComposerNodeMap.get(type) == null) {
                    mComposerNodeMap.put(type, item.getNode());
                    updateComposerNodes();
                }
                float progress = mProgressMap.get(type, NO_VALUE);
                if (progress == NO_VALUE) {
                    dispatchProgress(mPresenter.getDefaultValue(mSelectType));
                } else {
                    dispatchProgress(progress);
                }
            }
        }));
        titleList.add(getString(R.string.tab_face_beautification));

        // 美形
        // beauty reshape
        mFragmentList.add(new BeautyFaceFragment().setType(TYPE_BEAUTY_RESHAPE)
                .setCheckAvailableCallback(mCheckAvailableCallback).setCallback(new BeautyFaceFragment.IBeautyCallBack() {
            @Override
            public void onBeautySelect(ButtonItem item) {
                int type = item.getNode().getId();
                mSelectType = type;
                mTypeMap.put(POSITION_RESHAPE, mSelectType);
                if (type == TYPE_CLOSE) {
                    closeBeautyReshape();
                    return;
                }

                if (mComposerNodeMap.get(type) == null) {
                    mComposerNodeMap.put(type, item.getNode());
                    updateComposerNodes();
                }
                float progress = mProgressMap.get(type, NO_VALUE);
                if (progress == NO_VALUE) {
                    dispatchProgress(mPresenter.getDefaultValue(mSelectType));
                } else {
                    dispatchProgress(progress);
                }
            }
        }));
        titleList.add(getString(R.string.tab_face_beauty_reshape));

        // 美体
        // beauty body
        mFragmentList.add(new BeautyFaceFragment().setType(TYPE_BEAUTY_BODY)
                .setCheckAvailableCallback(mCheckAvailableCallback).setCallback(new BeautyFaceFragment.IBeautyCallBack() {
            @Override
            public void onBeautySelect(ButtonItem item) {
                int type = item.getNode().getId();
                mSelectType = type;
                mTypeMap.put(POSITION_BODY, mSelectType);
                if (type == TYPE_CLOSE) {
                    closeBeautyBody();
                    return;
                }

                if (mComposerNodeMap.get(type) == null) {
                    mComposerNodeMap.put(type, item.getNode());
                    updateComposerNodes();
                }
                dispatchProgress(mPresenter.getDefaultValue(mSelectType));
            }
        }));
        titleList.add(getString(R.string.tab_face_beauty_body));

        // 美妆
        // make
        mFragmentList.add(new BeautyFaceFragment().setType(TYPE_MAKEUP)
                .setCheckAvailableCallback(mCheckAvailableCallback).setCallback(new BeautyFaceFragment.IBeautyCallBack() {
            @Override
            public void onBeautySelect (ButtonItem item) {
                mSelectType = item.getNode().getId();
                mTypeMap.put(POSITION_MAKEUP, mSelectType);
                if (mSelectType == TYPE_CLOSE) {
                    closeMakeup();
                    return;
                }

                // 染发栏不显示滑杆
                if (item.getNode().getId() == TYPE_MAKEUP_HAIR) {
                    pb.setVisibility(View.INVISIBLE);
                } else {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(mProgressMap.get(mSelectType, 0F));
                }

                tvTitle.setText(item.getTitle());
                showOrHideMakeupOptionFragment(true);
            }
        }));
        titleList.add(getString(R.string.tab_face_makeup));
        // 滤镜
        // filter
        mFragmentList.add(new FilterFragment()
                .setCheckAvailableCallback(mCheckAvailableCallback)
                .setCallback(new FilterFragment.IFilterCallback() {
            @Override
            public void onFilterSelected(File file) {
                mSelectType = TYPE_FILTER;
                mTypeMap.put(POSITION_FILTER, TYPE_FILTER);
                mSavedFilterPath = file == null ? null : file.getAbsolutePath();
                if (getCallback() == null) {
                    return;
                }
                getCallback().onFilterSelected(file);
                // 选中滤镜之后初始化强度
                dispatchProgress(mPresenter.getDefaultValue(mSelectType));
            }
        }));
        titleList.add(getString(R.string.tab_filter));

        mSelectFragment = (IProgressCallback) mFragmentList.get(0);
        FragmentVPAdapter adapter = new FragmentVPAdapter(getChildFragmentManager(),
                mFragmentList, titleList);
        vp.setAdapter(adapter);
        vp.setOffscreenPageLimit(mFragmentList.size());
        vp.addOnPageChangeListener(new OnPageChangeListenerAdapter() {
            @Override
            public void onPageSelected(int position) {
                mSelectType = mTypeMap.get(position, TYPE_CLOSE);
                pb.setProgress(mProgressMap.get(mSelectType, NO_VALUE));

                if (mFragmentList.get(position) instanceof IProgressCallback) {
                    mSelectFragment = (IProgressCallback) mFragmentList.get(position);
                }

//                showOrHideProgressBar(position != POSITION_MAKEUP && position != POSITION_BODY);
                showOrHideProgressBar(position != POSITION_BODY);
            }
        });
        tl.setupWithViewPager(vp);
    }

    public EffectFragment setCheckAvailableCallback(ByteBaseActivity.ICheckAvailableCallback callback) {
        mCheckAvailableCallback = callback;
        return this;
    }

    /**
     * 将进度分发出去，有两个出口
     * 1、分到对应的 Fragment 中供其更改 UI
     * 2、传递给 Callback 供 EffectRenderHelper 渲染
     * @param progress 进度，0～1
     */
    private void dispatchProgress(float progress) {
        if (mSelectType < 0) return;

        if (mSelectFragment != null) {
            mSelectFragment.onProgress(progress, mSelectType);
        }

        if (pb.getProgress() != progress) {
            pb.setProgress(progress);
        }

        if (mSelectType == TYPE_FILTER) {
            if (getCallback() == null) {
                return;
            }
            mProgressMap.put(TYPE_FILTER, progress);
            getCallback().onFilterValueChanged(progress);
        } else {
            if ((mSelectType & MASK) == TYPE_BEAUTY_FACE ||
                    (mSelectType & MASK) == TYPE_BEAUTY_RESHAPE ||
                    (mSelectType & MASK) == TYPE_MAKEUP_OPTION) {
                mProgressMap.put(mSelectType, progress);
            }

            // 从 mComposerNodeMap 中取 node
            ComposerNode node = mComposerNodeMap.get(mSelectType);
            if (node == null) {
                LogUtils.e("composer node must be added in mComposerNodeMap before, " +
                        "node not found: " + mSelectType + ", map: " + mComposerNodeMap.toString());
                return;
            }
            node.setValue(progress);
            updateNodeIntensity(node);
        }
    }

    @Override
    public void onClose() {
        // 关闭美颜、美妆、滤镜效果
        if (getCallback() == null) {
            return;
        }
        getCallback().updateComposeNodes(new String[0]);
        getCallback().onFilterSelected(null);

        // 清空缓存数据
//        mProgressMap.clear();
//        mMakeupOptionSelectMap.clear();
//        mComposerNodeMap.clear();
//        mTypeMap.clear();
        mSavedSelectType = mSelectType;
        mSelectType = TYPE_CLOSE;

        // 重置 view
        vp.setCurrentItem(0);
        pb.setProgress(0);

        // 重置 MakeupOptionFragment
        MakeupOptionFragment fragment = (MakeupOptionFragment) getChildFragmentManager()
                .findFragmentByTag(TAG_MAKEUP_OPTION_FRAGMENT);
        if (fragment != null) {
            showOrHideMakeupOptionFragment(false);
        }

        // 调用子 View onClose
        for (Fragment f : mFragmentList) {
            if (f instanceof ByteBaseActivity.OnCloseListener) {
                ((ByteBaseActivity.OnCloseListener) f).onClose();
            }
        }
    }

    @Override
    public void onOptionSelect(ComposerNode node, int select) {
        // 记录当前选择并更新 UI
        mMakeupOptionSelectMap.put(mSelectType, select);
        BeautyFaceFragment fragment = (BeautyFaceFragment) mFragmentList.get(POSITION_MAKEUP);
        fragment.onProgress(select == 0 ? 0 : 1);

        int lastType = mSelectType;
        mSelectType = node.getId();

        // 关闭按钮
        if (mSelectType == TYPE_CLOSE) {
            mComposerNodeMap.remove(lastType);
            mProgressMap.remove(lastType);
            pb.setProgress(0);

            updateComposerNodes();
            return;
        }

        mComposerNodeMap.put(mSelectType, node);
        updateComposerNodes();

        float progress = mProgressMap.get(mSelectType, mPresenter.getDefaultValue(mSelectType));
//        float progress = mPresenter.getDefaultValue(mSelectType);
        dispatchProgress(progress);
    }

    /**
     * 默认按钮点击之后，需要将所有的值都设置为默认给定的值，其间主要需要解决三个问题
     * 1。 各功能强度值变动之后，需要更改各 item 的标志点
     * 2。 修改到默认值后，需要回到原来的状态（原来选中的按钮依旧选中，进度条依旧指示当前选中的按钮）
     * 3。 不能影响没有强度或不参与的功能（美体、美妆）
     */
    @Override
    public void onDefaultClick() {
        if (getCallback() == null) return;
        getCallback().onDefaultClick();

        int currentType = mSelectType;
        IProgressCallback currentFragment = mSelectFragment;
        float currentProgress = pb.getProgress();
        int selectedFace = mTypeMap.get(POSITION_BEAUTY, TYPE_CLOSE);
        int selectedReshape = mTypeMap.get(POSITION_RESHAPE, TYPE_CLOSE);

        // close beauty body and makeup when set default
        ((BeautyFaceFragment)mFragmentList.get(POSITION_BODY)).onClose();
        ((BeautyFaceFragment)mFragmentList.get(POSITION_MAKEUP)).onClose();
        ((FilterFragment)mFragmentList.get(POSITION_FILTER)).onClose();
        getCallback().onFilterSelected(null);
        mMakeupOptionSelectMap.clear();
        showOrHideMakeupOptionFragment(false);
        mPresenter.removeProgressInMap(mProgressMap, TYPE_MAKEUP_OPTION);

        mComposerNodeMap.clear();
        mPresenter.generateDefaultBeautyNodes(mComposerNodeMap);
        updateComposerNodes();
        for (int i = 0; i < mComposerNodeMap.size(); i++) {
            ComposerNode node = mComposerNodeMap.valueAt(i);
            if (mPresenter.hasIntensity(node.getId())) {
                mSelectType = node.getId();
                mSelectFragment = getFragmentWithType(mSelectType);
                dispatchProgress(node.getValue());

                if (mSelectType == currentType) {
                    currentProgress = node.getValue();
                }
            }
        }

        if (selectedFace == TYPE_CLOSE) {
            ((IProgressCallback)mFragmentList.get(POSITION_BEAUTY)).setSelect(-1);
        } else {
            ((IProgressCallback)mFragmentList.get(POSITION_BEAUTY)).setSelectItem(selectedFace);
        }
        if (selectedReshape == TYPE_CLOSE) {
            ((IProgressCallback)mFragmentList.get(POSITION_RESHAPE)).setSelect(-1);
        } else {
            ((IProgressCallback)mFragmentList.get(POSITION_RESHAPE)).setSelectItem(selectedReshape);
        }
        pb.setProgress(currentProgress);
        mSelectType = currentType;
        mSelectFragment = currentFragment;
    }

    public void recoverState() {
        mSelectType = mSavedSelectType;
        int currentType = mSelectType;
        IProgressCallback currentFragment = mSelectFragment;
        float currentProgress = pb.getProgress();
        int selectedFace = mTypeMap.get(POSITION_BEAUTY, TYPE_CLOSE);
        int selectedReshape = mTypeMap.get(POSITION_RESHAPE, TYPE_CLOSE);
        int selectedBody = mTypeMap.get(POSITION_BODY, TYPE_CLOSE);
        int selectedMakeup = mTypeMap.get(POSITION_MAKEUP, TYPE_CLOSE);
        boolean hasBeauty = false, hasReshape = false;

        // close beauty body and makeup when set default
//        ((BeautyFaceFragment)mFragmentList.get(POSITION_BODY)).onClose();
//        ((BeautyFaceFragment)mFragmentList.get(POSITION_MAKEUP)).onClose();
//        ((FilterFragment)mFragmentList.get(POSITION_FILTER)).onClose();
//        mMakeupOptionSelectMap.clear();
//        showOrHideMakeupOptionFragment(false);
//        mPresenter.removeProgressInMap(mProgressMap, TYPE_MAKEUP_OPTION);

//        mComposerNodeMap.clear();
//        mPresenter.generateDefaultBeautyNodes(mComposerNodeMap);
        updateComposerNodes();
        for (int i = 0; i < mComposerNodeMap.size(); i++) {
            ComposerNode node = mComposerNodeMap.valueAt(i);
            if (mPresenter.hasIntensity(node.getId())) {
                mSelectType = node.getId();
                mSelectFragment = getFragmentWithType(mSelectType);
                dispatchProgress(node.getValue());
                if ((mSelectType & MASK) == TYPE_BEAUTY_FACE && node.getValue() > 0) {
                    hasBeauty = true;
                } else if ((mSelectType & MASK) == TYPE_BEAUTY_RESHAPE && node.getValue() > 0) {
                    hasReshape = true;
                }

                if (mSelectType == currentType) {
                    currentProgress = node.getValue();
                }
            }
        }

//        if (selectedFace == TYPE_CLOSE) {
//            ((IProgressCallback)mFragmentList.get(POSITION_BEAUTY)).setSelect(-1);
//        }
        if (selectedFace == TYPE_CLOSE && hasBeauty) {
            selectedFace = 0;
        }
        ((IProgressCallback)mFragmentList.get(POSITION_BEAUTY)).setSelectItem(selectedFace);
//        if (selectedReshape == TYPE_CLOSE) {
//            ((IProgressCallback)mFragmentList.get(POSITION_RESHAPE)).setSelect(-1);
//        }
        if (selectedReshape == TYPE_CLOSE && hasReshape) {
            selectedReshape = 0;
        }
        ((IProgressCallback)mFragmentList.get(POSITION_RESHAPE)).setSelectItem(selectedReshape);
//        if (selectedBody == TYPE_CLOSE) {
//            ((IProgressCallback)mFragmentList.get(POSITION_BODY)).setSelect(-1);
//        }
        ((IProgressCallback)mFragmentList.get(POSITION_BODY)).setSelectItem(selectedBody);
//        if (selectedMakeup == TYPE_CLOSE) {
//            ((IProgressCallback)mFragmentList.get(POSITION_MAKEUP)).setSelect(-1);
//        }
        ((IProgressCallback)mFragmentList.get(POSITION_MAKEUP)).setSelectItem(selectedMakeup);
        pb.setProgress(currentProgress);
        mSelectType = currentType;
        mSelectFragment = currentFragment;

        if (mSavedFilterPath != null) {
            getCallback().onFilterSelected(mSavedFilterPath == null ? null : new File(mSavedFilterPath));
            getCallback().onFilterValueChanged(mProgressMap.get(TYPE_FILTER));
            ((FilterFragment)mFragmentList.get(POSITION_FILTER)).setSelectItem(mSavedFilterPath);
        }
    }

    private void updateComposerNodes() {
        if (getCallback() == null) {
            return;
        }
        getCallback().updateComposeNodes(mPresenter.generateComposerNodes(mComposerNodeMap));
    }

    private void updateNodeIntensity(ComposerNode node) {
        if (getCallback() == null) {
            return;
        }
        getCallback().updateComposeNodeIntensity(node);
    }

    private void closeBeautyFace() {
        mPresenter.removeNodesOfType(mComposerNodeMap, TYPE_BEAUTY_FACE);
        mPresenter.removeProgressInMap(mProgressMap, TYPE_BEAUTY_FACE);
        updateComposerNodes();

        pb.setProgress(0);

        // 调用子 Fragment 关闭 UI
        ByteBaseActivity.OnCloseListener listener = (ByteBaseActivity.OnCloseListener) mFragmentList.get(POSITION_BEAUTY);
        listener.onClose();
    }

    private void closeBeautyBody() {
        mPresenter.removeNodesOfType(mComposerNodeMap, TYPE_BEAUTY_BODY);
        updateComposerNodes();
        // 调用子 Fragment 关闭 UI
        ByteBaseActivity.OnCloseListener listener = (ByteBaseActivity.OnCloseListener) mFragmentList.get(POSITION_BODY);
        listener.onClose();
    }

    private void closeBeautyReshape() {
        mPresenter.removeNodesOfType(mComposerNodeMap, TYPE_BEAUTY_RESHAPE);
        mPresenter.removeProgressInMap(mProgressMap, TYPE_BEAUTY_RESHAPE);
        updateComposerNodes();

        pb.setProgress(0);

        ByteBaseActivity.OnCloseListener listener = (ByteBaseActivity.OnCloseListener) mFragmentList.get(POSITION_RESHAPE);
        listener.onClose();
    }

    private void closeMakeup() {
        mPresenter.removeNodesOfType(mComposerNodeMap, TYPE_MAKEUP_OPTION);
        mPresenter.removeProgressInMap(mProgressMap, TYPE_MAKEUP);
        mPresenter.removeProgressInMap(mProgressMap, TYPE_MAKEUP_OPTION);
        updateComposerNodes();

        pb.setProgress(0);
        mMakeupOptionSelectMap.clear();

        ByteBaseActivity.OnCloseListener listener = (ByteBaseActivity.OnCloseListener) mFragmentList.get(POSITION_MAKEUP);
        listener.onClose();
    }

    private void showOrHideProgressBar(boolean isShow) {
        pb.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 显示 or 隐藏 MakeupOptionFragment，在没有实例的情况下会先初始化一个实例
     * 显示一个 MakeupOptionFragment 的时候还会设置其默认选择位置，这个位置保存在
     * {@link this#mMakeupOptionSelectMap} 中
     * @param isShow 是否显示
     */
    private void showOrHideMakeupOptionFragment(boolean isShow) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
        Fragment makeupOptionFragment = manager.findFragmentByTag(TAG_MAKEUP_OPTION_FRAGMENT);

        if (isShow) {
            tl.setVisibility(View.GONE);
            vp.setVisibility(View.GONE);
            ivCloseMakeupOption.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
            ivCloseMakeupOption.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
            if (makeupOptionFragment == null) {
                makeupOptionFragment = generateMakeupOptionFragment();
                ((MakeupOptionFragment)makeupOptionFragment).setMakeupType(mSelectType, mMakeupOptionSelectMap.get(mSelectType, 0));

                transaction.add(R.id.fl_identify, makeupOptionFragment, TAG_MAKEUP_OPTION_FRAGMENT).commit();
            } else {
                ((MakeupOptionFragment)makeupOptionFragment).setMakeupType(mSelectType, mMakeupOptionSelectMap.get(mSelectType, 0));
                transaction.show(makeupOptionFragment).commit();
            }
        } else {
            if (makeupOptionFragment == null) return;
            transaction.hide(makeupOptionFragment).commit();
            tvTitle.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
            ivCloseMakeupOption.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvTitle.setVisibility(View.GONE);
                    ivCloseMakeupOption.setVisibility(View.GONE);
                    tl.setVisibility(View.VISIBLE);
                    vp.setVisibility(View.VISIBLE);
                }
            }, ANIMATION_DURATION);
        }
    }

    private Fragment generateMakeupOptionFragment() {
        return new MakeupOptionFragment().setCallback(this);
    }

    /**
     * 对比按钮按下，关闭无美颜美妆
     */
    private void onNormalDown() {
        if (getCallback() == null) {
            return;
        }
        getCallback().setEffectOn(false);
    }

    /**
     * 对比按钮松开，恢复美颜美妆
     */
    private void onNormalUp() {
        if (getCallback() == null) {
            return;
        }
        getCallback().setEffectOn(true);
    }

    private IProgressCallback getFragmentWithType(int type) {
        int index = ((type & MASK) >> OFFSET) - 1;
        // TYPE_MAKEUP_OPTION 映射到 TYPE_MAKEUP
        if (index == 5) index = 3;
        return index < mFragmentList.size() ? (IProgressCallback) mFragmentList.get(index) : null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_default:
                onDefaultClick();
                break;
            case R.id.iv_close_makeup_option:
                showOrHideMakeupOptionFragment(false);
                break;
        }
    }

    /**
     * 用户手动调节 ProgressBar 之后，由此回调至各功能 Fragment 调整 UI
     */
    public interface IProgressCallback {
        /**
         * 用于进度更改时的回调，将进度分发到子 Fragment 中
         * @param progress 进度
         */
        void onProgress(float progress);

        void onProgress(float progress, int id);

        int getSelect();

        void setSelect(int select);

        void setSelectItem(int id);
    }

    public interface IEffectCallback {
        /**
         * 更新美妆美颜设置
         * @param nodes 字符串数组，存储所有设置的美颜内容，当 node 长度为 0 时意为关闭美妆
         */
        void updateComposeNodes(String[] nodes);

        /**
         * 更新某一个效果的强度
         * @param node 效果对应 ComposerNode
         */
        void updateComposeNodeIntensity(ComposerNode node);

        // 滤镜
        void onFilterSelected(File file);
        void onFilterValueChanged(float cur);

        /**
         * 设置是否处理特效
         * @param isOn if false，则在处理纹理的时候不使用 RenderManager 处理原始纹理，则不会有效果
         */
        void setEffectOn(boolean isOn);

        void onDefaultClick();
    }
}
