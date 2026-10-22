package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class HourlyForecastResponse(
    val time: List<String>,

    @SerializedName("temperature_2m")
    val temperatures: List<Double>,

    @SerializedName("precipitation_probability")
    val precipitationProbabilities:List<Int>,

    @SerializedName("weather_code")
    val weatherCodes:List<Int>,

    @SerializedName("rain")
    val rainfall:List<Double>

)
