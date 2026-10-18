package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName


data class WeatherApiResponse(
    val current: CurrentWeatherResponse,
    val hourly: HourlyForecastResponse,
    val daily: DailyForecastResponse
)

class CurrentWeatherResponse(
    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("relative_humidity_2m")
    val humidity: Int,

    @SerializedName("apparent_temperature")
    val feelsLike: Double,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double,

    @SerializedName("weather_code")
    val weatherCode: Int,

    val time: String
)

data class ForecastCardItem(
    val label: String,
    val icon: String,
    val detail: String,
    val temperature: String
)