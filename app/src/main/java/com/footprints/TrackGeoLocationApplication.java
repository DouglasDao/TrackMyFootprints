package com.footprints;

import android.app.Application;


public class TrackGeoLocationApplication extends Application {

    private static TrackGeoLocationApplication mAppController;

    public static TrackGeoLocationApplication getInstance() {
        return mAppController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindDao();
    }

    private void bindDao() {

    }

    public void ClearDao() {

    }

}
