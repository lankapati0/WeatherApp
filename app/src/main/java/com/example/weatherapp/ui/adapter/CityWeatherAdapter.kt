package com.example.weatherapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.CityLocation
import com.example.weatherapp.data.CityWeather
import com.example.weatherapp.databinding.FragmentWeatherListBinding
import com.example.weatherapp.databinding.ItemSearchCityBinding
import com.example.weatherapp.utils.WeatherUtils
import com.example.weatherapp.utils.WeatherUtils.weatherDescription

class CityWeatherAdapter(
    private val onCityClick: (CityWeather) -> Unit
): RecyclerView.Adapter<CityWeatherAdapter.ViewHolder>() {
    private val cities = mutableListOf<CityWeather>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityWeatherAdapter.ViewHolder {
        val binding = ItemSearchCityBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding,onCityClick)
    }

    override fun onBindViewHolder(holder: CityWeatherAdapter.ViewHolder, position: Int) {
        Log.d("CityWeatherDebug", "Binding item at position $position")

        return holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size

    fun submitCities(newCities: List<CityWeather>) {
        cities.clear()
        cities.addAll(newCities)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemSearchCityBinding,
        private val onCityClick: (CityWeather) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: CityWeather){
            val current = city.weather.current
            val daily = city.weather.daily

            binding.tvCity.text = city.cityName
            binding.tvTemperature.text = "${current.temperature.toInt()}°"

            val high = daily.maxTemperatures.firstOrNull()?.toInt()
            val low = daily.minTemperatures.firstOrNull()?.toInt()

            binding.tvCardHighLow.text =
                "H: ${high ?: "--"}°  L: ${low ?: "--"}°"

            val weatherInfo = WeatherUtils.weatherDescription(current.weatherCode)


            binding.tvWeatherDescription.text = weatherInfo.description


            binding.ivTemperature.setImageResource(
                weatherInfo.iconRes
            )

        }
    }
}