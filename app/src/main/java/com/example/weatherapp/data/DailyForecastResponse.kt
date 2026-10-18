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
    val weatherCodes: List<Int>
)
