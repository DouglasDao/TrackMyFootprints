package com.footprints;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
