package com.footprints.viewmodel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.footprints.R;
import com.footprints.common.Constants;
import com.footprints.model.GeoData;
import com.footprints.model.GeoDataRepo;
import com.footprints.util.SharedPref;
import com.footprints.view.BaseActivity;
import com.footprints.view.iview.IGeoLocationView;
import com.footprints.viewmodel.iviewmodel.IGeoLocationViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Dell on 27-05-2018.
 */

public class GeoLocationViewModel extends BaseViewModel implements IGeoLocationViewModel {

    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int GPS_ENABLED_REQUEST_CODE = 102;
    private static final int GPS_ENABLE_REQUEST_CODE = 100;
    private static final int MY_PHONE_STATE = 103;
    private boolean isLocUserDialog = false;
    private IGeoLocationView iGeoLocationView;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient mLocationProviderClient;
    private GeoData geoData;
    private String address;
    private Double newLatitude;
    private Double newLongitude;
    private GpsStatus.Listener mGpsListener;
    private GnssStatus.Callback mGpsUpdate;
    private GeoDataRepo geoDataRepo;
    private LiveData<List<GeoData>> getLocations;
    private FirebaseFirestore mFirebaseFireStore;
    private Map<String, Object> mGeoPositions;
    private String mobNum[] = new String[10];
    private String mobInfo = "noNumb";
    private Uri mProfilePhoto;
    private String mDisplayName;
    private String mPersonEmail;
    private String mLoginType;
    private Context mContext;
    /**
     * Location Result from Location Callback for Periodic Updates
     */

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null) {
                getLastLocation();
                return;
            }
            List<Location> lastLocation = locationResult.getLocations();
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                for (Location loc : lastLocation) {
                    newLatitude = loc.getLatitude();
                    newLongitude = loc.getLongitude();
                }

                String oldLatitude = SharedPref.getInstance().getStringValue(mContext, Constants.SharedPrefKey.LAT_KEY);
                String oldLongitude = SharedPref.getInstance().getStringValue(mContext, Constants.SharedPrefKey.LNG_KEY);

                if (oldLatitude.isEmpty() && oldLongitude.isEmpty()) {
                    if (!newLatitude.isNaN() && !newLongitude.isNaN()) {
                        SharedPref.getInstance().setSharedValue(mContext, Constants.SharedPrefKey.LAT_KEY, String.valueOf(newLatitude));
                        SharedPref.getInstance().setSharedValue(mContext, Constants.SharedPrefKey.LNG_KEY, String.valueOf(newLongitude));
                        setAddressWithLocation(newLatitude, newLongitude);
                    }
                }

                if (!oldLatitude.isEmpty()
                        && !oldLongitude.isEmpty()) {

                    String prevLatitude = SharedPref.getInstance().getStringValue(mContext, Constants.SharedPrefKey.LAT_KEY);
                    String prevLongitude = SharedPref.getInstance().getStringValue(mContext, Constants.SharedPrefKey.LNG_KEY);

                    if (!newLatitude.equals(Double.valueOf(prevLatitude)) && !newLongitude.equals(Double.valueOf(prevLongitude))) {

                        SharedPref.getInstance().setSharedValue(mContext, Constants.SharedPrefKey.LAT_KEY, String.valueOf(newLatitude));
                        SharedPref.getInstance().setSharedValue(mContext, Constants.SharedPrefKey.LNG_KEY, String.valueOf(newLongitude));
                        setAddressWithLocation(newLatitude, newLongitude);
                    }
                }
            } else {
                getPeriodicLocations();
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if (!locationAvailability.isLocationAvailable()) {
                getPeriodicLocations();
            }
        }
    };

    public GeoLocationViewModel(@NonNull Application application) {
        super(application);
        geoDataRepo = new GeoDataRepo(application);
        getLocations = geoDataRepo.getLocations();
    }

    public GeoLocationViewModel(@NonNull Application application, IGeoLocationView iGeoLocationView) {
        super(iGeoLocationView.getActivity().getApplication(), iGeoLocationView);
        this.iGeoLocationView = iGeoLocationView;
        geoDataRepo = new GeoDataRepo(application);
        mGeoPositions = new HashMap<>();
        getLocations = geoDataRepo.getLocations();

    }

    @Override
    public void onCreateViewModel(Bundle bundle) {
        mContext = iGeoLocationView.getActivity();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFirebaseFireStore = FirebaseFirestore.getInstance();
        checkForGPSStatus();
        setPhoneState();
        intentFromAuth(bundle);
    }

    private void intentFromAuth(Bundle mBundle) {
        if (mBundle != null) {
            mLoginType = mBundle.getString(Constants.BundleKey.LOGIN_TYPE);
            mProfilePhoto = Uri.parse(Objects.requireNonNull(mBundle.getString(Constants.BundleKey.PHOTO_URI)));
            mDisplayName = Objects.requireNonNull(mBundle.getString(Constants.BundleKey.USER_NAME));
            mPersonEmail = Objects.requireNonNull(mBundle.getString(Constants.BundleKey.USER_EMAIL));
            mGeoPositions.put("Login Type", mLoginType);
            if (Objects.equals(mLoginType, "google")) {
                iGeoLocationView.updateProfileInfo(mDisplayName, mProfilePhoto);
                mGeoPositions.put("Person Email", mPersonEmail);
                mGeoPositions.put("Google Username", mDisplayName);
                mGeoPositions.put("Google Photo", mProfilePhoto.toString());
                SharedPref.getInstance().setSharedValue(iGeoLocationView.getActivity(), Constants.SharedPrefKey.DISPLAY_NAME, mDisplayName);
                SharedPref.getInstance().setSharedValue(iGeoLocationView.getActivity(), Constants.SharedPrefKey.PHOTO_URI, mProfilePhoto.toString());
            } else if (Objects.equals(mLoginType, "fb")) {
                iGeoLocationView.updateProfileInfo(mDisplayName, mProfilePhoto);
                mGeoPositions.put("Person Email", mPersonEmail);
                mGeoPositions.put("FB Username", mDisplayName);
                mGeoPositions.put("FB Photo", mProfilePhoto.toString());
                if (!Objects.requireNonNull(mBundle.getString(Constants.BundleKey.MOB_NUM)).isEmpty()) {
                    mGeoPositions.put("FB Phone Number", Objects.requireNonNull(mBundle.getString(Constants.BundleKey.MOB_NUM)));
                }
                SharedPref.getInstance().setSharedValue(iGeoLocationView.getActivity(), Constants.SharedPrefKey.DISPLAY_NAME, mDisplayName);
                SharedPref.getInstance().setSharedValue(iGeoLocationView.getActivity(), Constants.SharedPrefKey.PHOTO_URI, mProfilePhoto.toString());
            }
            String dispName = SharedPref.getInstance().getStringValue(iGeoLocationView.getActivity(), Constants.SharedPrefKey.DISPLAY_NAME);
            String profPic = SharedPref.getInstance().getStringValue(iGeoLocationView.getActivity(), Constants.SharedPrefKey.PHOTO_URI);
            iGeoLocationView.updateProfileInfo(dispName, Uri.parse(profPic));
        }
    }


    private void checkForGPSStatus() {
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
            mGpsListener = event -> {
                if (event == GpsStatus.GPS_EVENT_STOPPED) {
                    isLocUserDialog = false;
                    getPeriodicLocations();
                }
            };
        }
    }

    /**
     * Getting Phone number and Network operator information using PHONE_STATE
     */

    private void setPhoneState() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(iGeoLocationView.getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PHONE_STATE);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(mContext);
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                if (subsInfoList != null) {
                    if (subsInfoList.size() > 0) {
                        for (int i = 0; i < subsInfoList.size(); i++) {
                            mobNum[i] = subsInfoList.get(i).getCarrierName() + "-" + subsInfoList.get(i).getNumber();
                        }
                    }
                }
                if (mobNum[0] != null || mobNum[1] != null) {
                    mobInfo = mobNum[0] + " | " + mobNum[1];
                }
                Log.e("Test", " Number is  " + mobNum[0] + " | " + mobNum[1]);
            } else {
                TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr != null ? tMgr.getLine1Number() : null;
                if (mPhoneNumber != null && !mPhoneNumber.isEmpty()) {
                    mobInfo = mPhoneNumber;
                } else {
                    mobInfo = tMgr.getNetworkOperatorName();
                }
            }
        }
    }

    private void setAddressWithLocation(Double newLatitude, Double newLongitude) {
        address = iGeoLocationView.getCodeSnippet().getAddressFromLocation(newLatitude, newLongitude);
        geoData = new GeoData(newLatitude, newLongitude, address);
        geoData.setLatit(newLatitude);
        geoData.setLongit(newLongitude);
        if (address != null) {
            if (!address.isEmpty()) {
                geoData.setAddress(address);
            } else {
                geoData.setAddress("~~");
            }
        }
        mGeoPositions.put("Latitude", newLatitude);
        mGeoPositions.put("Longitude", newLongitude);
        mGeoPositions.put("Address", address);
        mGeoPositions.put("Mobile Info", mobInfo);
        mGeoPositions.put("Date-Time", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
        insertLatLngWithAddress(geoData);
    }

    /**
     * Inserting all the information in Local DB and cloud FireStore
     */

    private void insertLatLngWithAddress(GeoData geoData) {
        geoDataRepo.insert(geoData);
        Log.e(TAG, "Mobile Num : " + mobInfo);
        mFirebaseFireStore.collection("loc").document(mDisplayName != null ? mDisplayName : "Nil")
                .set(mGeoPositions)
                .addOnSuccessListener(aVoid -> Log.e(TAG, "Successfully written"))
                .addOnFailureListener(e -> Log.e(TAG, "Something went wrong on writing"));
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


    /**
     * Creating Custom Location Request with Fast Interval (delay)
     */

    private LocationRequest setLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(2000);
        return mLocationRequest;
    }

    /**
     * Getting Periodic Geolocation Updates using FusedLocationProviderClient
     */

    private void getPeriodicLocations() {
        checkForGPSStatus();
        ((BaseActivity) mContext).hideKeyboard(iGeoLocationView.getActivity());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(setLocationRequest());
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(mContext).checkLocationSettings(builder.build());
        if (!result.isComplete()) {
            result.addOnCompleteListener(task -> {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    LocationSettingsStates locState = response.getLocationSettingsStates();
                    if (locState.isGpsPresent() && locState.isGpsUsable()) {
                        if (locState.isLocationPresent() && locState.isLocationUsable()) {
                            GeoLocationViewModel.this.isLocUserDialog = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                    mLocationProviderClient.requestLocationUpdates(setLocationRequest(), locationCallback, Looper.myLooper());

                                } else {
                                    ActivityCompat.requestPermissions(iGeoLocationView.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
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
                            GeoLocationViewModel.this.isLocUserDialog = true;
                            getPeriodicLocations();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                if (!isLocUserDialog) {
                                    resolvable.startResolutionForResult(iGeoLocationView.getActivity(), GPS_ENABLE_REQUEST_CODE);
                                    GeoLocationViewModel.this.isLocUserDialog = true;
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
            case MY_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPeriodicLocations();
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mLocationProviderClient.getLastLocation()
                .addOnCompleteListener(iGeoLocationView.getActivity(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        setAddressWithLocation(task.getResult().getLatitude(), task.getResult().getLongitude());
                        Log.d(TAG, "Latitude :" + task.getResult().getLatitude());
                        Log.d(TAG, "Longitude :" + task.getResult().getLongitude());
                    }
                });
    }

    /**
     * UnRegister the GPS status Listener
     */

    @Override
    public void onPauseViewModel() {
        LocationServices.getFusedLocationProviderClient(iGeoLocationView.getActivity()).removeLocationUpdates(locationCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocationManager.unregisterGnssStatusCallback(mGpsUpdate);
        } else {
            mLocationManager.removeGpsStatusListener(mGpsListener);
        }
    }

    /**
     * Register the GPS status Listener
     */

    @Override
    public void onResumeViewModel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(iGeoLocationView.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.registerGnssStatusCallback(mGpsUpdate);
                getPeriodicLocations();
            } else {
                ActivityCompat.requestPermissions(iGeoLocationView.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_ENABLED_REQUEST_CODE);
            }
        } else {
            mLocationManager.addGpsStatusListener(mGpsListener);
            getPeriodicLocations();
        }
    }

    @Override
    public void delete(int geoData) {
        geoDataRepo.delete(geoData);
    }

    /**
     * Popup to Delete All entries
     */

    @Override
    public void deleteAll() {
        if (geoDataRepo.getLocationSize() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Clear All");
            builder.setPositiveButtonIcon(ContextCompat.getDrawable(iGeoLocationView.getActivity(), R.drawable.ic_trash_can));
            builder.setPositiveButton("", (dialogInterface, i) -> {
                iGeoLocationView.setImgRes(R.drawable.ic_waste_black);
                geoDataRepo.deleteAll();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).getDecorView().setAlpha(0.6f);
        }
    }

    @Override
    public void logout() {
        if (mLoginType != null) {
            if (!mLoginType.isEmpty()) {
                if (mLoginType.equals("google")) {
                    if (((BaseActivity) iGeoLocationView.getActivity()) != null) {
                        ((BaseActivity) iGeoLocationView.getActivity()).googleSignOut();
                    }
                } else if (mLoginType.equals("fb")) {
                    if (((BaseActivity) iGeoLocationView.getActivity()) != null) {
                        ((BaseActivity) iGeoLocationView.getActivity()).fbSignOut();
                    }
                }
            }
        }
    }
}

