package com.geo.presenter;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.geo.presenter.ipresenter.IPresenter;
import com.geo.view.iview.IView;

public abstract class BasePresenter implements IPresenter {

    protected String TAG = getClass().getSimpleName();
    protected View mParentView;
    private IView iView;

    public BasePresenter() {

    }

    public BasePresenter(IView iView) {
        this.iView = iView;
        iView.bindPresenter(this);
        mParentView = iView.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onStartPresenter() {

    }

    @Override
    public void onStopPresenter() {

    }

    @Override
    public void onPausePresenter() {

    }

    @Override
    public void onResumePresenter() {

    }

    @Override
    public void onDestroyPresenter() {

    }

    @Override
    public void onActivityResultPresenter(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    public void showSnackBar(String message) {
        if (mParentView != null) {
            Snackbar snackbar = Snackbar.make(mParentView, message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    /*protected String getStringRes(int res){
        return iView.getActivity().getString(res);
    }

    protected int getIntegerRes(int res){
        return iView.getActivity().getResources().getInteger(res);
    }*/

}
