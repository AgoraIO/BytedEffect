package io.agora.rtcwithbyte.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.agora.rtcwithbyte.adapter.ButtonViewRVAdapter;
import io.agora.rtcwithbyte.contract.ItemGetContract;
import io.agora.rtcwithbyte.contract.presenter.ItemGetPresenter;
import io.agora.rtcwithbyte.model.ButtonItem;
import io.agora.rtcwithbyte.model.ComposerNode;
import io.agora.rtcwithbyte.R;


public class MakeupOptionFragment
        extends BaseFeatureFragment<ItemGetContract.Presenter, MakeupOptionFragment.IMakeupOptionCallback>
        implements ButtonViewRVAdapter.OnItemClickListener, View.OnClickListener, ItemGetContract.View {
    private RecyclerView rv;
    private ImageView ivClose;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_makeup_option, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPresenter(new ItemGetPresenter());

        rv = view.findViewById(R.id.rv_makeup_option);
        ivClose = view.findViewById(R.id.iv_close_makeup_option);
        ivClose.setOnClickListener(this);

        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setMakeupType(final int type, final int select) {
        if (rv == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setMakeupType(type, select);
                }
            }, 100);
            return;
        }

        ButtonViewRVAdapter adapter;
        if (rv.getAdapter() == null) {
            adapter = new ButtonViewRVAdapter(mPresenter.getItems(type), this, select);
        } else {
            adapter = (ButtonViewRVAdapter) rv.getAdapter();
            adapter.setItemList(mPresenter.getItems(type));
            adapter.setSelect(select);
        }
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_makeup_option:
                getCallback().onCloseClick();
                break;
        }
    }

    @Override
    public void onItemClick(ButtonItem item) {
        getCallback().onOptionSelect(item.getNode(), ((ButtonViewRVAdapter)rv.getAdapter()).getSelect());
    }

    interface IMakeupOptionCallback {
        /**
         * 点击关闭按钮，收起当前的 MakeupOptionFragment
         */
        void onCloseClick();

        void onDefaultClick();

        /**
         * 点击某一项之后，回调给 EffectFragment 处理
         * @param node 点击项的 node
         * @param select 点击项所处位置
         */
        void onOptionSelect(ComposerNode node, int select);
    }
}
