package com.example.weatherapp.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SelectedLocationDao {

    @Query("SELECT * FROM selected_location WHERE id = 1")
    fun getSelectedLocation(): Flow<SelectedLocation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSelectedLocation(location: SelectedLocation)
}