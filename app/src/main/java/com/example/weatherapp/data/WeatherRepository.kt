package com.example.weatherapp.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val airQualityApi: AirQualityApiService
){
    suspend fun getWeatherResponse(
        latitude: Double,
        longitude: Double
    ): WeatherApiResponse {
        return weatherApi.getCurrentWeather(
            latitude,longitude
        )
    }

    suspend fun getAirquality(latitude: Double,longitude: Double): AirQualityResponse {
        return airQualityApi.getAirQuality(
            latitude = latitude,
            longitude = longitude
        )
    }
}