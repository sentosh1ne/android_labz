package com.labz.base.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Stanislav on 15.02.17.
 */

public abstract class BaseRecyclerViewAdapter<M, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected List<M> mObjects;
    protected final Context context;
    protected boolean mIsLoading = false;


    public BaseRecyclerViewAdapter(Context context, List<M> objects) {
        this.context = context;
        mObjects = objects;

    }

    public List<M> getItems() {
        return mObjects;
    }

    public void setItems(List<M> objects) {
        setItems(objects, true);
    }

    public void setItems(List<M> objects, boolean notifyDataSetChanged) {
        if (mObjects != null) {
            mObjects.clear();
            if (objects != null)
                mObjects.addAll(objects);
        } else {
            mObjects = objects;
        }
        try {
            if (notifyDataSetChanged)
                notifyDataSetChanged();
        } catch (IllegalStateException ignore) {

        }
    }

    @Override
    public int getItemCount() {
        return mObjects != null ? mObjects.size() : 0;
    }

    @Nullable
    public M getItem(int position) {
        if (mObjects != null) {
            return mObjects.size() > position ? mObjects.get(position) : null;
        }
        return null;
    }

}
