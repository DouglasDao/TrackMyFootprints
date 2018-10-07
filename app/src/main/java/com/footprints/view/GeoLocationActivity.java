package com.footprints.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.login.LoginManager;
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

import butterknife.BindView;
import butterknife.OnClick;

public class GeoLocationActivity extends BaseActivity implements IGeoLocationView, SwipeAction {

    @BindView(R.id.rv_geo_coordinates)
    RecyclerView mRvGeoCoordinates;
    @BindView(R.id.iv_delete)
    AppCompatImageView mIvDelete;
    @BindView(R.id.iv_pic)
    AppCompatImageView mIvProfilePic;
    @BindView(R.id.tv_profile_name)
    AppCompatTextView mIvProfileName;

    private IGeoLocationViewModel iGeoLocationViewModel;
    private GeoAdapter mGeoAdapter;
    private GeoLocationViewModel geoDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRvGeoCoordinates.setLayoutManager(new LinearLayoutManager(this));
        SharedPref.getInstance().setSharedValue(this, Constants.SharedPrefKey.LOGIN_FLAG, true);
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
        iGeoLocationViewModel = new GeoLocationViewModel(getActivity().getApplication(), this);
        iGeoLocationViewModel.onCreateViewModel(getIntent().getExtras());
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
        checkForDeleteOption();
        super.onResume();
    }

    @Override
    protected void onPause() {
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
        mIvProfileName.setText(name);
        Glide.with(this)
                .load(photo)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mIvProfilePic.setImageResource(R.drawable.ic_broken_image);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(RequestOptions.circleCropTransform())
                .into(mIvProfilePic);
    }

    private void checkForDeleteOption() {
        if (mGeoAdapter != null) {
            if (mGeoAdapter.getItemCount() > 0) {
                mIvDelete.setImageResource(R.drawable.ic_waste);
            } else {
                mIvDelete.setImageResource(R.drawable.ic_waste_black);
            }
        }
    }

    @OnClick(R.id.iv_logout)
    void logout() {
        iGeoLocationViewModel.logout();
        getFirebaseAuth().signOut();
        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                SharedPref.getInstance().setSharedValue(GeoLocationActivity.this, Constants.SharedPrefKey.LOGIN_FLAG, false);
                finish();
            }
        });
    }
}
