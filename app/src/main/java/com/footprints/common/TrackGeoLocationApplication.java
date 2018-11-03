package com.footprints.common;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.footprints.common.customactivityoncrash.CustomActivityOnCrash;
import com.footprints.common.customactivityoncrash.config.CaocConfig;


public class TrackGeoLocationApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        CaocConfig.Builder.create().apply();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static class CustomEventListener implements CustomActivityOnCrash.EventListener {
        @Override
        public void onLaunchErrorActivity() {
            Log.i("TrackGeoLocation", "onLaunchErrorActivity()");
        }

        @Override
        public void onRestartAppFromErrorActivity() {
            Log.i("TrackGeoLocation", "onRestartAppFromErrorActivity()");
        }

        @Override
        public void onCloseAppFromErrorActivity() {
            Log.i("TrackGeoLocation", "onCloseAppFromErrorActivity()");
        }
    }
}
