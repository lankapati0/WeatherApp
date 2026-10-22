package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ViewModel.WeatherListViewModel
import com.example.weatherapp.ViewModel.WeatherListViewModelFactory
import com.example.weatherapp.data.CityWeather
import com.example.weatherapp.data.RetrofitClient
import com.example.weatherapp.data.Room.SavedCityRepository
import com.example.weatherapp.data.Room.WeatherDatabase
import com.example.weatherapp.databinding.FragmentWeatherListBinding
import com.example.weatherapp.ui.adapter.CitySuggestionAdapter
import com.example.weatherapp.ui.adapter.CityWeatherAdapter
import kotlinx.coroutines.launch

class WeatherListFragment : Fragment() {

    private var _binding: FragmentWeatherListBinding? = null
    private lateinit var searchCityAdapter: CitySuggestionAdapter
    private val binding get() = _binding!!
    private lateinit var cityWeatherAdapter: CityWeatherAdapter
    private val viewModel: WeatherListViewModel by viewModels {
        val database = WeatherDatabase.getDatabase(requireContext())

        val repository = SavedCityRepository(database.savedCityDao())

        WeatherListViewModelFactory(geocodingApi = RetrofitClient.geocodingApi,
            savedCityRepository = repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCitySuggestions()
        setupWeatherCards()
        setupSearch()
        observeCities()
        observeSelectedCitiesWeather()
    }
    fun setupCitySuggestions() {
        searchCityAdapter = CitySuggestionAdapter { city ->

            // Fill the selected city name into the search box.
            binding.etSearchCity.setText(city.name)

            // Hide search suggestions.
            binding.rvSearchCityList.visibility = View.GONE

            // Fetch weather using the selected city's coordinates.
            viewModel.loadSelectedCityWeather(
                cityName = city.name,
                latitude = city.latitude,
                longitude = city.longitude
            )
        }
        binding.rvSearchCityList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchCityAdapter
        }


    }

    private fun setupWeatherCards(){
        cityWeatherAdapter = CityWeatherAdapter{ city ->
            val navController = findNavController()

            navController.previousBackStackEntry?.savedStateHandle?.set("selected_city",city.cityName)

            navController.previousBackStackEntry?.savedStateHandle?.set("selected_latitude",city.latitude)

            navController.previousBackStackEntry?.savedStateHandle?.set("selected_longitude",city.longitude)

            navController.popBackStack()
        }
        binding.rvSearchCityWeather.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cityWeatherAdapter
        }

    }

    fun setupSearch(){
        binding.etSearchCity.doAfterTextChanged { editable ->

            val query = editable?.toString().orEmpty()

            if (query.isBlank()) {
                binding.rvSearchCityList.visibility = View.GONE
                viewModel.clearSearch()
            } else {
                binding.rvSearchCityList.visibility = View.VISIBLE
                viewModel.searchCities(query)
            }
        }
    }

    private fun observeSelectedCitiesWeather(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.selectedCitiesWeather.collect { cities ->
                    Log.d(
                        "CityWeatherDebug",
                        "Observer received ${cities.size} cities"
                    )
                    // cityWeatherAdapter is initialized before this runs.
                    cityWeatherAdapter.submitCities(cities)
                    Log.d(
                        "CityWeatherDebug",
                        "Adapter item count: ${cityWeatherAdapter.itemCount}"
                    )
                    binding.rvSearchCityWeather.post {
                        Log.d(
                            "CityWeatherDebug",
                            "Weather RecyclerView: " +
                                    "visibility=${binding.rvSearchCityWeather.visibility}, " +
                                    "width=${binding.rvSearchCityWeather.width}, " +
                                    "height=${binding.rvSearchCityWeather.height}"
                        )
                    }
                    binding.rvSearchCityWeather.visibility =
                        if (cities.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun observeCities(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.cities.collect { cities ->
                    searchCityAdapter.submitCities(cities)

                    binding.rvSearchCityList.visibility = View.VISIBLE
//                    binding.rvSearchCityList.visibility =
//                        if(cities.isNotEmpty()) View.GONE else View.VISIBLE


                }
            }
        }

        // Observe search errors separately.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.error.collect { error ->

                    if (error != null) {
                        Toast.makeText(
                            requireContext(),
                            error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}