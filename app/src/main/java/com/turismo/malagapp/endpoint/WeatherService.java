package com.turismo.malagapp.endpoint;

import com.turismo.malagapp.model.weather.WeatherResponseDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    // q=Badajoz,es&APPID=28ef52b4a0133553b3e579e28c59201e&units=metric
    @GET("weather?")
    Call<WeatherResponseDto> getWeather(@Query("q") String city, @Query("APPID") String api, @Query("units") String units);
}
