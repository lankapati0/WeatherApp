package com.example.weatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current")
        current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,weather_code",

        @Query("hourly")
        hourly: String =
            "temperature_2m,precipitation_probability,weather_code",

        @Query("daily")
        daily:String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",

        @Query("forecast_days")
        forecastDays: Int = 7,

        @Query("timezone")
        timezone: String = "auto"
    ): WeatherApiResponse


}