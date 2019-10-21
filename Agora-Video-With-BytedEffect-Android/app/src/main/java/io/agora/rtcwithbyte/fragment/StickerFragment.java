package io.agora.rtcwithbyte.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;

import io.agora.rtcwithbyte.activities.ByteBaseActivity;
import io.agora.rtcwithbyte.activities.MainActivity;
import io.agora.rtcwithbyte.adapter.StickerRVAdapter;
import io.agora.rtcwithbyte.contract.StickerContract;
import io.agora.rtcwithbyte.contract.presenter.StickerPresenter;
import io.agora.rtcwithbyte.model.StickerItem;
import io.agora.rtcwithbyte.utils.ToasUtils;
import io.agora.rtcwithbyte.R;

public class StickerFragment extends BaseFeatureFragment<StickerContract.Presenter, StickerFragment.IStickerCallback>
        implements StickerRVAdapter.OnItemClickListener, ByteBaseActivity.OnCloseListener, StickerContract.View {
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rv = (RecyclerView) inflater.inflate(R.layout.fragment_sticker, container, false);
        return rv;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPresenter(new StickerPresenter());

        StickerRVAdapter adapter = new StickerRVAdapter(mPresenter.getItems(), this);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(StickerItem item) {
        if (item.hasTip()) {
            ToasUtils.show(item.getTip());
        }
        getCallback().onStickerSelected(new File(item.getResource()));
    }

    @Override
    public void onClose() {
        getCallback().onStickerSelected(null);

        ((StickerRVAdapter)rv.getAdapter()).setSelect(0);
    }

    public interface IStickerCallback {
        void onStickerSelected(File file);
    }
}
