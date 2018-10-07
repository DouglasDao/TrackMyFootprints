package com.footprints.viewmodel.iviewmodel;

import com.facebook.login.widget.LoginButton;

public interface IGeoAuthViewModel extends IViewModel {
    void registerFbLoginAuth(LoginButton loginButton);
}
