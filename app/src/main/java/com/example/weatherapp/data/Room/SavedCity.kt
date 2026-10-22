package com.example.weatherapp.data.Room

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCity(
    @PrimaryKey
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)
