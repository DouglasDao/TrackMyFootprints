package com.geo.view.iview;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.geo.presenter.ipresenter.IPresenter;
import com.geo.util.CodeSnippet;

public interface IView {

    void showMessage(String message);

    void showMessage(int resId);

    FragmentActivity getActivity();

    void showSnackBar(String message);

    void showSnackBar(@NonNull View view, String message);

    void showNetworkMessage();

    void bindPresenter(IPresenter iPresenter);

    CodeSnippet getCodeSnippet();

}
