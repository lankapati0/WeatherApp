package com.example.weatherapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object WeatherUtils {
    data class WeatherInfo(
        val description: String,
        val iconRes: Int
    )
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        return try {
            LocalDate.parse(dateString)
                .format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH))
        } catch (e: Exception) {
            dateString
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDay(dateString: String): String {
        return try {
            LocalDate.parse(dateString)
                .format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH))
        } catch (e: Exception) {
            dateString
        }
    }

    fun getWeatherIcon(weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "☀️"
            1 -> "🌤️"
            2 -> "⛅"
            3 -> "☁️"
            45, 48 -> "🌫️"
            in 51..57 -> "🌦️"
            in 61..67 -> "🌧️"
            in 71..77 -> "❄️"
            in 80..82 -> "🌦️"
            in 85..86 -> "🌨️"
            in 95..99 -> "⛈️"
            else -> "🌡️"
        }
    }

    fun formatRainChance(probability: Int): String {
        return "💧 $probability%"
    }

    fun weatherDescription(code: Int): WeatherInfo {
        return when (code) {
            0 -> WeatherInfo("Clear Sky", R.drawable.ic_sun_cloud_angled_rain)

            1 -> WeatherInfo("Mainly Clear", R.drawable.ic_sun_cloud_angled_rain)
            2, 3 -> WeatherInfo("Partly Cloudy", R.drawable.ic_moon_cloud_fast_wind)

            45, 48 -> WeatherInfo("Foggy", R.drawable.ic_moon_cloud_fast_wind)

            51, 53, 55, 56, 57 ->
                WeatherInfo("Drizzle", R.drawable.ic_moon_cloud_mid_rain)

            61, 63, 65, 66, 67 ->
                WeatherInfo("Rain", R.drawable.ic_moon_cloud_mid_rain)

            71, 73, 75, 77, 85, 86 ->
                WeatherInfo("Snow", R.drawable.ic_moon_cloud_fast_wind)

            80, 81, 82 ->
                WeatherInfo("Rain Showers", R.drawable.ic_sun_cloud_angled_rain)

            95, 96, 99 ->
                WeatherInfo("Thunderstorm", R.drawable.ic_tornado)

            else -> WeatherInfo("Unknown", R.drawable.ic_moon_cloud_fast_wind)
        }

    }

    fun formatTime(value: String): String {
        return try {
            val input = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm",
                Locale.US
            )
            val output = SimpleDateFormat("ha", Locale.US)
            output.format(input.parse(value) ?: Date())
        } catch (e: Exception) {
            value
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatSunTime(dateTime: String): String {
        val inputFormatter =
            java.time.LocalDateTime.parse(dateTime)

        val outputFormatter =
            java.time.format.DateTimeFormatter.ofPattern("h:mm a")

        return inputFormatter.format(outputFormatter)
    }
}
