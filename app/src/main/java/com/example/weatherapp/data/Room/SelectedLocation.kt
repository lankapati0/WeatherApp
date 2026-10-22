package com.example.weatherapp.data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "selected_location")
data class SelectedLocation(

    @PrimaryKey
    val id: Int = 1,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val locationType: String

)
