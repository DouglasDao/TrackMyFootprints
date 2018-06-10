package com.geo.view.iview;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.geo.util.CodeSnippet;
import com.geo.viewmodel.iviewmodel.IViewModel;

public interface IView {

    void showMessage(String message);

    void showMessage(int resId);

    FragmentActivity getActivity();

    void showSnackBar(String message);

    void showSnackBar(@NonNull View view, String message);

    void showNetworkMessage();

    void bindViewModel(IViewModel iViewModel);

    CodeSnippet getCodeSnippet();

}
