package com.example.weatherapp.data

class WeatherRepository {

    suspend fun getCurrentWeather (
        latitude: Double,
        longitude: Double
    ): CurrentWeather {
        val response = RetrofitClient.weatherApi.getCurrentWeather(
            latitude,longitude)

        return CurrentWeather(
            cityName = "Current Location",
            temperature = response.current.temperature,
            feelsLike = response.current.feelsLike,
            humidity = response.current.humidity,
            windSpeed = response.current.windSpeed
        )
    }

    suspend fun getWeatherResponse(
        latitude: Double,
        longitude: Double
    ): WeatherApiResponse {
        return RetrofitClient.weatherApi.getCurrentWeather(
            latitude,longitude
        )
    }


}