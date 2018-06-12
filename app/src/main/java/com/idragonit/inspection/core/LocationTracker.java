package com.idragonit.inspection.core;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by CJH on 5/19/2016.
 */
public class LocationTracker extends Service implements LocationListener {

    final String TAG = "LocationTracker";
    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public LocationTracker(Context context) {
        mContext = context;
        location = null;
        getLocation();
    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.d(TAG, "GPS Enabled");
                    if (location == null) {
                        location = getLocation(LocationManager.GPS_PROVIDER);
                    }
                }

                // get location from Network Provider
                if (isNetworkEnabled) {
                    Log.d(TAG, "Network Enabled");
                    if (location == null) {
                        location = getLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private Location getLocation(String provider) {
        Location location = null;

        if (Build.VERSION.SDK_INT>=23) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (locationManager != null) {
                    locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    location = locationManager.getLastKnownLocation(provider);
                }
            }
        } else {
            if (locationManager != null) {
                locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                location = locationManager.getLastKnownLocation(provider);
            }
        }


        return location;
    }

    public Location getCurrentLocation() {
        return location;
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
       Location p = getCurrentLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
