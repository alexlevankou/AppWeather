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
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import by.alexlevankou.weatherapp.R;
import by.alexlevankou.weatherapp.model.WeatherData;
import by.alexlevankou.weatherapp.viewmodel.WeatherViewModel;


public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager mLocationManager = null;
    private Location mLocation;
    private WeatherViewModel mViewModel;
    //private GPSTrackerTrackerService;

    private TextView city;
    private ImageView weatherImage;
    private TextView temperature;
    private TextView pressure;
    private TextView humidity;
    private TextView windSpeed;

    static final int REQUEST_CODE_PERMISSION_ACCESS_LOCATION = 0;
    static final int CELSIUS_ZERO_IN_KELVIN = 273;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.city_name);
        weatherImage = findViewById(R.id.weatherImage);
        temperature = findViewById(R.id.temperature_value);
        pressure = findViewById(R.id.pressure_value);
        humidity = findViewById(R.id.humidity_value);
        windSpeed = findViewById(R.id.wind_speed_value);

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
                } else {
                    // display no data
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
        if(mLocationManager == null)
        {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if(mLocationManager != null)
        {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onLocationChanged(Location location) {
        mLocation = location;
        mViewModel.getWeatherByCoordinates(mLocation.getLatitude(), mLocation.getLongitude()).observe(this, new Observer<WeatherData>() {
            @Override
            public void onChanged(@Nullable WeatherData weatherData) {

                if(weatherData != null)
                {
                    city.setText(weatherData.getName());
                    temperature.setText(String.format(getResources().getString(R.string.degree_celsius), weatherData.getMain().getTemp() - CELSIUS_ZERO_IN_KELVIN));

                    pressure.setText(String.format(getResources().getString(R.string.pressure_value), weatherData.getMain().getPressure()));
                    humidity.setText(String.format(getResources().getString(R.string.percentage), weatherData.getMain().getHumidity()));
                    windSpeed.setText(String.format(getResources().getString(R.string.speed), weatherData.getWind().getSpeed()));
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
