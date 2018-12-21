package by.alexlevankou.weatherapp.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import by.alexlevankou.weatherapp.R;
import by.alexlevankou.weatherapp.view.MainActivity;

public class LocationService extends Service implements LocationListener {

    IBinder mBinder;

    double latitude = 0;
    double longitude = 0;

    // The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10000;
    // The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 10000;

    public void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }

    public void requestLocation() {
        try {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            if(locationManager != null) {
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            if(locationManager != null) {
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }

            if (!isGPSEnabled && !isNetworkEnabled) {
                // location service disabled
            } else {

                Location location = null;
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (isNetworkEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                updateGPSCoordinates(location);
            }
        } catch (Exception e) {
            Log.e("Error : Location","Impossible to connect to LocationManager", e);
        }
    }

    public void updateGPSCoordinates(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Intent intent = new Intent();
            intent.setAction(MainActivity.BROADCAST_ACTION);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateGPSCoordinates(location);
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        getLocation();
        mBinder = new LocationBinder();
        return mBinder;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public class LocationBinder extends Binder {
        public LocationService getLocationService() {
            return LocationService.this;
        }
    }
}