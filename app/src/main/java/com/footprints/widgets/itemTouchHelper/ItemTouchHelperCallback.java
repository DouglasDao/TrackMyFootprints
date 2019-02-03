package com.footprints.widgets.itemTouchHelper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.footprints.R;
import com.footprints.adapter.ViewHolder.GeoViewHolder;
import com.footprints.adapter.ViewHolder.ItemTouchViewHolder;

public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("ItemTouchHelperCallback", "Swiped");
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (dY != 0 && dX == 0)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        GeoViewHolder holder = (GeoViewHolder) viewHolder;
        View container = holder.itemView.findViewById(R.id.view_list_repo_action_container);
        View cardLay = holder.itemView.findViewById(R.id.swipeMenuLayout);

        if (viewHolder instanceof ItemTouchViewHolder) {
            if (dX < -container.getWidth()) {
                dX = -container.getWidth();
            }
            cardLay.setTranslationX(dX);
            return;
        }
        cardLay.setTranslationX(dX);
    }
}
