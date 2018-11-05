package by.alexlevankou.weatherapp.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    double latitude;
    double longitude;

    // The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
    // The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 10000;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            if(!isPermissionGranted())
            {
                return null;
            }
            if(locationManager != null) {
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            if(locationManager != null) {
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }

            if (!isGPSEnabled && !isNetworkEnabled) {
                // location service disabled
            } else {
                canGetLocation = true;

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateGPSCoordinates(location);
                }

                if (isNetworkEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates(location);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error : Location","Impossible to connect to LocationManager", e);
        }
        return location;
    }

    public void updateGPSCoordinates(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    public void onLocationChanged(Location location) {
        updateGPSCoordinates(location);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}