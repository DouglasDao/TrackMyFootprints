package com.geo.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.geo.adapter.GeoAdapter;
import com.geo.model.GeoData;
import com.geo.presenter.ipresenter.IGeoLaunchPresenter;
import com.geo.view.iview.IGeoLaunchView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class GeoLaunchPresenter extends BasePresenter implements IGeoLaunchPresenter {

    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int GPS_ENABLE_REQUEST_CODE = 100;
    private IGeoLaunchView iGeoLaunchView;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private GeoAdapter mGeoAdapter;
    private FusedLocationProviderClient mLocationProviderClient;
    private List<GeoData> geoList = new ArrayList<>();



    public GeoLaunchPresenter(IGeoLaunchView iGeoLaunchView) {
        super(iGeoLaunchView);
        this.iGeoLaunchView = iGeoLaunchView;
    }

    @Override
    public void onCreatePresenter(Bundle bundle) {
        mLocationManager = (LocationManager) iGeoLaunchView.getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(iGeoLaunchView.getActivity());
    }

    /**
     *  Creating Custom Location Request with Fast Interval (delay)
     * */

    private LocationRequest setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 100);
        mLocationRequest.setFastestInterval(10 * 100);
        return mLocationRequest;
    }

    private SettingsClient checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(setLocationRequest());
        builder.setAlwaysShow(true);
        LocationSettingsRequest settingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(iGeoLaunchView.getActivity());
        settingsClient.checkLocationSettings(settingsRequest);
        return settingsClient;
    }

    /**
     *  Location Result from Location Callback for Periodic Updates
     * */

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                GeoData geoData = new GeoData();
                geoData.setLatit(Double.toString(lastLocation.getLatitude()));
                geoData.setLongit(Double.toString(lastLocation.getLongitude()));
                geoList.add(geoData);
                if (mGeoAdapter == null) {
                    mGeoAdapter = new GeoAdapter(geoList);
                    iGeoLaunchView.setAdapter(mGeoAdapter);
                } else {
                    mGeoAdapter.resetItems(geoList);
                }
            } else {
                showGPSDiabledDialog();
            }
        }
    };

    /**
     * Getting Periodic Geolocation Updates using FusedLocationProviderClient
     * */

    private void getPeriodicLocations() {
        if (mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    checkLocationSettings();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(iGeoLaunchView.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(iGeoLaunchView.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            mLocationProviderClient.requestLocationUpdates(setLocationRequest(), locationCallback, Looper.myLooper());

                        } else {
                            ActivityCompat.requestPermissions(iGeoLaunchView.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                        }
                    } else {
                        mLocationProviderClient.requestLocationUpdates(setLocationRequest(), locationCallback, Looper.myLooper());
                    }
                } else {
                    iGeoLaunchView.showNetworkMessage();
                }
            } else {
                showGPSDiabledDialog();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mLocationProviderClient.getLastLocation()
                .addOnCompleteListener(iGeoLaunchView.getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "Latitude :" + task.getResult().getLatitude());
                            Log.d(TAG, "Longitude :" + task.getResult().getLongitude());
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getPeriodicLocations();
                }
        }
    }

    @Override
    public void onActivityResultPresenter(int requestCode, int resultCode, Intent data) {
        super.onActivityResultPresenter(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                getPeriodicLocations();
                break;
        }
    }

    private void showGPSDiabledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(iGeoLaunchView.getActivity());
        builder.setTitle("GPS Disabled");
        builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device");
        builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iGeoLaunchView.getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onStartPresenter() {
        super.onStartPresenter();
        Log.d(TAG, "onStart");
        getPeriodicLocations();
    }

    @Override
    public void onPausePresenter() {
        super.onPausePresenter();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStopPresenter() {
        super.onStopPresenter();
        Log.d(TAG, "onStop");
        LocationServices.getFusedLocationProviderClient(iGeoLaunchView.getActivity()).removeLocationUpdates(locationCallback);
    }
}
