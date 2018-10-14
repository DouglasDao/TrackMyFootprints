package com.footprints.viewmodel.iviewmodel;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;

import com.facebook.login.widget.LoginButton;
import com.footprints.util.GeoEditText;

public interface IGeoAuthViewModel extends IViewModel {
    void registerFbLoginAuth(LoginButton loginButton);

    void authMyPhone(String name, String num, GeoEditText edMobNum, AppCompatEditText edVerifyCode, AppCompatButton btPhone, AppCompatButton btOtp, AppCompatEditText edUsername);

    void authMyPhoneOTP(String otp, GeoEditText edMobNum, AppCompatEditText edVerifyCode);
}
