package com.footprints.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.footprints.common.Constants;
import com.footprints.util.SharedPref;
import com.footprints.view.GeoLocationActivity;
import com.footprints.view.iview.IGeoAuthView;
import com.footprints.viewmodel.iviewmodel.IGeoAuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class GeoAuthViewModel extends BaseViewModel implements IGeoAuthViewModel {
    private IGeoAuthView iGeoAuthView;

    public GeoAuthViewModel(Application application, IGeoAuthView iGeoAuthView) {
        super(application, iGeoAuthView);
        this.iGeoAuthView = iGeoAuthView;
    }

    @Override
    public void onCreateViewModel(Bundle bundle) {
        if (SharedPref.getInstance().getBooleanValue(iGeoAuthView.getActivity(), Constants.SharedPrefKey.LOGIN_FLAG)) {
            iGeoAuthView.navigateToHome(new Intent(iGeoAuthView.getActivity(), GeoLocationActivity.class));
        }
    }

    @Override
    public void onActivityResultViewModel(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCodes.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                iGeoAuthView.showMessage("Sign in Failed");
                Log.e(TAG, "Sign in Failed " + e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(iGeoAuthView.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(iGeoAuthView.getActivity(), GeoLocationActivity.class);
                            intent.putExtra(Constants.BundleKey.USER_EMAIL, Objects.requireNonNull(user.getEmail()));
                            intent.putExtra(Constants.BundleKey.USER_NAME, Objects.requireNonNull(user.getDisplayName()));
                            intent.putExtra(Constants.BundleKey.PHOTO_URI, Objects.requireNonNull(user.getPhotoUrl()).toString());
                            iGeoAuthView.navigateToHome(intent);
                        }
                    }
                });
    }


}
