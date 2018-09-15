package com.footprints.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.footprints.R;
import com.footprints.adapter.ViewHolder.GeoViewHolder;
import com.footprints.model.GeoData;

import java.util.List;

public class GeoAdapter extends BaseRecyclerAdapter<GeoData,GeoViewHolder>{

    public GeoAdapter(List<GeoData> data) {
        super(data);
    }

    @Override
    public GeoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GeoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_location_details, parent, false));
    }
}