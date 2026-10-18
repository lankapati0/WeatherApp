package com.example.weatherapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.databinding.BottomNavigationBinding
import com.example.weatherapp.ui.adapter.ForecastCardAdapter

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomBinding: BottomNavigationBinding

    init {
        Log.d("WeatherDebug", "WeatherViewModel created")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomBinding = BottomNavigationBinding.bind(
            findViewById(R.id.bottomNavigation)
        )
        bottomBinding.btnRefresh.setOnClickListener {
            showCitySearchDialog()
        }

        bottomBinding.ivLocation.setOnClickListener {
            findNavController(R.id.navHostFragment)
                .navigate(R.id.weatherFragment)
        }

        bottomBinding.btnForecastList.setOnClickListener {
            findNavController(R.id.navHostFragment)
                .navigate(R.id.forecastFragment)
        }
    }

    private fun showCitySearchDialog(){
        val input = EditText(this).apply {
            hint = "Enter city name"
            setSingleLine(true)
            setPadding(48, 24, 48, 24)
        }

        AlertDialog.Builder(this)
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
                        this,
                        "Enter at least 2 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}
