package com.footprints.adapter.ViewHolder;

import android.view.View;

import com.footprints.R;
import com.footprints.widgets.itemTouchHelper.Extension;

public class ItemTouchViewHolder extends GeoViewHolder implements Extension {

    public View mActionViewDelete;
    public View mActionViewRefresh;

    int actionWidth = 480;

    public ItemTouchViewHolder(View itemView) {
        super(itemView);
        mActionViewDelete = itemView.findViewById(R.id.iv_delete);
        mActionViewRefresh = itemView.findViewById(R.id.iv_share);

    }

    @Override
    public float getActionWidth() {
        return actionWidth;
    }
}