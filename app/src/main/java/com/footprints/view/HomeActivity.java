package com.footprints.view;


import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.footprints.R;
import com.footprints.common.menu.DrawerAdapter;
import com.footprints.common.menu.DrawerItem;
import com.footprints.common.menu.SimpleItem;
import com.footprints.widgets.slidingrootnav.SlidingRootNav;
import com.footprints.widgets.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_GEO_LOC = 0;
    private static final int POS_ACCOUNT = 1;
    private static final int POS_MESSAGES = 2;
    private static final int POS_CART = 3;
    private static final int POS_LOGOUT = 5;
    @BindView(R.id.tv_title)
    AppCompatTextView mTvTitle;
    @BindView(R.id.iv_delete)
    AppCompatImageView mIvDelete;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    GeoLocationFragment geoLocationFragment;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(mToolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_GEO_LOC).setChecked(true)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        adapter.setSelected(POS_GEO_LOC);
    }

    public void setUserInfo(String name, Uri photo) {
        AppCompatImageView mProfilePic = findViewById(R.id.iv_profile_pic);
        AppCompatTextView mPersonName = findViewById(R.id.tv_person_name);
        Log.e(TAG, "URI photo " + photo);
        mPersonName.setText(name);
        Glide.with(this)
                .load(photo)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mProfilePic.setImageResource(R.drawable.ic_broken_image);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .apply(RequestOptions.circleCropTransform())
                .into(mProfilePic);
    }

    public void setToolbarItems(int res) {
        mIvDelete.setImageResource(res);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            finish();
        }
        slidingRootNav.closeMenu();
        showFragment();
    }

    private void showFragment() {
        Fragment fragment = new GeoLocationFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @OnClick(R.id.iv_delete)
    void delete() {
        if (getGeoLocFragment() != null) {
            getGeoLocFragment().deleteAll();
        }
    }

    @OnClick(R.id.iv_logout)
    void logout() {
        if (getGeoLocFragment() != null) {
            getGeoLocFragment().logout();
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.colorMilkGreen))
                .withTextTint(color(R.color.colorThickMaroon))
                .withSelectedIconTint(color(R.color.colorMilkGreen))
                .withSelectedTextTint(color(R.color.colorThickMaroon));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    private GeoLocationFragment getGeoLocFragment() {
        geoLocationFragment = (GeoLocationFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (geoLocationFragment != null) return geoLocationFragment;
        else return null;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
}
