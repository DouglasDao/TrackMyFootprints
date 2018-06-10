package com.geo.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.geo.R;
import com.geo.adapter.GeoAdapter;
import com.geo.model.GeoData;
import com.geo.view.iview.IGeoLaunchView;
import com.geo.viewmodel.GeoLaunchViewModel;
import com.geo.viewmodel.iviewmodel.IGeoLaunchViewModel;

import java.util.List;

import butterknife.BindView;

public class GeoLaunchActivity extends BaseActivity implements IGeoLaunchView {

    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;
    private IGeoLaunchViewModel iGeoLaunchViewModel;
    private GeoAdapter mGeoAdapter;
    private GeoLaunchViewModel geoDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(this));

        /**
         *  Register your viewmodels
         */

        geoDataViewModel = ViewModelProviders.of(this).get(GeoLaunchViewModel.class);

        /**
         *  Implement Observer to receive latest Data using ViewModel
         */

        geoDataViewModel.getGeoDatas().observe(this, new Observer<List<GeoData>>() {
            @Override
            public void onChanged(@Nullable List<GeoData> geoData) {
                if (mGeoAdapter == null) {
                    mGeoAdapter = new GeoAdapter(geoData);
                    mRvGeoCoordinates.setAdapter(mGeoAdapter);
                } else {
                    mGeoAdapter.resetItems(geoData);
                }
            }
        });

        iGeoLaunchViewModel = new GeoLaunchViewModel(getActivity().getApplication(), this);
        iGeoLaunchViewModel.onCreateViewModel(getIntent().getExtras());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iGeoLaunchViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }
}
