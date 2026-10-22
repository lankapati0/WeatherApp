package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class AirQualityResponse(val current: AirQualityCurrent)
data class AirQualityCurrent(
    @SerializedName("us_aqi")
    val usAqi: Int
)
