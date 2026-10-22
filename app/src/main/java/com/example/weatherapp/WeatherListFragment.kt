package com.example.weatherapp

import android.graphics.Canvas
import android.graphics.Paint
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.ViewModel.WeatherListViewModel
import com.example.weatherapp.databinding.FragmentWeatherListBinding
import com.example.weatherapp.ui.adapter.CitySuggestionAdapter
import com.example.weatherapp.ui.adapter.CityWeatherAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class WeatherListFragment : Fragment() {

    private var _binding: FragmentWeatherListBinding? = null
    private lateinit var searchCityAdapter: CitySuggestionAdapter
    private val binding get() = _binding!!
    private lateinit var cityWeatherAdapter: CityWeatherAdapter
    private val viewModel: WeatherListViewModel by viewModels()

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
        setupSwipeToDelete()
        viewModel.refreshCitiesWeather()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.isRefreshing.collect { refreshing ->
                    binding.swrCityWeather.isRefreshing = refreshing
                }
            }
        }
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
                    // cityWeatherAdapter is initialized before this runs.
                    cityWeatherAdapter.submitCities(cities)
                    binding.rvSearchCityWeather.post {
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

    private fun setupSwipeToDelete() {
        val swipeCallback =
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                private val deletePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                    textSize = 42f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                private val backgroundPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.rgb(220, 45, 55)
                    isAntiAlias = true
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView

                    if (dX < 0) {

                        val background = android.graphics.RectF(
                            itemView.right + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )

                        c.drawRoundRect(background, 16f, 16f, backgroundPaint)

                        val centerX = background.centerX()
                        val centerY = background.centerY()

                        c.drawText(
                            "Delete",
                            centerX,
                            centerY + deletePaint.textSize / 3,
                            deletePaint
                        )
                    }

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.bindingAdapterPosition

                    if (position != RecyclerView.NO_POSITION) {
                        val city = cityWeatherAdapter.getCityAt(position)
                        viewModel.deleteCity(city)
                    }
                }
            }

        ItemTouchHelper(swipeCallback)
            .attachToRecyclerView(binding.rvSearchCityWeather)
    }
}