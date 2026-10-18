package com.example.weatherapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object WeatherUtils {

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

    fun weatherDescription(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45, 48 -> "Foggy"
            51, 53, 55 -> "Drizzle"
            56, 57 -> "Freezing Drizzle"
            61, 63, 65 -> "Rain"
            66, 67 -> "Freezing Rain"
            71, 73, 75, 77 -> "Snow"
            80, 81, 82 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with Hail"
            else -> "Unknown"
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
}