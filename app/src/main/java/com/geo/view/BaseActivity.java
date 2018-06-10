package com.geo.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.geo.util.CodeSnippet;
import com.geo.view.iview.IView;
import com.geo.viewmodel.iviewmodel.IViewModel;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements IView {

    protected String TAG = getClass().getSimpleName();
    protected View mParentView;
    protected CodeSnippet mCodeSnippet;
    ProgressDialog pDialog;
    private IViewModel iViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        injectViews();
    }

    protected abstract int getLayoutId();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        mParentView = getWindow().getDecorView().findViewById(android.R.id.content);
        return super.onCreateView(name, context, attrs);
    }

    private void injectViews() {
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (iViewModel != null) iViewModel.onStartViewModel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (iViewModel != null) iViewModel.onStopViewModel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (iViewModel != null) iViewModel.onPauseViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iViewModel != null) iViewModel.onResumeViewModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iViewModel != null) iViewModel.onDestroyViewModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (iViewModel != null) iViewModel.onSaveInstanceStateViewModel(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (iViewModel != null) iViewModel.onRestoreInstanceStateViewModel(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (iViewModel != null) iViewModel.onActivityResultViewModel(requestCode, resultCode, data);
    }

    public void bindViewModel(IViewModel iViewModel) {
        this.iViewModel = iViewModel;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showLoadingDialog(Context context) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(Html.escapeHtml("<b>Please wait auto read otp processing...</b>"));
        pDialog.setCancelable(false);
        pDialog.show();
    }
    public void closeLoadingDialog() {

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }

    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    @Override
    public void showSnackBar(String message) {
        if (mParentView != null) {
            Snackbar snackbar = Snackbar.make(mParentView, message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    @Override
    public void showSnackBar(@NonNull View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void showNetworkMessage() {
        if (mParentView != null) {
            Snackbar snackbar = Snackbar.make(mParentView, "No Network found!", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.setAction("Settings", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeSnippet.showNetworkSettings();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public CodeSnippet getCodeSnippet() {
        if (mCodeSnippet == null) {
            mCodeSnippet = new CodeSnippet(getActivity());
            return mCodeSnippet;
        }
        return mCodeSnippet;
    }

}
