package by.alexlevankou.weatherapp.network;

import android.support.annotation.NonNull;

import by.alexlevankou.weatherapp.model.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather?units=metric")
    Call<WeatherData> getWeather(@NonNull @Query("q") String query);

    @GET("data/2.5/weather?")
    Call<WeatherData> getWeatherByLocation(@Query("lat") double lat, @Query("lon") double lon);

}
