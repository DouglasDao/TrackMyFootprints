package com.footprints.adapter.ViewHolder;

import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.footprints.R;
import com.footprints.model.GeoData;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;

public class GeoViewHolder extends BaseViewHolder<GeoData> {

    @BindView(R.id.tv_geo_coordinates)
    AppCompatTextView mGeoCoordinates;
    @BindView(R.id.tv_place_name)
    AppCompatTextView mPlaceName;
    @BindView(R.id.iv_loc_pos)
    AppCompatTextView mId;
    @BindView(R.id.iv_loc_date)
    AppCompatTextView mLocationDateTime;

    public GeoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    void populateData() {
        try {
            if (data.getLatit() != null && data.getLongit() != null)
                mGeoCoordinates.setText(data.getLatit() + " , " + data.getLongit());
            if (data.getAddress() != null && !data.getAddress().isEmpty())
                mPlaceName.setText(data.getAddress());

            mId.setText("" + data.getId());
            mLocationDateTime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
        } catch (Exception e) {
            Log.e(TAG, "Error : " + e.getMessage());
        }
    }

}
