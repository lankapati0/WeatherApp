package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.databinding.FragmentForecastBinding
import com.example.weatherapp.ui.adapter.ForecastCardAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment: Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by activityViewModels()

    private lateinit var hourlyAdapter: ForecastCardAdapter
    private lateinit var dailyAdapter: ForecastCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.weather.observe(viewLifecycleOwner){weather ->
            binding.tvForecastCity.text = weather.cityName
        }

        setupForecastLists()
        observeForecastData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupForecastLists(){
        hourlyAdapter = ForecastCardAdapter(R.layout.item_hourly_forecast)
        dailyAdapter = ForecastCardAdapter(R.layout.item_daily_forecast)

        binding.rvForecastHourly.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false)

        binding.rvForecastHourly.adapter = hourlyAdapter

        binding.rvForecastWeekly.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)

        binding.rvForecastWeekly.adapter = dailyAdapter
    }

    private fun observeForecastData(){
        viewModel.weather.observe(viewLifecycleOwner){ weather ->
            binding.tvForecastCity.text = weather.cityName
        }

        viewModel.hourlyForecast.observe(viewLifecycleOwner){ forecasts ->
            hourlyAdapter.submitItems(forecasts)
        }

        viewModel.dailyForecast.observe(viewLifecycleOwner){ daily ->
            dailyAdapter.submitItems(daily)
        }
    }

}