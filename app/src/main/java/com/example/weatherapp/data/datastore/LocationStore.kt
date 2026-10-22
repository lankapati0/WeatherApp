package com.example.weatherapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.selectedLocationDataStore by preferencesDataStore(name = "selected_location")
class LocationStore(private val context: Context) {
    private val latitudeKey =
        doublePreferencesKey("selected_latitude")

    private val longitudeKey =
        doublePreferencesKey("selected_longitude")

    val savedCoordinates: Flow<Pair<Double, Double>?> =
        context.selectedLocationDataStore.data.map { preferences ->

            val latitude = preferences[latitudeKey]
            val longitude = preferences[longitudeKey]

            if (latitude != null && longitude != null) {
                latitude to longitude
            } else {
                null
            }
        }

     suspend fun saveCoordinates(
        latitude: Double,
        longitude: Double
    ) {
        context.selectedLocationDataStore.edit { preferences ->
            preferences[latitudeKey] = latitude
            preferences[longitudeKey] = longitude
        }
    }
}