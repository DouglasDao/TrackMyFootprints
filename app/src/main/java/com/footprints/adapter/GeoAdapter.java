package com.footprints.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.footprints.R;
import com.footprints.adapter.ViewHolder.GeoViewHolder;
import com.footprints.adapter.ViewHolder.ItemTouchViewHolder;
import com.footprints.adapter.listener.GeoLocRecyclerAdapterListener;
import com.footprints.model.GeoData;
import com.footprints.widgets.itemTouchHelper.ItemTouchHelperExtension;

import java.util.List;

public class GeoAdapter extends BaseRecyclerAdapter<GeoData, GeoViewHolder> {

    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    List<GeoData> data;
    private GeoLocRecyclerAdapterListener<GeoData> listener;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public GeoAdapter(List<GeoData> data, GeoLocRecyclerAdapterListener<GeoData> listener) {
        super(data);
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE_ACTION_WIDTH;
    }

    @NonNull
    @Override
    public GeoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_location_details, parent, false);
        if (viewType == ITEM_TYPE_ACTION_WIDTH) return new ItemTouchViewHolder(view);
        return new GeoViewHolder(view, listener, mItemTouchHelperExtension);
    }

    @Override
    public void onBindViewHolder(@NonNull GeoViewHolder holder, int position) throws IndexOutOfBoundsException {
        super.onBindViewHolder(holder, position);


        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mItemTouchHelperExtension.startDrag(holder);
                }
                return true;
            }
        });

        ItemTouchViewHolder touchViewHolder = (ItemTouchViewHolder) holder;

        holder.itemView.findViewById(R.id.swipeMenuLayout).setOnClickListener(view -> holder.popLocationDetails(listener, position));

        touchViewHolder.mActionViewRefresh.setOnClickListener(view -> holder.getShareIntent(listener,position));

        touchViewHolder.mActionViewDelete.setOnClickListener(view -> holder.deleteLocation(listener, position));

    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }
}