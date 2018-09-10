package com.geo.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeItem extends ItemTouchHelper.SimpleCallback {

    private SwipeAction mSwipeAction;

    public SwipeItem(int dragDirs, int swipeDirs, SwipeAction mSwipeAction) {
        super(dragDirs, swipeDirs);
        this.mSwipeAction = mSwipeAction;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return mSwipeAction.onMove(recyclerView, viewHolder, target);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mSwipeAction.onSwiped(viewHolder, direction);
    }
}
