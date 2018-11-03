package com.footprints.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.footprints.R;
import com.footprints.adapter.GeoAdapter;
import com.footprints.common.Constants;
import com.footprints.model.GeoData;
import com.footprints.util.SharedPref;
import com.footprints.util.SwipeAction;
import com.footprints.util.SwipeItem;
import com.footprints.view.iview.IGeoLocationView;
import com.footprints.viewmodel.GeoLocationViewModel;
import com.footprints.viewmodel.iviewmodel.IGeoLocationViewModel;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class GeoLocationFragment extends BaseFragment implements IGeoLocationView, SwipeAction {

    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;
    @BindView(R.id.iv_delete)
    AppCompatImageView mIvDelete;
    public IGeoLocationViewModel iGeoLocationViewModel;
    /*@BindView(R.id.iv_pic)
    AppCompatImageView mIvProfilePic;*/
    @BindView(R.id.tv_profile_name)
    AppCompatTextView mIvProfileName;
    private GeoAdapter mGeoAdapter;
    private GeoLocationViewModel geoDataViewModel;

    public static GeoLocationFragment createFor(Bundle args) {
        GeoLocationFragment fragment = new GeoLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(getContext()));
        SharedPref.getInstance().setSharedValue(getContext(), Constants.SharedPrefKey.LOGIN_FLAG, true);
        SwipeItem swipeItem = new SwipeItem(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(swipeItem).attachToRecyclerView(mRvGeoCoordinates);
        /**
         *  Register your ViewModel
         */

        geoDataViewModel = ViewModelProviders.of(this).get(GeoLocationViewModel.class);

        /**
         *  Implement Observer to receive latest Data using ViewModel
         */

        geoDataViewModel.getGeoDatas().observe(this, new Observer<List<GeoData>>() {
            @Override
            public void onChanged(@Nullable List<GeoData> geoData) {
                if (mGeoAdapter != null) {
                    mGeoAdapter.resetItems(geoData);
                    checkForDeleteOption();
                } else {
                    mGeoAdapter = new GeoAdapter(geoData);
                    mRvGeoCoordinates.setAdapter(mGeoAdapter);
                    checkForDeleteOption();
                }
            }
        });
        Log.e(TAG, "Fragment onViewCreated");
        iGeoLocationViewModel = new GeoLocationViewModel(Objects.requireNonNull(getActivity()).getApplication(), this);
        iGeoLocationViewModel.onCreateViewModel(getArguments());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "Fragment onResume");
        checkForDeleteOption();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "Fragment onPause");
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        iGeoLocationViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mGeoAdapter.getData().size() > 0) {
            iGeoLocationViewModel.delete(mGeoAdapter.getData().get(viewHolder.getAdapterPosition()).getId());
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.d(TAG, "onMove target :" + target.getAdapterPosition());
        return true;
    }

    @OnClick(R.id.iv_delete)
    void deleteAll() {
        iGeoLocationViewModel.deleteAll();
    }

    @Override
    public void setImgRes(int res) {
        mIvDelete.setImageResource(res);
    }

    @Override
    public void updateProfileInfo(String name, Uri photo) {
        if (getActivity() != null) {
            if (!getActivity().isDestroyed()) {
                ((HomeActivity) getActivity()).setUserInfo(name, photo);
            }
        }
    }

    @Override
    public void signOut() {
        redirectToAuth();
    }

    private void checkForDeleteOption() {
        if (mGeoAdapter != null) {
            if (mGeoAdapter.getItemCount() > 0) {
                if (getActivity() != null)
                    ((HomeActivity) getActivity()).setToolbarItems(R.drawable.ic_waste);
            } else {
                if (getActivity() != null)
                    ((HomeActivity) getActivity()).setToolbarItems(R.drawable.ic_waste_black);
            }
        }
    }

    @OnClick(R.id.iv_logout)
    void logout() {
        iGeoLocationViewModel.logout();
    }
}
