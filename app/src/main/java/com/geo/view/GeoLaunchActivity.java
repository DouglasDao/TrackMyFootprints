package com.geo.view;


import android.os.Bundle;
import android.util.Log;

import com.geo.R;

public class GeoLaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Geo","GeoLaunchActi");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }
}
