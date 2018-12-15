package by.alexlevankou.weatherapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import by.alexlevankou.weatherapp.R;
import by.alexlevankou.weatherapp.model.WeatherData;
import by.alexlevankou.weatherapp.service.LocationService;
import by.alexlevankou.weatherapp.viewmodel.WeatherViewModel;


public class MainActivity extends AppCompatActivity implements LocationListener {

    final String LOG_LOCATION = "LocationLogs";

    private Intent mIntent;
    private ServiceConnection mServiceConn;
    private LocationService mLocationService;
    boolean bound = false;

    ////////////////////////////////////////////////////////////////////////////////////
    private SwipeRefreshLayout swipeRefresher;
    private LocationManager mLocationManager = null;
    private Location mLocation;
    private WeatherViewModel mViewModel;

    private TextView city;
    private ImageView weatherImage;
    private TextView temperature;
    private TextView pressure;
    private TextView humidity;
    private TextView windSpeed;

    private static final Map<String, Integer> imageMap = createImageMap();

    private static final int REQUEST_CODE_PERMISSION_ACCESS_LOCATION = 0;
    private static final int CELSIUS_ZERO_IN_KELVIN = 273;

    private static Map<String, Integer> createImageMap()
    {
        Map<String,Integer> imageMap = new HashMap<String,Integer>();
        // day
        imageMap.put("01d", R.mipmap.i01d);
        imageMap.put("02d", R.mipmap.i02d);
        imageMap.put("03d", R.mipmap.i03d);
        imageMap.put("04d", R.mipmap.i04d);
        imageMap.put("09d", R.mipmap.i09d);
        imageMap.put("10d", R.mipmap.i10d);
        imageMap.put("11d", R.mipmap.i11d);
        imageMap.put("13d", R.mipmap.i13d);
        imageMap.put("50d", R.mipmap.i50d);
        // night
        imageMap.put("01n", R.mipmap.i01n);
        imageMap.put("02n", R.mipmap.i02n);
        imageMap.put("03n", R.mipmap.i03d);
        imageMap.put("04n", R.mipmap.i04d);
        imageMap.put("09n", R.mipmap.i09d);
        imageMap.put("10n", R.mipmap.i10n);
        imageMap.put("11n", R.mipmap.i11d);
        imageMap.put("13n", R.mipmap.i13d);
        imageMap.put("50n", R.mipmap.i50d);
        return imageMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefresher = findViewById(R.id.swipeRefresher);
        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocation();
            }
        });
        swipeRefresher.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        city = findViewById(R.id.city_name);
        weatherImage = findViewById(R.id.weatherImage);
        temperature = findViewById(R.id.temperature_value);
        pressure = findViewById(R.id.pressure_value);
        humidity = findViewById(R.id.humidity_value);
        windSpeed = findViewById(R.id.wind_speed_value);

        mViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        mViewModel.init();

        //getLocation();

        mIntent = new Intent(this, LocationService.class);
        mServiceConn = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_LOCATION, "MainActivity onServiceConnected");
                mLocationService = ((LocationService.LocationBinder) binder).getLocationService();
                getLocation();
                bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_LOCATION, "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mIntent, mServiceConn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bound) return;
        unbindService(mServiceConn);
        bound = false;
    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationService.requestLocation();
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, R.string.permission_request, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_ACCESS_LOCATION);
        }
        swipeRefresher.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_ACCESS_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationService.requestLocation();
            } else {
                Toast.makeText(this, R.string.permission_decline, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

//    @SuppressLint("MissingPermission")
//    private void requestLocation()
//    {
//        if(mLocationManager == null)
//        {
//            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        }
//        if(mLocationManager != null)
//        {
//            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        }
//    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    public void onLocationChanged(Location location) {
        mLocation = location;
        mViewModel.getWeatherByCoordinates(mLocation.getLatitude(), mLocation.getLongitude()).observe(this, new Observer<WeatherData>() {
            @Override
            public void onChanged(@Nullable WeatherData weatherData) {

                if(weatherData != null)
                {
                    String imageName = weatherData.getWeather().getIcon();
                    weatherImage.setImageResource(imageMap.get(imageName));

                    city.setText(weatherData.getName());
                    int temp = weatherData.getMain().getTemp() - CELSIUS_ZERO_IN_KELVIN;
                    temperature.setText(String.format(getResources().getString(R.string.degree_celsius), temp));
                    pressure.setText(String.format(getResources().getString(R.string.pressure_value), weatherData.getMain().getPressure()));
                    humidity.setText(String.format(getResources().getString(R.string.percentage), weatherData.getMain().getHumidity()));
                    windSpeed.setText(String.format(getResources().getString(R.string.speed), weatherData.getWind().getSpeed()));
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
