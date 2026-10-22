package com.example.weatherapp.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "saved_cities")
data class SavedCity(
    @PrimaryKey
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)
