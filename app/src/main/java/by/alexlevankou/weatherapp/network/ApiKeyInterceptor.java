package by.alexlevankou.weatherapp.network;

import java.io.IOException;

import by.alexlevankou.weatherapp.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url().newBuilder()
                .addQueryParameter("appid", BuildConfig.API_KEY)
                .build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}