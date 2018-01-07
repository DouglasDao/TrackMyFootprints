package com.geo.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.geo.R;
import com.geo.adapter.ViewHolder.GeoViewHolder;
import com.geo.model.GeoData;

import java.util.List;

public class GeoAdapter extends BaseRecyclerAdapter<GeoData,GeoViewHolder>{

    public GeoAdapter(List<GeoData> data) {
        super(data);
    }

    @Override
    public GeoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GeoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_location_details, parent, false));
    }
}