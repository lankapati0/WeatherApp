package com.example.weatherapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.ui.adapter.ForecastCardAdapter

class WeatherFragment: Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var hourlyAdapter: ForecastCardAdapter
    private lateinit var dailyAdapter: ForecastCardAdapter
    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupForecastLists()
        observeWeather()
        setupClickListeners()

        if(savedInstanceState == null){
            viewModel.loadWeather()
        }
    }

    private fun setupForecastLists() {
        hourlyAdapter =
            ForecastCardAdapter(R.layout.item_hourly_forecast)

        dailyAdapter =
            ForecastCardAdapter(R.layout.item_daily_forecast)

        binding.rvHourlyForecast.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvHourlyForecast.adapter = hourlyAdapter

        binding.rvDailyForecast.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

        binding.rvDailyForecast.adapter = dailyAdapter
    }

    private fun observeWeather() {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            binding.tvLocation.text = weather.cityName
            binding.tvTemperature.text =
                "${weather.temperature.toInt()}°"
        }

        viewModel.weatherDescription.observe(viewLifecycleOwner) {
            binding.tvCondition.text = it
        }

        viewModel.highLow.observe(viewLifecycleOwner) {
            binding.tvHighLow.text = it
        }

        viewModel.hourlyForecast.observe(viewLifecycleOwner) {
            hourlyAdapter.submitItems(it)
        }

        viewModel.dailyForecast.observe(viewLifecycleOwner) {
            dailyAdapter.submitItems(it)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun setupClickListeners() {
        binding.tvLocation.setOnClickListener {
            viewModel.loadWeather()
        }





        binding.tvHourlyForecast.setOnClickListener {
            binding.rvHourlyForecast.visibility = View.VISIBLE
            binding.rvDailyForecast.visibility = View.GONE
        }

        binding.tvWeeklyForecast.setOnClickListener {
            binding.rvHourlyForecast.visibility = View.GONE
            binding.rvDailyForecast.visibility = View.VISIBLE
        }
    }

    private fun showCitySearchDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter city name"
            setSingleLine(true)
            setPadding(48, 24, 48, 24)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add a city")
            .setMessage("Enter the city you want weather for.")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Search") { _, _ ->
                val cityName = input.text.toString().trim()

                if (cityName.length >= 2) {
                    viewModel.searchCity(cityName)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Enter at least 2 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}