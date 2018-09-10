package com.geo.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.geo.R;
import com.geo.adapter.GeoAdapter;
import com.geo.model.GeoData;
import com.geo.util.SwipeAction;
import com.geo.util.SwipeItem;
import com.geo.view.iview.IGeoLaunchView;
import com.geo.viewmodel.GeoLaunchViewModel;
import com.geo.viewmodel.iviewmodel.IGeoLaunchViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GeoLaunchActivity extends BaseActivity implements IGeoLaunchView, SwipeAction {

    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;


    private IGeoLaunchViewModel iGeoLaunchViewModel;
    private GeoAdapter mGeoAdapter;
    private GeoLaunchViewModel geoDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(this));
        SwipeItem swipeItem = new SwipeItem(0, ItemTouchHelper.LEFT, GeoLaunchActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Clear All");
        builder.setPositiveButtonIcon(ContextCompat.getDrawable(this, R.drawable.ic_trash_can));
        builder.setPositiveButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                iGeoLaunchViewModel.deleteAll();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().getDecorView().setAlpha(0.8f);

    }

}
