package by.alexlevankou.weatherapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import by.alexlevankou.weatherapp.R;
import by.alexlevankou.weatherapp.model.WeatherData;
import by.alexlevankou.weatherapp.viewmodel.WeatherViewModel;


public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager = null;
    private Location mLocation;
    private WeatherViewModel mViewModel;
    //private GPSTracker gpsTrackerService;

    boolean isLocationLoaded = false;

    static final int REQUEST_CODE_PERMISSION_ACCESS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        mViewModel.init();

        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_ACCESS_LOCATION);
            return;
        }

        requestLocation();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_ACCESS_LOCATION:
                if (grantResults.length >= 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
//                    mViewModel.getWeatherByCoordinates(mLocation.getLatitude(), mLocation.getLongitude()).observe(this, new Observer<WeatherData>() {
//                        @Override
//                        public void onChanged(@Nullable WeatherData weatherData) {
//                            int z = 9;
//                        }
//                    });
                } else {
                    // need to call data by city name
                    mViewModel.getWeatherByCity("Minsk").observe(this, new Observer<WeatherData>() {
                        @Override
                        public void onChanged(@Nullable WeatherData weatherData) {
                            int z = 9;
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void requestLocation()
    {
        if(locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if(locationManager != null)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation()
    {
        if(locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if(locationManager != null)
        {
            mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onLocationChanged(Location location) {
        if(!isLocationLoaded)
        {
            mLocation = location;
            mViewModel.getWeatherByCoordinates(mLocation.getLatitude(), mLocation.getLongitude()).observe(this, new Observer<WeatherData>() {
                @Override
                public void onChanged(@Nullable WeatherData weatherData) {
                    int z = 9;
                }
            });
            isLocationLoaded = true;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
