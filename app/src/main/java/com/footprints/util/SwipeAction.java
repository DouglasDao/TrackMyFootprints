package com.footprints.util;

import android.support.v7.widget.RecyclerView;

public interface SwipeAction {
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);
}
