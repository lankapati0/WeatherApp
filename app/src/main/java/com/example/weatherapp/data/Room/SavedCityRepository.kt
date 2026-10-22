package com.example.weatherapp.data.Room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedCityRepository @Inject constructor(
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

class SelectedLocationRepository @Inject constructor(
    private val selectedLocationDao: SelectedLocationDao
){
    fun getSelectedLocation(): Flow<SelectedLocation?>{
        return selectedLocationDao.getSelectedLocation()
    }

    suspend fun saveSelectedLocation(location: SelectedLocation){
        selectedLocationDao.saveSelectedLocation(location)
    }
}