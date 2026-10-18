package com.example.weatherapp.ViewModel

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.CurrentWeather
import com.example.weatherapp.data.ForecastCardItem
import com.example.weatherapp.data.GeocodingClient
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.utils.WeatherUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel: ViewModel() {
    private val repository = WeatherRepository()
    private val _weather = MutableLiveData<CurrentWeather>()

    val weather: LiveData<CurrentWeather> = _weather
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _hourlyForecast = MutableLiveData<List<ForecastCardItem>>()
    val hourlyForecast = _hourlyForecast


    private val _weatherDescription = MutableLiveData<String>()
    val weatherDescription: LiveData<String> = _weatherDescription

    private val _highLow = MutableLiveData<String>()
    val highLow: LiveData<String> = _highLow

    private val _dailyForecast = MutableLiveData<List<ForecastCardItem>>()
    val dailyForecast = _dailyForecast

    private var selectedLatitude = 22.3215099
    private var selectedLongitude = 73.1277504
    private var selectedCity = "Vadodara"


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadWeather(){
        viewModelScope.launch {
            _error.value = null
            try {
                val response = repository.getWeatherResponse(selectedLatitude,selectedLongitude)
                val current1 = response.current
                val daily = response.daily
//                _weather.value = repository.getCurrentWeather(
//                    latitude = 22.3215099,
//                    longitude = 73.1277504
//                )
                _weather.value = CurrentWeather(selectedCity,
                    current1.temperature,
                    current1.feelsLike,
                    current1.humidity,
                    current1.windSpeed

                )

                _weatherDescription.value = WeatherUtils.weatherDescription(current1.weatherCode)

                _highLow.value =  "H: ${daily.maxTemperatures[0].toInt()}°   " +
                        "L: ${daily.minTemperatures[0].toInt()}°"

                val hourly = response.hourly
                val currentTime = hourly.time.indexOfFirst {
                    it >= current1.time
                }.let {if (it<0)0 else it}

                _hourlyForecast.value = (currentTime until minOf(currentTime + 24, hourly.time.size)).map {index ->
                    ForecastCardItem(
                        label = WeatherUtils.formatTime(hourly.time[index]),
                        icon = WeatherUtils.getWeatherIcon(hourly.weatherCodes[index]),
                        detail = "${hourly.precipitationProbabilities[index]}%",
                        temperature = "${hourly.temperatures[index].toInt()}°"
                    )
                }



                _dailyForecast.value = daily.time.indices.map { index ->
                    val date = daily.time[index]
                    ForecastCardItem(
                        label = WeatherUtils.formatDay(date),
                        icon = WeatherUtils.getWeatherIcon(daily.weatherCodes[index]),
                        detail = WeatherUtils.formatDate(date),
                        temperature = "\${daily.maxTemperatures[index].toInt()}° / \${daily.minTemperatures[index].toInt()}°"

                    )
                }

            } catch (exception: Exception){
                _error.value = exception.message ?: "Unable to load weather"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchCity(cityName: String){

        if (cityName.isBlank()){
            _error.value = "Please enter a city name."
            return
        }

        viewModelScope.launch {
            _error.value = null
            try {
                val response = GeocodingClient.api.searchCity(cityName.trim())

                val city = response.results?.firstOrNull()
                if(city == null){
                    _error.value = "City not found. Try another name."
                    return@launch
                }

                selectedLatitude = city.latitude
                selectedLongitude = city.longitude
                selectedCity = city.name

                loadWeather()
            } catch (exception: Exception){
                Log.e("WeatherApp", "City search failed", exception)

                _error.value = exception.message ?: "Unknown error"

            }
        }
    }
}





