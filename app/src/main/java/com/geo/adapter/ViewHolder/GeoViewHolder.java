package com.geo.adapter.ViewHolder;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.geo.R;
import com.geo.model.GeoData;

import butterknife.BindView;

public class GeoViewHolder extends BaseViewHolder<GeoData> {

    @BindView(R.id.tv_geo_coordinates)
    AppCompatTextView mGeoCoordinates;
    @BindView(R.id.tv_place_name)
    AppCompatTextView mPlaceName;

    public GeoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void populateData() {
        try {
            if (data.getLatit() != null && data.getLongit() != null)
                mGeoCoordinates.setText(data.getLatit() + " , " + data.getLongit());
            if (!data.getAddress().isEmpty())
                mPlaceName.setText(data.getAddress());
        } catch (Exception ignored) {

        }
    }

}
