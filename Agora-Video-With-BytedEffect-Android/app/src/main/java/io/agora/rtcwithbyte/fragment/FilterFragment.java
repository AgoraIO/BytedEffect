// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package io.agora.rtcwithbyte.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;

import io.agora.rtcwithbyte.activities.ByteBaseActivity;
import io.agora.rtcwithbyte.adapter.FilterRVAdapter;
import io.agora.rtcwithbyte.contract.FilterContract;
import io.agora.rtcwithbyte.contract.presenter.FilterPresenter;
import io.agora.rtcwithbyte.R;

/**
 * 滤镜
 */
public class FilterFragment extends BaseFeatureFragment<FilterContract.Presenter, FilterFragment.IFilterCallback>
        implements FilterRVAdapter.OnItemClickListener,
        ByteBaseActivity.OnCloseListener, FilterContract.View {
    private RecyclerView rv;

    public interface IFilterCallback {
        void onFilterSelected(File file);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rv = (RecyclerView) View.inflate(getContext(), R.layout.fragment_filter, null);
        return rv;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPresenter(new FilterPresenter());

        FilterRVAdapter adapter = new FilterRVAdapter(mPresenter.getItems(), this);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(File file) {
        getCallback().onFilterSelected(file);
    }

    @Override
    public void onClose() {
        ((FilterRVAdapter)rv.getAdapter()).setSelect(0);
    }
}
