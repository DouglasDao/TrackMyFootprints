package com.geo.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.geo.R;
import com.geo.adapter.GeoAdapter;
import com.geo.presenter.GeoLaunchPresenter;
import com.geo.presenter.ipresenter.IGeoLaunchPresenter;
import com.geo.view.iview.IGeoLaunchView;

import butterknife.BindView;

public class GeoLaunchActivity extends BaseActivity implements IGeoLaunchView {

    public final String TAG = getClass().getSimpleName();

    private IGeoLaunchPresenter iGeoLaunchPresenter;

    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(this));
        iGeoLaunchPresenter = new GeoLaunchPresenter(getActivity(),this);
        iGeoLaunchPresenter.onCreatePresenter(getIntent().getExtras());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iGeoLaunchPresenter.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void setAdapter(GeoAdapter mGeoAdapter) {
        mRvGeoCoordinates.setAdapter(mGeoAdapter);
    }
}
