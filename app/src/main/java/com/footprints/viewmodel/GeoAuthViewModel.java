package com.footprints.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.footprints.common.Constants;
import com.footprints.util.GeoEditText;
import com.footprints.util.SharedPref;
import com.footprints.view.BaseActivity;
import com.footprints.view.GeoLocationActivity;
import com.footprints.view.iview.IGeoAuthView;
import com.footprints.viewmodel.iviewmodel.IGeoAuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GeoAuthViewModel extends BaseViewModel implements IGeoAuthViewModel {
    private CallbackManager mCallbackManager;
    private IGeoAuthView iGeoAuthView;
    private boolean phoneAuthVerifyStatus;
    private GeoEditText edMobNum;
    private AppCompatEditText edVerifyCode;
    private AppCompatEditText edUsername;
    private AppCompatButton btPhone;
    private AppCompatButton btOtp;
    private String userName;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.e(TAG, "PhoneAuth onVerificationCompleted ");
            phoneAuthVerifyStatus = false;
            handlePhoneAuth(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e(TAG, "PhoneAuth onVerificationFailed ");
            iGeoAuthView.closeLoadingDialog();
            phoneAuthVerifyStatus = false;
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                iGeoAuthView.showMessage("Invalid Phone Number.");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                iGeoAuthView.showMessage("Today Sms Quoted Exceeded.");
            }
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            iGeoAuthView.closeLoadingDialog();
            Log.e(TAG, "PhoneAuth onCodeSent :" + verificationId);
            SharedPref.getInstance().setSharedValue(iGeoAuthView.getActivity(), Constants.SharedPrefKey.PHONE_VERIFY_CODE, verificationId);
            reDraw();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String autoCode) {
            super.onCodeAutoRetrievalTimeOut(autoCode);
            iGeoAuthView.closeLoadingDialog();
            Log.e(TAG, "PhoneAuth onCodeAutoRetrievalTimeOut :" + autoCode);
            SharedPref.getInstance().setSharedValue(iGeoAuthView.getActivity(), Constants.SharedPrefKey.PHONE_VERIFY_CODE, autoCode);
        }
    };

    public GeoAuthViewModel(Application application, IGeoAuthView iGeoAuthView) {
        super(application, iGeoAuthView);
        this.iGeoAuthView = iGeoAuthView;
    }

    @Override
    public void onCreateViewModel(Bundle bundle) {
        mCallbackManager = CallbackManager.Factory.create();

        /*Log.e(TAG,"FLAG : "+SharedPref.getInstance().getBooleanValue(iGeoAuthView.getActivity(), Constants.SharedPrefKey.LOGIN_FLAG));

        if (SharedPref.getInstance().getBooleanValue(iGeoAuthView.getActivity(), Constants.SharedPrefKey.LOGIN_FLAG)) {
            iGeoAuthView.navigateToHome(new Intent(iGeoAuthView.getActivity(), GeoLocationActivity.class));
        }*/
    }

    @Override
    public void onActivityResultViewModel(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCodes.GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                iGeoAuthView.showLoadingDialog(iGeoAuthView.getActivity());
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                iGeoAuthView.closeLoadingDialog();
                iGeoAuthView.showMessage("Sign in Cancelled");
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(iGeoAuthView.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        iGeoAuthView.closeLoadingDialog();
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(iGeoAuthView.getActivity(), GeoLocationActivity.class);
                            intent.putExtra(Constants.BundleKey.PHOTO_URI, Objects.requireNonNull(user.getPhotoUrl()).toString());
                            intent.putExtra(Constants.BundleKey.USER, user);
                            intent.putExtra(Constants.BundleKey.LOGIN_TYPE, "google");
                            iGeoAuthView.navigateToHome(intent);
                        }
                    } else {
                        iGeoAuthView.closeLoadingDialog();
                        iGeoAuthView.showMessage("Something went wrong!");
                    }
                });
    }

    @Override
    public void registerFbLoginAuth(LoginButton mLoginButton) {
        mLoginButton.performClick();
        mLoginButton.setReadPermissions("email", "public_profile");
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(@NonNull LoginResult loginResult) {
                Log.e(TAG, "Fb login success : " + loginResult);
                iGeoAuthView.showLoadingDialog(iGeoAuthView.getActivity());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "Fb login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Fb login error : " + error.getMessage());
                mLoginButton.clearPermissions();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(iGeoAuthView.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        iGeoAuthView.closeLoadingDialog();
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(iGeoAuthView.getActivity(), GeoLocationActivity.class);
                            if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().isEmpty()) {
                                intent.putExtra(Constants.BundleKey.PHOTO_URI, user.getPhotoUrl().toString());
                            }
                            intent.putExtra(Constants.BundleKey.MOB_NUM, !isNullOrEmpty(user.getPhoneNumber()) ? user.getPhoneNumber() : "Nil");
                            intent.putExtra(Constants.BundleKey.USER, user);
                            intent.putExtra(Constants.BundleKey.LOGIN_TYPE, "fb");
                            iGeoAuthView.navigateToHome(intent);
                        }
                    } else {
                        iGeoAuthView.closeLoadingDialog();
                        iGeoAuthView.showMessage("Something went wrong");
                    }
                });
    }

    @Override
    public void authMyPhone(String name, String num, GeoEditText edMobNum, AppCompatEditText edVerifyCode, AppCompatButton btPhone, AppCompatButton btOtp, AppCompatEditText edUsername) {
        if (!num.isEmpty()) {
            this.userName = name;
            this.edMobNum = edMobNum;
            this.edVerifyCode = edVerifyCode;
            this.edUsername = edUsername;
            this.btPhone = btPhone;
            this.btOtp = btOtp;
            if (iGeoAuthView.getCodeSnippet().hasNetworkConnection()) {
                iGeoAuthView.showLoadingDialog(iGeoAuthView.getActivity());
                PhoneAuthProvider.getInstance().verifyPhoneNumber(num, 60, TimeUnit.SECONDS, iGeoAuthView.getActivity(), mCallbacks);
            } else {
                ((BaseActivity) iGeoAuthView.getActivity()).showNetworkMessage();
            }
        } else {
            showSnackBar("PLease Enter Phone Number..");
        }
    }

    @Override
    public void authMyPhoneOTP(String otp, GeoEditText edMobNum, AppCompatEditText edVerifyCode) {
        if (!otp.isEmpty()) {
            Log.e(TAG, "OTP code: " + otp);
            String verificationId = SharedPref.getInstance().getStringValue(iGeoAuthView.getActivity(), Constants.SharedPrefKey.PHONE_VERIFY_CODE);
            if (iGeoAuthView.getCodeSnippet().hasNetworkConnection()) {
                iGeoAuthView.showLoadingDialog(iGeoAuthView.getActivity());
                verifyPhoneNumberWithCode(verificationId, otp);
            } else {
                ((BaseActivity) iGeoAuthView.getActivity()).showNetworkMessage();
            }
        } else {
            showSnackBar("PLease Enter OTP..");
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        handlePhoneAuth(credential);
    }

    private void handlePhoneAuth(PhoneAuthCredential credential) {
        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(iGeoAuthView.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        iGeoAuthView.closeLoadingDialog();
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(iGeoAuthView.getActivity(), GeoLocationActivity.class);
                            intent.putExtra(Constants.BundleKey.USER, user);
                            intent.putExtra(Constants.BundleKey.USER_NAME, userName);
                            intent.putExtra(Constants.BundleKey.LOGIN_TYPE, "phone");
                            iGeoAuthView.navigateToHome(intent);
                        }
                    } else {
                        iGeoAuthView.closeLoadingDialog();
                        iGeoAuthView.showMessage("Please check OTP!");
                    }
                });
    }

    private void reDraw() {
        edMobNum.setVisibility(View.GONE);
        edVerifyCode.setVisibility(View.VISIBLE);
        btPhone.setVisibility(View.GONE);
        edUsername.setVisibility(View.GONE);
        btOtp.setVisibility(View.VISIBLE);
    }
}
