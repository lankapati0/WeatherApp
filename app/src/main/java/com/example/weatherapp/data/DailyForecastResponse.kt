package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class DailyForecastResponse(
    val time: List<String>,

    @SerializedName("temperature_2m_max")
    val maxTemperatures: List<Double>,

    @SerializedName("temperature_2m_min")
    val minTemperatures: List<Double>,

    @SerializedName("precipitation_probability_max")
    val precipitationProbabilities: List<Int>,

    @SerializedName("weather_code")
    val weatherCodes: List<Int>,

    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>,

    val sunrise: List<String>,

    val sunset: List<String>,

    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>,

    @SerializedName("rain_sum")
    val rainSum: List<Double>,

    @SerializedName("wind_speed_10m_max")
    val maxWindSpeeds: List<Double>,

    @SerializedName("wind_direction_10m_dominant")
    val dominantWindDirections: List<Double>
)
