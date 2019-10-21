package io.agora.rtcwithbyte.adapter;

import android.support.v7.widget.RecyclerView;

abstract public class SelectRVAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected int mSelect;

    public void setSelect(int select) {
        if (mSelect != select) {
            int oldSelect = mSelect;
            mSelect = select;
            notifyItemChanged(oldSelect);
            notifyItemChanged(select);
        }
    }

    public int getSelect() {
        return mSelect;
    }
}
