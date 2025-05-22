package com.example.weather_app.network

import com.example.weather_app.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.weather_app.utils.Constants

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String = Constants.OPENWEATHER_API_KEY,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.OPENWEATHER_API_KEY,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
} 