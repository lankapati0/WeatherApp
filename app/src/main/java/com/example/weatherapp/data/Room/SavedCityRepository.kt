package com.example.weatherapp.data.Room

import kotlinx.coroutines.flow.Flow

class SavedCityRepository(
    private val savedCityDao: SavedCityDao
) {
    fun getAllCities(): Flow<List<SavedCity>>{
        return savedCityDao.getAllCities()
    }

    suspend fun saveCity(city: SavedCity){
        savedCityDao.insertCity(city)
    }

    suspend fun deleteCity(latitude: Double, longitude: Double){
        savedCityDao.deleteCity(latitude,longitude)
    }
}