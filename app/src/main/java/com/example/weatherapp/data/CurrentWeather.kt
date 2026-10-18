package com.example.weatherapp.data

data class CurrentWeather(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double
)
