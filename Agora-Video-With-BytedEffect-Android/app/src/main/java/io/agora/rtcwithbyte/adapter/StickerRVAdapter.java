package io.agora.rtcwithbyte.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.activities.ByteBaseActivity;
import io.agora.rtcwithbyte.activities.MainActivity;
import io.agora.rtcwithbyte.model.StickerItem;
import io.agora.rtcwithbyte.utils.CommonUtils;
import io.agora.rtcwithbyte.utils.ToasUtils;

import java.util.List;

import static io.agora.rtcwithbyte.contract.StickerContract.TYPE_STICKER;

public class StickerRVAdapter extends SelectRVAdapter<StickerRVAdapter.ViewHolder> {
    private List<StickerItem> mStickerList;
    private OnItemClickListener mListener;
    private ByteBaseActivity.ICheckAvailableCallback mCheckAvailableCallback;

    public StickerRVAdapter(List<StickerItem> stickers, OnItemClickListener listener) {
        mStickerList = stickers;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sticker, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final StickerItem item = mStickerList.get(position);

        if (mSelect == position) {
            holder.ll.setBackgroundResource(R.drawable.bg_item_select_selector);
        } else {
            holder.ll.setBackgroundResource(R.drawable.bg_item_unselect_selector);
        }

        holder.iv.setImageResource(item.getIcon());
        holder.tv.setText(item.getTitle());
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastClick()) {
                    ToasUtils.show("too fast click");
                    return;
                }
                if (mCheckAvailableCallback != null &&
                        !mCheckAvailableCallback.checkAvailable(TYPE_STICKER)) {
                    return;
                }
                if (mSelect != position) {
                    mListener.onItemClick(item);
                    setSelect(position);
                }
            }
        });
    }

    public void setSelectItem(String sticker) {
        for (int i = 0; i < mStickerList.size(); i++) {
            StickerItem item = mStickerList.get(i);
            if (sticker.equals(item.getResource())) {
                setSelect(i);
                return;
            }
        }
        setSelect(0);
    }

    public void setCheckAvailableCallback(ByteBaseActivity.ICheckAvailableCallback callback) {
        mCheckAvailableCallback = callback;
    }

    @Override
    public int getItemCount() {
        return mStickerList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(StickerItem item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll;
        ImageView iv;
        TextView tv;

        ViewHolder(View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.ll_item_sticker);
            iv = itemView.findViewById(R.id.iv_item_sticker);
            tv = itemView.findViewById(R.id.tv_item_sticker);
        }
    }
}
