package by.alexlevankou.weatherapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import by.alexlevankou.weatherapp.model.WeatherData;
import by.alexlevankou.weatherapp.repository.Repository;


public class WeatherViewModel extends ViewModel {

    private LiveData<WeatherData> data;

    private Repository repo;

    public void init() {
        if(repo == null) {
            repo = new Repository();
        }
    }

    public LiveData<WeatherData> getWeatherByCity(String city) {
        if(data == null){
            data = new MutableLiveData<>();
        }
        // call by city
        return data;
    }

    public LiveData<WeatherData> getWeatherByCoordinates(double latitude, double longitude) {
        if(data == null) {
            data = new MutableLiveData<>();
        }
        Runnable r = new ApiRequestRunnable(latitude, longitude);
        new Thread(r).start();
        return data;
    }

    public class ApiRequestRunnable implements Runnable {

        private LiveData<WeatherData> mData;

        double mLatitude = 0;
        double mLongitude = 0;

        ApiRequestRunnable(double latitude, double longitude) {
            mData = data;
            mLatitude = latitude;
            mLongitude = longitude;
        }

        public void run() {
            data = repo.getWeather(mLatitude, mLongitude);
        }

        public LiveData<WeatherData> getData(){
            return mData;
        }
    }
}