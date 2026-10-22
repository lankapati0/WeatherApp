package com.example.weatherapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitClient {
    //f36e354da4c349488e450444261610
    private const val BASE_URL =
        "https://api.open-meteo.com/"
    private const val AIR_QUALITY_BASE_URL =
        "https://air-quality-api.open-meteo.com/"

    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"

    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(WeatherApiService::class.java)
    }

    val airQualityApi: AirQualityApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AIR_QUALITY_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create())
            .build()
            .create(AirQualityApiService::class.java)
    }

    val geocodingApi: GeocodingApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApiService::class.java)
    }
}