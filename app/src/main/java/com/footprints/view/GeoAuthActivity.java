package com.footprints.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.login.widget.LoginButton;
import com.footprints.R;
import com.footprints.common.Constants;
import com.footprints.util.GeoEditText;
import com.footprints.view.iview.IGeoAuthView;
import com.footprints.viewmodel.GeoAuthViewModel;
import com.footprints.viewmodel.iviewmodel.IGeoAuthViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class GeoAuthActivity extends BaseActivity implements IGeoAuthView {

    @BindView(R.id.iv_facebook_signin)
    AppCompatImageView mFbLogin;
    @BindView(R.id.lb_fblogin)
    LoginButton mLoginButton;

    private IGeoAuthViewModel iGeoAuthViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iGeoAuthViewModel = new GeoAuthViewModel(getActivity().getApplication(), this);
        iGeoAuthViewModel.onCreateViewModel(getIntent().getExtras());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_geo_auth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.iv_google_signin)
    void googleSignInPopup() {
        if (getCodeSnippet().hasNetworkConnection()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), Constants.RequestCodes.GOOGLE_SIGN_IN);
        } else {
            showNetworkMessage();
        }
    }

    @OnClick(R.id.iv_facebook_signin)
    void fbAuth() {
        if (!getCodeSnippet().hasNetworkConnection()) {
            showNetworkMessage();
            return;
        }
        iGeoAuthViewModel.registerFbLoginAuth(mLoginButton);
    }

    @OnClick(R.id.iv_mob_auth)
    void phoneAuth() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.phone_num_alert);
        GeoEditText mPhoneNum = dialog.findViewById(R.id.ed_phone_num);
        AppCompatEditText mEdVerifyNum = dialog.findViewById(R.id.ed_verify_code);
        AppCompatButton mBtVerifyPhone = dialog.findViewById(R.id.bt_verify_phone);
        AppCompatButton mBtVerifyCode = dialog.findViewById(R.id.bt_verify_code);
        AppCompatImageView mIvDismiss = dialog.findViewById(R.id.iv_dismiss);
        AppCompatEditText mEdUsername = dialog.findViewById(R.id.ed_username);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        Objects.requireNonNull(mBtVerifyPhone).setOnClickListener(view -> {
            if (!Objects.requireNonNull(mPhoneNum.getText()).toString().isEmpty() && mPhoneNum.getText().length() == 10 && !String.valueOf(mEdUsername.getText()).isEmpty()) {
                iGeoAuthViewModel.authMyPhone(String.valueOf(mEdUsername.getText()), Objects.requireNonNull(mPhoneNum.getTag().toString() + mPhoneNum.getText()), mPhoneNum, mEdVerifyNum, mBtVerifyPhone, mBtVerifyCode, mEdUsername);
            } else {
                showMessage("Need both name and number");
            }
        });
        Objects.requireNonNull(mBtVerifyCode).setOnClickListener(view -> {
            if (!Objects.requireNonNull(mEdVerifyNum.getText()).toString().isEmpty() && mEdVerifyNum.getText().length() == 6) {
                iGeoAuthViewModel.authMyPhoneOTP(String.valueOf(mEdVerifyNum.getText()), mPhoneNum, mEdVerifyNum);
            } else {
                showMessage("Please Check OTP.");
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setOwnerActivity(GeoAuthActivity.this);
        dialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return true;
            }
            return false;
        });
        mIvDismiss.setOnClickListener(view -> {
            dialog.dismiss();
            hideKeyboard(this);
        });
        dialog.show();
        doKeepDialog(dialog);
    }

    @OnClick(R.id.iv_anonymous_auth)
    void anonymousAuthMe() {
        showSnackBar("Anonymous Authentication Under Construction!");
    }

    @Override
    public void navigateToHome(Intent intent) {
        startActivity(intent);
        finish();
        overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
    }

    @OnTouch(R.id.root)
    boolean onPointTouch(View cornerPoint, MotionEvent event) {
        hideKeyboard(this);
        return false;
    }
}
