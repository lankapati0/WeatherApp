package com.example.weatherapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.GeocodingApiService
import com.example.weatherapp.data.Room.SavedCityRepository

class WeatherListViewModelFactory(

    private val geocodingApi: GeocodingApiService,
    private val savedCityRepository: SavedCityRepository): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(
        modelClass:Class<T>
    ):T{
        if(modelClass.isAssignableFrom(WeatherListViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return WeatherListViewModel(geocodingApi,savedCityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}