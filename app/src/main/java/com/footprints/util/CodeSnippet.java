package com.footprints.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.footprints.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CodeSnippet {

    //Time AM or PM
    private String PM = "PM";
    private String AM = "AM";
    private String TAG = getClass().getSimpleName();
    private Context mContext;
    private FirebaseAuth mFirebaseAuth;
    public CodeSnippet() {
    }

    public CodeSnippet(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Checking the internet connectivity
     *
     * @return true if the connection is available otherwise false
     */
    public boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public void showNetworkSettings() {
        Intent chooserIntent = Intent.createChooser(getSettingsIntent(Settings.ACTION_DATA_ROAMING_SETTINGS),
                "Complete action using");
        List<Intent> networkIntents = new ArrayList<>();
        networkIntents.add(getSettingsIntent(Settings.ACTION_WIFI_SETTINGS));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, networkIntents.toArray(new Parcelable[]{}));
        startActivityBySettings(mContext, chooserIntent);
    }

    private Intent getSettingsIntent(String settings) {
        return new Intent(settings);
    }

    private void startActivityBySettings(Context context, String settings) {
        context.startActivity(getSettingsIntent(settings));
    }

    private void startActivityBySettings(Context context, Intent intent) {
        context.startActivity(intent);
    }

    public boolean isLocationPermissionGranted(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void setInfoDialog(final Context context, String title, String message, final Intent intent) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(intent);
            }
        });
        alertDialog.show();
    }

    public String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            if (Geocoder.isPresent()) {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address fetchedAddress = addresses.get(0);
                    StringBuilder strAddress = new StringBuilder();
                    for (int i = 0; i <= fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                    }
                    return strAddress.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getString(R.string.no_network), Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }
}