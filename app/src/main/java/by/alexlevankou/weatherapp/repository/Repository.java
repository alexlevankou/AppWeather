package by.alexlevankou.weatherapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import by.alexlevankou.weatherapp.model.WeatherData;
import by.alexlevankou.weatherapp.network.ApiFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    public LiveData<WeatherData> getWeather(double latitude, double longitude) {
        final MutableLiveData<WeatherData> data = new MutableLiveData<>();
        callLocation(data, latitude, longitude);
        return data;
    }

    private void callLocation(final MutableLiveData<WeatherData> data, double latitude, double longitude) {
        ApiFactory.getWeatherService().getWeatherByLocation(latitude, longitude).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
            }
        });
    }

    private void call(final MutableLiveData<WeatherData> data, String query) {
        ApiFactory.getWeatherService().getWeather(query).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
            }
        });
    }
}
