package com.geo.adapter.ViewHolder;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.geo.R;
import com.geo.model.GeoData;

import butterknife.BindView;

public class GeoViewHolder extends BaseViewHolder<GeoData> {

    @BindView(R.id.tv_geo_coordinates)
    AppCompatTextView mGeoCoordinates;

    public GeoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void populateData() {
        mGeoCoordinates.setText(data.getLatit() + " , " + data.getLongit());
    }

}
