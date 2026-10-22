package com.example.weatherapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.utils.AqiUtils
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.ui.adapter.ForecastCardAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

class WeatherFragment: Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var hourlyAdapter: ForecastCardAdapter
    private lateinit var dailyAdapter: ForecastCardAdapter
    private val viewModel: WeatherViewModel by activityViewModels()
    private val weatherViewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater,container,false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = binding.weatherBottomSheet.root
        val params = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams

        params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT
        params.marginStart = 0
        params.leftMargin = 0
        params.marginEnd = 0
        params.rightMargin = 0

        val bottomSheetBehavior = BottomSheetBehavior<ConstraintLayout>()
        params.behavior = bottomSheetBehavior
        bottomSheet.layoutParams = params
        bottomSheetBehavior.peekHeight = 370.dpToPx()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        setupForecastLists()
        observeWeather()
        observeSelectedCity()
        setupClickListeners()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun setupForecastLists() {
        hourlyAdapter =
            ForecastCardAdapter(R.layout.item_hourly_forecast)

        dailyAdapter =
            ForecastCardAdapter(R.layout.item_daily_forecast)

        binding.weatherBottomSheet.rvHourlyForecast.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )


        binding.weatherBottomSheet.rvHourlyForecast.adapter = hourlyAdapter

        viewModel.airQuality.observe(viewLifecycleOwner){ aqi ->
            binding.weatherBottomSheet.tvUVIndex.text = aqi.toString()
            val aqiResultDesc = AqiUtils.getCategory(aqi)
            binding.weatherBottomSheet.tvSampleTextProgress.text = aqiResultDesc.level
            binding.weatherBottomSheet.progressAirQuality.setProgress(aqi.toFloat(),400f)

        }

        viewModel.uvIndex.observe(viewLifecycleOwner){uv ->

            val uvInfo = AqiUtils.getUVIndexDesc(uv.toFloat())
            binding.weatherBottomSheet.tvUVIndex.text = uv.toString()
            binding.weatherBottomSheet.tvUVIndexDetail.text = uvInfo.level
            binding.weatherBottomSheet.progressUVIndex.setProgress(uv.toFloat(),15f)
        }

        viewModel.sunrise.observe(this){ time ->
            binding.weatherBottomSheet.tvSunriseTime.text = time
        }

        viewModel.sunset.observe(this){ time ->
            binding.weatherBottomSheet.tvSunsettime.text = "Sunset: $time"
        }

        viewModel.rainFall.observe(this){rainfall ->
            binding.weatherBottomSheet.tvRainfall.text = String.format("%.1f mm in last hour",rainfall)
        }

        binding.weatherBottomSheet.rvDailyForecast.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

        binding.weatherBottomSheet.rvDailyForecast.adapter = dailyAdapter
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeSelectedCity() {
        val navController = findNavController()

        val handle = navController.currentBackStackEntry
            ?.savedStateHandle ?: return

        handle.getLiveData<Double>("selected_latitude")
            .observe(viewLifecycleOwner) { latitude ->

                val longitude =
                    handle.get<Double>("selected_longitude") ?: return@observe

                val cityName =
                    handle.get<String>("selected_city") ?: "Selected city"

                // Load weather for the tapped city here.
                viewModel.selectCity(
                    cityName = cityName,
                    latitude = latitude,
                    longitude = longitude
                )

                handle.remove<Double>("selected_latitude")
                handle.remove<Double>("selected_longitude")
                handle.remove<String>("selected_city")
            }
    }
    private fun observeWeather() {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            binding.tvLocation.text = weather.cityName
            binding.tvTemperature.text =
                "${weather.temperature.toInt()}°"

            binding.weatherBottomSheet.tvWind.setWindData(weather.windSpeed.toString(),"KM/h",weather.windDirection.toFloat())
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
            Log.d("WeatherDebug", "Fragment received daily items: ${it.size}")

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



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupClickListeners() {
        binding.tvLocation.setOnClickListener {
            viewModel.loadWeather(22.3215099,
                73.1277504)
        }

        binding.weatherBottomSheet.tvHourlyForecast.setOnClickListener {
            binding.weatherBottomSheet.rvHourlyForecast.visibility = View.VISIBLE
            binding.weatherBottomSheet.rvDailyForecast.visibility = View.GONE
            binding.weatherBottomSheet.cardAQI.visibility = View.VISIBLE
            binding.weatherBottomSheet.llUVIndexSunrise.visibility = View.VISIBLE
            binding.weatherBottomSheet.llWindRainfall.visibility = View.VISIBLE
        }

        binding.weatherBottomSheet.tvWeeklyForecast.setOnClickListener {
            binding.weatherBottomSheet.rvHourlyForecast.visibility = View.GONE
            binding.weatherBottomSheet.rvDailyForecast.visibility = View.VISIBLE
            binding.weatherBottomSheet.cardAQI.visibility = View.GONE
            binding.weatherBottomSheet.llUVIndexSunrise.visibility = View.GONE
            binding.weatherBottomSheet.llWindRainfall.visibility = View.GONE

            binding.weatherBottomSheet.rvDailyForecast.post {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
private fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

