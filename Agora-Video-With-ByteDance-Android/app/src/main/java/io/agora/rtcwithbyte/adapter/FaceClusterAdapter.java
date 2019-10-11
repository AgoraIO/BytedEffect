package io.agora.rtcwithbyte.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.utils.ToasUtils;

import java.util.List;

public class FaceClusterAdapter extends RecyclerView.Adapter<FaceClusterAdapter.ViewHolder> {
    private static final int DEFAULT_INDEX = -1;

    private List<String> mChooseList;               //Images selected to do clustering选择聚类图片
    private List<List<String>> mClusterResultList;  //The result of clustering 聚类结果
    private int mCurIndex = DEFAULT_INDEX;          //The currently cluster category当前查看聚类类别
    private OnItemClickListener mClickListener;
    private Context mContext;
    private int mClusterNums;

    public FaceClusterAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.face_cluster_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mTv.setText("");
        holder.mRoot.setOnClickListener(null);
        if (mClusterResultList != null){
            if (DEFAULT_INDEX == mCurIndex ){
                Glide.with(mContext)
                        .load(mClusterResultList.get(position).get(0))
                        .into(holder.mIv);
                if (position >= mClusterNums){
                    holder.mTv.setText(mContext.getString(R.string.face_cluster_null_class_name));
                }else {
                    holder.mTv.setText(mContext.getString(R.string.face_cluster_class_name) + position);
                }
                holder.mRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCluster(position);
                    }
                });
            }else{
                Glide.with(mContext)
                        .load(mClusterResultList.get(mCurIndex).get(position))
                        .into(holder.mIv);
            }
        }else {
            Glide.with(mContext)
                    .load(mChooseList.get(position))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mChooseList.remove(position);
                            ToasUtils.show("failed to show image, delete");
                            notifyDataSetChanged();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.mIv);
        }

    }

    @Override
    public int getItemCount() {
        if (mClusterResultList != null){
            if (DEFAULT_INDEX != mCurIndex){
                return mClusterResultList.get(mCurIndex).size();
            }
            return mClusterResultList.size();
        }
        return mChooseList == null ? 0 : mChooseList.size();
    }

    public void setClusterResultList(List<List<String>> data, int clusterNums) {
        mClusterResultList = data;
        mClusterNums = clusterNums;
        notifyDataSetChanged();
    }

    public void setOpenCluserListener(OnItemClickListener listener){
        mClickListener = listener;
    }

    public void setChooseList(List<String> data) {
        mChooseList = data;
        notifyDataSetChanged();
    }

    private void openCluster(int pos){
        mCurIndex = pos;
        mClickListener.onOpenCluster();
        notifyDataSetChanged();
    }

    public void resetCluster(){
        mCurIndex = DEFAULT_INDEX;
        notifyDataSetChanged();
    }

    public void clear() {
        mChooseList = null;
        mClusterResultList = null;
        mCurIndex = DEFAULT_INDEX;
        mClickListener = null;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mRoot;
        ImageView mIv;
        TextView mTv;
        ViewHolder(View itemView) {
            super(itemView);
            mIv = itemView.findViewById(R.id.iv_cluster_item);
            mTv = itemView.findViewById(R.id.tv_cluster_item);
            mRoot = itemView.findViewById(R.id.ll_cluster_item);
        }
    }

    public interface OnItemClickListener {
        void onOpenCluster();
    }
}
