package com.example.weatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApiService {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponse
}