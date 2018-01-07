package com.geo.adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public T data;
    String TAG = getClass().getSimpleName();

    BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(T data) {
        this.data = data;
        populateData();
    }

    abstract void populateData();
}
