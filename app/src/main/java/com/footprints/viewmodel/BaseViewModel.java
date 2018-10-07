package com.footprints.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.footprints.view.iview.IView;
import com.footprints.viewmodel.iviewmodel.IViewModel;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseViewModel extends AndroidViewModel implements IViewModel {

    protected String TAG = getClass().getSimpleName();
    protected View mParentView;
    private IView iView;
    private FirebaseAuth mFirebaseAuth;
    public BaseViewModel(Application application) {
        super(application);
    }

    public BaseViewModel(Application application, IView iView) {
        super(application);
        this.iView = iView;
        iView.bindViewModel(this);
        mParentView = iView.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onStartViewModel() {

    }

    @Override
    public void onStopViewModel() {

    }

    @Override
    public void onPauseViewModel() {

    }

    @Override
    public void onResumeViewModel() {

    }

    @Override
    public void onDestroyViewModel() {

    }

    @Override
    public void onActivityResultViewModel(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onSaveInstanceStateViewModel(Bundle data) {

    }

    @Override
    public void onRestoreInstanceStateViewModel(Bundle data) {

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

    public FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            return mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    protected boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }

    /*protected String getStringRes(int res){
        return iView.getActivity().getString(res);
    }

    protected int getIntegerRes(int res){
        return iView.getActivity().getResources().getInteger(res);
    }*/

}
