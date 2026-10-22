package com.example.weatherapp.utils

object AqiUtils {

    data class AqiResult(
        val level: String,
        val description: String
    )

    fun getCategory(aqiValue: Int): AqiResult {
        return when (aqiValue) {
            in 0..50 -> AqiResult("Good", "Low Health Risk")
            in 51..100 -> AqiResult("Moderate", "Acceptable Air Quality")
            in 101..150 -> AqiResult("Unhealthy for Sensitive Groups", "Moderate Health Risk")
            in 151..200 -> AqiResult("Unhealthy", "High Health Risk")
            in 201..300 -> AqiResult("Very Unhealthy", "Very High Health Risk")
            else -> AqiResult("Hazardous", "Severe Health Risk")
        }
    }

    data class UvResult(
        val level: String,
        val description: String
    )

    fun getUVIndexDesc(uvValue: Float): UvResult {
        return when {
            uvValue <= 2f -> UvResult("Low", "Minimal sun protection needed")
            uvValue <= 5f -> UvResult("Moderate", "Protection required during midday hours")
            uvValue <= 7f -> UvResult("High", "Protection essential—seek shade")
            uvValue <= 10f -> UvResult("Very High", "Extra precautions required—avoid sun exposure")
            else -> UvResult("Extreme", "Take all precautions—extreme risk of harm")
        }
    }
}

