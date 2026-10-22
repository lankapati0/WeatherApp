package com.example.weatherapp.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.CityWeather
import com.example.weatherapp.data.GeocodingApiService
import com.example.weatherapp.data.GeocodingResult
import com.example.weatherapp.data.RetrofitClient
import com.example.weatherapp.data.Room.SavedCity
import com.example.weatherapp.data.Room.SavedCityRepository
import com.example.weatherapp.data.WeatherApiResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherListViewModel(
    private val geocodingApi: GeocodingApiService,
    private val savedCityRepository: SavedCityRepository
): ViewModel() {
    val _cities = MutableStateFlow<List<GeocodingResult>>(emptyList())
    val cities: StateFlow<List<GeocodingResult>> =
        _cities.asStateFlow()
    val savedCities = savedCityRepository.getAllCities()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private var searchJob: Job? = null
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedCitiesWeather =
        MutableStateFlow<List<CityWeather>>(emptyList())

    val selectedCitiesWeather: StateFlow<List<CityWeather>> =
        _selectedCitiesWeather.asStateFlow()

    private val _loadingWeatherCities =
        MutableStateFlow<Set<String>>(emptySet())

    fun searchCities(query: String) {
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _cities.value = emptyList()
            _isLoading.value = false
            _error.value = null
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _isLoading.value = true
            _error.value = null

            try {
                val response = geocodingApi.searchCity(cityName = query.trim())
                _cities.value = response.results.orEmpty()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _cities.value = emptyList()
                _error.value = "Unable to search cities. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveCity(city: CityWeather) {
        viewModelScope.launch {
            savedCityRepository.saveCity(
                SavedCity(city.cityName, city.latitude, city.longitude)
            )
        }
    }

    fun loadSelectedCityWeather(cityName: String, latitude: Double, longitude: Double) {

        viewModelScope.launch {
            val cityKey = "$latitude,$longitude"

            if (_selectedCitiesWeather.value.any {
                    it.latitude == latitude &&
                            it.longitude == longitude
                } || cityKey in _loadingWeatherCities.value) {
                return@launch
            }

            _loadingWeatherCities.value = _loadingWeatherCities.value + cityKey

            try {
                // fetch fresh weather data
                val response = RetrofitClient.weatherApi.getCurrentWeather(latitude, longitude)

                val cityWeather = CityWeather(cityName, latitude, longitude, response)
                _selectedCitiesWeather.value += cityWeather

                // Add the card only if it isn't already present.
                if (_selectedCitiesWeather.value.none {
                        it.latitude == latitude &&
                                it.longitude == longitude
                    }
                ) {
                    _selectedCitiesWeather.value = _selectedCitiesWeather.value + cityWeather
                    savedCityRepository.saveCity(SavedCity(cityName, latitude, longitude))
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Unable to load weather for $cityName."
            } finally {
                _loadingWeatherCities.value =
                    _loadingWeatherCities.value - cityKey

            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _cities.value = emptyList()
        _isLoading.value = false
        _error.value = null
    }

    fun deleteCity(city: CityWeather) {
        viewModelScope.launch {
            // Delete the city from Room.
            savedCityRepository.deleteCity(
                latitude = city.latitude,
                longitude = city.longitude
            )
            // Remove it from the displayed list.
            _selectedCitiesWeather.update { cities ->
                cities.filterNot {
                    it.latitude == city.latitude &&
                            it.longitude == city.longitude
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val cities = savedCities.first()
            cities.forEach { city ->
                loadSelectedCityWeather(cityName = city.cityName, city.latitude, city.longitude)
            }
        }
    }


    fun refreshCitiesWeather() {
        viewModelScope.launch {
            _isRefreshing.value = true

            try {
                val savedCities = savedCityRepository.getAllCities().first()

                for (city in savedCities) {
                    try {
                        val response = RetrofitClient.weatherApi.getCurrentWeather(
                            city.latitude,
                            city.longitude
                        )

                        val updatedCity = CityWeather(
                            city.cityName,
                            city.latitude,
                            city.longitude,
                            response
                        )

                        _selectedCitiesWeather.value =
                            _selectedCitiesWeather.value.map { existing ->
                                if (
                                    existing.latitude == city.latitude &&
                                    existing.longitude == city.longitude
                                ) {
                                    updatedCity
                                } else {
                                    existing
                                }
                            }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _error.value =
                            "Unable to refresh weather for ${city.cityName}."
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }


}


