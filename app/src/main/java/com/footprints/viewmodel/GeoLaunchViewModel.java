package com.footprints.viewmodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.footprints.R;
import com.footprints.model.GeoData;
import com.footprints.model.GeoDataRepo;
import com.footprints.view.iview.IGeoLaunchView;
import com.footprints.viewmodel.iviewmodel.IGeoLaunchViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Objects;

/**
 * Created by Dell on 27-05-2018.
 */

public class GeoLaunchViewModel extends BaseViewModel implements IGeoLaunchViewModel {

    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int GPS_ENABLED_REQUEST_CODE = 102;
    private static final int GPS_ENABLE_REQUEST_CODE = 100;
    public boolean isLocUserDialog = false;
    private IGeoLaunchView iGeoLaunchView;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient mLocationProviderClient;
    private GeoData geoData;
    private GeoData geoDataDel;
    private String address;
    private Double newLatitude;
    private Double newLongitude;
    private Double prevLatitude = 0.0;
    private Double prevLongitude = 0.0;
    private GpsStatus.Listener mGpsListener;
    private GnssStatus.Callback mGpsUpdate;
    private GeoDataRepo geoDataRepo;
    private LiveData<List<GeoData>> getLocations;
    private int mLocListSize;
    /**
     * Location Result from Location Callback for Periodic Updates
     */

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                newLatitude = lastLocation.getLatitude();
                newLongitude = lastLocation.getLongitude();
                if (!newLatitude.equals(prevLatitude) && !newLongitude.equals(prevLongitude)) {

                    prevLatitude = newLatitude;
                    prevLongitude = newLongitude;

                    address = iGeoLaunchView.getCodeSnippet().getAddressFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude());
                    geoData = new GeoData(newLatitude, newLongitude, address);
                    geoData.setLatit(newLatitude);
                    geoData.setLongit(newLongitude);
                    if (!address.isEmpty()) geoData.setAddress(address);
                    geoDataDel = geoData;
                    insertLatLngWithAddress(geoData);
                }
            } else {
                getPeriodicLocations();
            }
        }
    };

    public GeoLaunchViewModel(@NonNull Application application) {
        super(application);
        geoDataRepo = new GeoDataRepo(application);
        getLocations = geoDataRepo.getLocations();
    }

    public GeoLaunchViewModel(@NonNull Application application, IGeoLaunchView iGeoLaunchView) {
        super(iGeoLaunchView.getActivity().getApplication(), iGeoLaunchView);
        this.iGeoLaunchView = iGeoLaunchView;
        geoDataRepo = new GeoDataRepo(application);
        getLocations = geoDataRepo.getLocations();
    }

    @Override
    public void onCreateViewModel(Bundle bundle) {
        mLocationManager = (LocationManager) iGeoLaunchView.getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(iGeoLaunchView.getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mGpsUpdate = new GnssStatus.Callback() {
                @Override
                public void onStopped() {
                    super.onStopped();
                    isLocUserDialog = false;
                    getPeriodicLocations();
                }
            };
        } else {
            mGpsListener = new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    if (event == GpsStatus.GPS_EVENT_STOPPED) {
                        isLocUserDialog = false;
                        getPeriodicLocations();
                    }
                }
            };
        }
    }

    @Override
    public void onSaveInstanceStateViewModel(Bundle data) {
        super.onSaveInstanceStateViewModel(data);
        data.putBoolean("UserLocationDialog", isLocUserDialog);
    }

    @Override
    public void onRestoreInstanceStateViewModel(Bundle data) {
        if (data != null) {
            this.isLocUserDialog = data.getBoolean("UserLocationDialog");
        }
    }

    public LiveData<List<GeoData>> getGeoDatas() {
        return getLocations;
    }

    public void insertLatLngWithAddress(GeoData geoData) {
        geoDataRepo.insert(geoData);
    }

    /**
     * Creating Custom Location Request with Fast Interval (delay)
     */

    private LocationRequest setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 50);
        mLocationRequest.setFastestInterval(10 * 50);
        return mLocationRequest;
    }

    /**
     * Getting Periodic Geolocation Updates using FusedLocationProviderClient
     */

    public void getPeriodicLocations() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(setLocationRequest());
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(iGeoLaunchView.getActivity()).checkLocationSettings(builder.build());
        if (!result.isComplete()) {
            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        LocationSettingsStates locState = response.getLocationSettingsStates();
                        if (locState.isGpsPresent() && locState.isGpsUsable()) {
                            if (locState.isLocationPresent() && locState.isLocationUsable()) {
                                GeoLaunchViewModel.this.isLocUserDialog = true;
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
                                getPeriodicLocations();
                            }
                        } else {
                            getPeriodicLocations();
                        }
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                GeoLaunchViewModel.this.isLocUserDialog = true;
                                getPeriodicLocations();
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    if (!isLocUserDialog) {
                                        resolvable.startResolutionForResult(iGeoLaunchView.getActivity(), GPS_ENABLE_REQUEST_CODE);
                                        GeoLaunchViewModel.this.isLocUserDialog = true;
                                    }
                                } catch (IntentSender.SendIntentException | ClassCastException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.d(TAG, "SETTINGS_CHANGE_UNAVAILABLE");
                                break;
                        }
                    }
                }
            });
        }
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getPeriodicLocations();
                }
                break;
            case GPS_ENABLED_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.registerGnssStatusCallback(mGpsUpdate);
                }
                break;
        }
    }

    @Override
    public void onActivityResultViewModel(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    this.isLocUserDialog = true;
                    getPeriodicLocations();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    this.isLocUserDialog = false;
                    getPeriodicLocations();
                }
                break;
        }
    }

    /**
     * UnRegister the GPS status Listener
     */

    @Override
    public void onPauseViewModel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocationManager.unregisterGnssStatusCallback(mGpsUpdate);
        } else {
            mLocationManager.removeGpsStatusListener(mGpsListener);
        }
        LocationServices.getFusedLocationProviderClient(iGeoLaunchView.getActivity()).removeLocationUpdates(locationCallback);
    }

    /**
     * Register the GPS status Listener
     */

    @Override
    public void onResumeViewModel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(iGeoLaunchView.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.registerGnssStatusCallback(mGpsUpdate);
            } else {
                ActivityCompat.requestPermissions(iGeoLaunchView.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_ENABLED_REQUEST_CODE);
            }
        } else {
            mLocationManager.addGpsStatusListener(mGpsListener);
        }
        getPeriodicLocations();
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
    public void delete(int geoData) {
        geoDataRepo.delete(geoData);
    }

    @Override
    public void deleteAll() {
        if (geoDataRepo.getLocationSize() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(iGeoLaunchView.getActivity());
            builder.setMessage("Clear All");
            builder.setPositiveButtonIcon(ContextCompat.getDrawable(iGeoLaunchView.getActivity(), R.drawable.ic_trash_can));
            builder.setPositiveButton("", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    iGeoLaunchView.setImgRes(R.drawable.ic_waste_black);
                    geoDataRepo.deleteAll();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).getDecorView().setAlpha(0.6f);
        }
    }

    @Override
    public int getLocSize(int adapterItemCount) {
        return this.mLocListSize = adapterItemCount;
    }
}
