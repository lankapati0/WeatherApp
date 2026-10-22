package com.example.weatherapp.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {

    @Query("SELECT * from saved_cities")
    fun getAllCities(): Flow<List<SavedCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCity)

    @Query("DELETE FROM saved_cities " +
            "WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun deleteCity(latitude:Double, longitude: Double)

    @Query("DELETE FROM saved_cities")
    suspend fun deleteAllCities()
}
