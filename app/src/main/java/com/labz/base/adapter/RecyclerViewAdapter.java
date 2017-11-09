package com.labz.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

abstract public class RecyclerViewAdapter<M, VH extends RecyclerView.ViewHolder>
        extends BaseRecyclerViewAdapter<M, VH> implements View.OnClickListener, View.OnLongClickListener {

    protected OnViewClickListener mOnViewClickListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private boolean clicksEnabled = true;

    public RecyclerViewAdapter(Context context) {
        super(context, new ArrayList<M>());
        setHasStableIds(true);
    }

    public RecyclerViewAdapter(Context context, List<M> list) {
        super(context, list);
        setHasStableIds(true);
    }

    public boolean isClicksEnabled() {
        return clicksEnabled;
    }

    public void setClicksEnabled(boolean enabled) {
        clicksEnabled = enabled;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.mOnViewClickListener = onViewClickListener;
    }

    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public final void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public void onClick(View v) {
        if (!clicksEnabled) return;

        VH vh = (VH) v.getTag();

        if (vh != null) {
            if (!(v.getParent() instanceof RecyclerView) && mOnViewClickListener != null) {
                mOnViewClickListener.onViewClick(v, vh.getLayoutPosition(), vh.getItemId());
            } else if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, vh.getLayoutPosition(), vh.getItemId());
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!clicksEnabled) return false;

        VH vh = (VH) v.getTag();
        return mOnItemLongClickListener != null
                && vh != null
                && mOnItemLongClickListener.onItemLongClick(v, vh.getLayoutPosition(), vh.getItemId());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
