package com.footprints.view;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.footprints.R;
import com.footprints.adapter.GeoAdapter;
import com.footprints.adapter.listener.GeoLocRecyclerAdapterListener;
import com.footprints.common.Constants;
import com.footprints.model.GeoData;
import com.footprints.util.SharedPref;
import com.footprints.view.iview.IGeoLocationView;
import com.footprints.viewmodel.GeoLocationViewModel;
import com.footprints.viewmodel.iviewmodel.IGeoLocationViewModel;
import com.footprints.widgets.itemTouchHelper.ItemTouchHelperCallback;
import com.footprints.widgets.itemTouchHelper.ItemTouchHelperExtension;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class GeoLocationFragment extends BaseFragment implements IGeoLocationView {

    public IGeoLocationViewModel iGeoLocationViewModel;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;
    @BindView(R.id.iv_delete)
    AppCompatImageView mIvDelete;
    @BindView(R.id.tv_profile_name)
    AppCompatTextView mIvProfileName;
    private GeoAdapter mGeoAdapter;
    private GeoLocationViewModel geoDataViewModel;

    private GeoLocRecyclerAdapterListener<GeoData> adapterListener = new GeoLocRecyclerAdapterListener<GeoData>() {
        @Override
        public void delete(int position, GeoData data) {

            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.inflate_delete_location);

            dialog.findViewById(R.id.iv_yes).setOnClickListener(view -> {
                if (mGeoAdapter.getData().size() > 0) {
                    iGeoLocationViewModel.delete(mGeoAdapter.getData().get(position).getId());
                    dialog.dismiss();
                    mItemTouchHelper.closeOpened();
                }
            });

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_rectangle);
            Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        public void callShareIntent(GeoData data) {
            String uri = "geo:" + data.getLatit() + "," + data.getLongit() + "?q=" + data.getLatit() + "," + data.getLongit();
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
        }

        @Override
        public void onClickItem(int position, GeoData data) {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.inflate_geo_info);
            AppCompatTextView mTvLatLng = dialog.findViewById(R.id.tv_LatLng);
            AppCompatTextView mTvAddress = dialog.findViewById(R.id.tv_address);
            mTvLatLng.setText(data.getLatit() + ", " + data.getLongit());
            mTvAddress.setText(data.getAddress());
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_rectangle);
            Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.show();
        }
    };

    public static GeoLocationFragment createFor(Bundle args) {
        GeoLocationFragment fragment = new GeoLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvGeoCoordinates.setItemAnimator(new DefaultItemAnimator());
        mRvGeoCoordinates.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        SharedPref.getInstance().setSharedValue(getContext(), Constants.SharedPrefKey.LOGIN_FLAG, true);

        mCallback = new ItemTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRvGeoCoordinates);

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
                setAdapter(geoData);
                mGeoAdapter.setItemTouchHelperExtension(mItemTouchHelper);
            }
        });

        iGeoLocationViewModel = new GeoLocationViewModel(Objects.requireNonNull(getActivity()).getApplication(), this);
        iGeoLocationViewModel.onCreateViewModel(getArguments());
    }

    private void setAdapter(List<GeoData> geoData) {
        if (mGeoAdapter != null) {
            mGeoAdapter.resetItems(geoData);
            checkForDeleteOption();
        } else {
            mGeoAdapter = new GeoAdapter(geoData, adapterListener);
            mRvGeoCoordinates.setAdapter(mGeoAdapter);
            checkForDeleteOption();
        }
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
        checkForDeleteOption();
        super.onResume();
    }

    @Override
    public void onPause() {
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
