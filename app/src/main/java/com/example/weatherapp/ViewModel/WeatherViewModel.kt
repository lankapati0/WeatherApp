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

    private val _airQuality = MutableLiveData<Int>()
    val airQuality: MutableLiveData<Int> = _airQuality

    private val _uvIndex = MutableLiveData<Double>()
    val uvIndex: MutableLiveData<Double> = _uvIndex

    private val _rainfall = MutableLiveData<Double>()
    val rainFall: MutableLiveData<Double> = _rainfall

    private val _sunrise = MutableLiveData<String>()
    val sunrise : MutableLiveData<String> = _sunrise

    private val _sunset = MutableLiveData<String>()
    val sunset : MutableLiveData<String> = _sunset

    private val _dailyMaxUv = MutableLiveData<Double>()
    val dailyMaxUv: LiveData<Double> = _dailyMaxUv

    private val _dailyRainfall = MutableLiveData<Double>()
    val dailyRainfall: LiveData<Double> = _dailyRainfall

    private val _maxWindSpeed = MutableLiveData<Double>()
    val maxWindSpeed: LiveData<Double> = _maxWindSpeed

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadWeather(latitude:Double, longitude:Double){
        viewModelScope.launch {
            _error.value = null
            try {
                val response = repository.getWeatherResponse(latitude,longitude)

                val airQualityResponse = repository.getAirquality(latitude,longitude)

                val current1 = response.current
                val daily = response.daily
               _weather.value = CurrentWeather(selectedCity,
                    current1.temperature,
                    current1.feelsLike,
                    current1.humidity,
                    current1.windSpeed,
                    current1.windDirection
                )

                _airQuality.value = airQualityResponse.current.usAqi
                val desc = WeatherUtils.weatherDescription(current1.weatherCode)

                _weatherDescription.value = desc.description.toString()
                _uvIndex.value = current1.uvIndex
                _rainfall.value = current1.rain
                _sunrise.value = WeatherUtils.formatSunTime(daily.sunrise[0])
                _sunset.value = WeatherUtils.formatSunTime(daily.sunset[0])
                _dailyMaxUv.value = daily.uvIndexMax[0]
                _dailyRainfall.value = daily.rainSum[0]
                _maxWindSpeed.value = daily.maxWindSpeeds[0]

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
                        temperature = "${daily.maxTemperatures[index].toInt()}° / ${daily.minTemperatures[index].toInt()}°"

                    )

                }

                val hourly1 = response.hourly
                val lastHourIndex = hourly1.time.indexOfLast { timing ->
                    timing <= current1.time
                }

                if(lastHourIndex >= 0){
                    val lastHourData = hourly1.rainfall[lastHourIndex]
                    _rainfall.value = lastHourData
                }

            } catch (exception: Exception){
                _error.value = exception.message ?: "Unable to load weather"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectCity(
        cityName: String,
        latitude: Double,
        longitude: Double
    ){
        selectedCity = cityName
        selectedLatitude = latitude
        selectedLongitude = longitude

        loadWeather(latitude,longitude)
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

                loadWeather(selectedLatitude,selectedLongitude)

            } catch (exception: Exception){
                Log.e("WeatherApp", "City search failed", exception)

                _error.value = exception.message ?: "Unknown error"

            }
        }
    }
}