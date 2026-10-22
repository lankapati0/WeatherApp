
package com.example.weatherapp.di

import com.example.weatherapp.data.AirQualityApiService
import com.example.weatherapp.data.GeocodingApiService
import com.example.weatherapp.data.WeatherApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL =
        "https://api.open-meteo.com/"

    private const val AIR_QUALITY_BASE_URL =
        "https://air-quality-api.open-meteo.com/"
    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAirQualityApi(): AirQualityApiService {
        return Retrofit.Builder()
            .baseUrl(AIR_QUALITY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirQualityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodigApi(): GeocodingApiService {
            return Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApiService::class.java)
    }
}
