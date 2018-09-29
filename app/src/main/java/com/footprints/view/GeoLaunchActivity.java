package com.footprints.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.footprints.R;
import com.footprints.adapter.GeoAdapter;
import com.footprints.model.GeoData;
import com.footprints.util.SwipeAction;
import com.footprints.util.SwipeItem;
import com.footprints.view.iview.IGeoLaunchView;
import com.footprints.viewmodel.GeoLaunchViewModel;
import com.footprints.viewmodel.iviewmodel.IGeoLaunchViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GeoLaunchActivity extends BaseActivity implements IGeoLaunchView, SwipeAction {

    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;
    @BindView(R.id.iv_delete)
    AppCompatImageView mIvDelete;

    private IGeoLaunchViewModel iGeoLaunchViewModel;
    private GeoAdapter mGeoAdapter;
    private GeoLaunchViewModel geoDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(this));
        mIvDelete.setImageResource(R.drawable.ic_waste_black);
        SwipeItem swipeItem = new SwipeItem(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(swipeItem).attachToRecyclerView(mRvGeoCoordinates);
        /**
         *  Register your ViewModel
         */

        geoDataViewModel = ViewModelProviders.of(this).get(GeoLaunchViewModel.class);

        /**
         *  Implement Observer to receive latest Data using ViewModel
         */

        geoDataViewModel.getGeoDatas().observe(this, new Observer<List<GeoData>>() {
            @Override
            public void onChanged(@Nullable List<GeoData> geoData) {
                if (mGeoAdapter != null) {
                    mGeoAdapter.resetItems(geoData);
                    if (mGeoAdapter.getItemCount() > 0) {
                        mIvDelete.setImageResource(R.drawable.ic_waste);
                    } else {
                        mIvDelete.setImageResource(R.drawable.ic_waste_black);
                    }
                } else {
                    mGeoAdapter = new GeoAdapter(geoData);
                    mRvGeoCoordinates.setAdapter(mGeoAdapter);
                    mIvDelete.setImageResource(R.drawable.ic_waste_black);
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
        if (mGeoAdapter != null) {
            if (mGeoAdapter.getItemCount() > 0) {
                mIvDelete.setImageResource(R.drawable.ic_waste);
            } else {
                mIvDelete.setImageResource(R.drawable.ic_waste_black);
            }
        }
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mGeoAdapter.getData().size() > 0) {
            iGeoLaunchViewModel.delete(mGeoAdapter.getData().get(viewHolder.getAdapterPosition()).getId());
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.d(TAG, "onMove target :" + target.getAdapterPosition());
        return true;
    }

    @OnClick(R.id.iv_delete)
    void deleteAll() {
        iGeoLaunchViewModel.deleteAll();
    }

    @Override
    public void setImgRes(int res) {
        mIvDelete.setImageResource(res);
    }

}
