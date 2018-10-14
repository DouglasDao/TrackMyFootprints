package com.footprints.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.footprints.R;
import com.footprints.common.Constants;
import com.footprints.util.CodeSnippet;
import com.footprints.util.SharedPref;
import com.footprints.view.iview.IView;
import com.footprints.view.widget.CustomProgressbar;
import com.footprints.viewmodel.iviewmodel.IViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements IView {

    public GoogleSignInClient mGoogleSignInClient;
    protected String TAG = getClass().getSimpleName();
    protected View mParentView;
    protected CodeSnippet mCodeSnippet;
    ProgressDialog pDialog;
    private IViewModel iViewModel;
    private CustomProgressbar mCustomProgressbar;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(getLayoutId());
        hideKeyboard(getActivity());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        injectViews();
        googleLoginSetup();
    }

    private void googleLoginSetup() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
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

    public void doKeepDialog(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
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

    @Override
    public void showLoadingDialog(Context context) {
        getProgressBar().show();
    }

    @Override
    public void closeLoadingDialog() {
        runOnUiThread(() -> {
            try {
                getProgressBar().dismissProgress();
            } catch (Exception e) {
                showMessage("Please Remove/Kill the app!");
            }
        });
    }

    private CustomProgressbar getProgressBar() {
        if (mCustomProgressbar == null) {
            mCustomProgressbar = new CustomProgressbar(this);
        }
        return mCustomProgressbar;
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
            snackbar.setAction("Settings", view -> mCodeSnippet.showNetworkSettings());
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

    public void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            return mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    public boolean isAuthUserFB() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    public void googleSignOut() {
        SharedPref.getInstance().setSharedValue(this, Constants.SharedPrefKey.LOGIN_FLAG, false);
        getFirebaseAuth().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                redirectToAuth();
            }
        });
    }

    public void fbSignOut() {
        SharedPref.getInstance().setSharedValue(this, Constants.SharedPrefKey.LOGIN_FLAG, false);
        getFirebaseAuth().signOut();
        LoginManager.getInstance().logOut();
        redirectToAuth();
    }

    public void redirectToAuth() {
        SharedPref.getInstance().setSharedValue(this, Constants.SharedPrefKey.LOGIN_FLAG, false);
        Intent intent = new Intent(this, GeoAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.animator.activity_back_in, R.animator.activity_back_out);
    }

    public boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }

    public void clearOnBackPress() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_alert);
        AppCompatButton mPositive = dialog.findViewById(R.id.bt_yes);
        Objects.requireNonNull(mPositive).setOnClickListener(view -> {
            dialog.dismiss();
            clearLoginSession();
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.shape_rectangle);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();
        doKeepDialog(dialog);
    }

    public void clearLoginSession() {
        SharedPref.getInstance().setSharedValue(this, Constants.SharedPrefKey.LOGIN_FLAG, false);
        getFirebaseAuth().signOut();
        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        clearOnBackPress();
    }

    private void getFbKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.footprints", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
