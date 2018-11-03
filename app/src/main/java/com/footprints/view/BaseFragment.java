package com.footprints.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

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

import java.util.Objects;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment implements IView {

    public GoogleSignInClient mGoogleSignInClient;
    protected String TAG = getClass().getSimpleName();
    protected CodeSnippet mCodeSnippet;
    protected View mParentView;
    private FirebaseAuth mFirebaseAuth;
    private CustomProgressbar mCustomProgressbar;
    private Context mContext;
    private IViewModel iViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*Objects.requireNonNull(getActivity()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getActivity()).getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);*/
        mParentView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        return inflater.inflate(getLayoutId(), container, false);
    }

    private void googleLoginSetup() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), googleSignInOptions);
    }

    public void googleSignOut() {
        SharedPref.getInstance().setSharedValue(getContext(), Constants.SharedPrefKey.LOGIN_FLAG, false);
        getFirebaseAuth().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                redirectToAuth();
            }
        });
    }


    public void fbSignOut() {
        SharedPref.getInstance().setSharedValue(getContext(), Constants.SharedPrefKey.LOGIN_FLAG, false);
        getFirebaseAuth().signOut();
        LoginManager.getInstance().logOut();
        redirectToAuth();
    }

    public void redirectToAuth() {
        SharedPref.getInstance().setSharedValue(getContext(), Constants.SharedPrefKey.LOGIN_FLAG, false);
        Intent intent = new Intent(getContext(), GeoAuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
        getActivity().overridePendingTransition(R.animator.activity_back_in, R.animator.activity_back_out);
    }

    public FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            return mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hideKeyboard(getActivity());
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.bind(this, view);
        googleLoginSetup();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (iViewModel != null) iViewModel.onSaveInstanceStateViewModel(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (iViewModel != null) iViewModel.onRestoreInstanceStateViewModel(savedInstanceState);
    }

    public void bindViewModel(IViewModel iViewModel) {
        this.iViewModel = iViewModel;
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

    @Override
    public CodeSnippet getCodeSnippet() {
        if (mCodeSnippet == null) {
            mCodeSnippet = new CodeSnippet(getActivity());
            return mCodeSnippet;
        }
        return mCodeSnippet;
    }

    private CustomProgressbar getProgressBar() {
        if (mCustomProgressbar == null) {
            mCustomProgressbar = new CustomProgressbar(getContext());
        }
        return mCustomProgressbar;
    }


    @Override
    public void showLoadingDialog(Context context) {
        if (context != null) {
            if (getProgressBar() != null) {
                getProgressBar().show();
            }
        }
    }

    @Override
    public void closeLoadingDialog() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            try {
                getProgressBar().dismissProgress();
            } catch (Exception e) {
                showMessage("Please Remove/Kill the app!");
            }
        });
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
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
    public void showNetworkMessage() {
        if (mParentView != null) {
            Snackbar snackbar = Snackbar.make(mParentView, "No Network found!", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.setAction("Settings", view -> mCodeSnippet.showNetworkSettings());
            View view = snackbar.getView();
            view.setAlpha(0.9f);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
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
    public void onStart() {
        super.onStart();
        if (iViewModel != null) iViewModel.onStartViewModel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (iViewModel != null) iViewModel.onStopViewModel();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (iViewModel != null) iViewModel.onPauseViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (iViewModel != null) iViewModel.onResumeViewModel();
    }

    protected abstract int getLayoutId();
}
