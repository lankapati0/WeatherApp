package com.example.weatherapp.data

data class CityWeather(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val weather: WeatherApiResponse
)
