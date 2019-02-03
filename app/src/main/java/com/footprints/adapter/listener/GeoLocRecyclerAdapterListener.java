package com.footprints.adapter.listener;

public interface GeoLocRecyclerAdapterListener<GeoData> extends BaseRecyclerAdapterListener<GeoData> {
    void delete(int position, GeoData data);

    void callShareIntent(GeoData data);
}
