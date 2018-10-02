package com.footprints.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;

import com.footprints.R;
import com.footprints.common.Constants;
import com.footprints.view.iview.IGeoAuthView;
import com.footprints.viewmodel.GeoAuthViewModel;
import com.footprints.viewmodel.iviewmodel.IGeoAuthViewModel;

import butterknife.BindView;
import butterknife.OnClick;


public class GeoAuthActivity extends BaseActivity implements IGeoAuthView {

    @BindView(R.id.iv_google_signin)
    AppCompatImageView mIvGoogleSignin;
    @BindView(R.id.iv_mob_auth)
    AppCompatImageView mIvMobAuth;

    private IGeoAuthViewModel iGeoAuthViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iGeoAuthViewModel = new GeoAuthViewModel(getActivity().getApplication(), this);
        iGeoAuthViewModel.onCreateViewModel(getIntent().getExtras());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_geo_launch_auth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.iv_google_signin)
    void googleSignInPopup() {
        if (getCodeSnippet().hasNetworkConnection()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), Constants.RequestCodes.RC_SIGN_IN);
        } else {
            showNetworkMessage();
        }
    }

    @OnClick(R.id.iv_mob_auth)
    void mobileAuth() {

    }

    @Override
    public void navigateToHome(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
    }
}
