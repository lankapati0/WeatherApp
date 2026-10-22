package com.example.weatherapp

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.weatherapp.ViewModel.WeatherViewModel
import com.example.weatherapp.data.Room.SelectedLocation
import com.example.weatherapp.data.Room.SelectedLocationRepository
import com.example.weatherapp.data.Room.WeatherDatabase
import com.example.weatherapp.data.datastore.LocationStore
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.databinding.BottomNavigationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomBinding: BottomNavigationBinding

    private val selectedLocationRepository by lazy {
        val database = WeatherDatabase.getDatabase(applicationContext)
        SelectedLocationRepository(database.selectedLocationDao())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                checkLocationSettings()
            } else {
                Toast.makeText(
                    this,
                    "Permission not granted!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private val locationSettingsLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            // Check again after the user returns from Settings.
            getCurrentLocation()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val coordinates = selectedLocationRepository.getSelectedLocation()
                .first()

            if (coordinates != null) {
                viewModel.selectCity(cityName = coordinates.cityName,latitude=coordinates.latitude,longitude= coordinates.longitude)
            } else {
                viewModel.selectCity("Vadodara",22.3215099, 73.1277504)
            }
        }


        bottomBinding = BottomNavigationBinding.bind(
            findViewById(R.id.bottomNavigation)
        )
        bottomBinding.btnRefresh.setOnClickListener {
            showCitySearchDialog()
        }

        bottomBinding.ivLocation.setOnClickListener {
/*            findNavController(R.id.navHostFragment)
                .navigate(R.id.weatherFragment)*/
            val hasFineLocation = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarseLocation = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFineLocation || hasCoarseLocation) {
                checkLocationSettings()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }


        bottomBinding.btnForecastList.setOnClickListener {
            findNavController(R.id.navHostFragment)
                .navigate(R.id.weatherListFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation(){
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        val hasFineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {

                val latitude = location.latitude
                val longitude = location.longitude
                lifecycleScope.launch {

                    val cityName = withContext(Dispatchers.IO){
                        try {
                            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())

                            @Suppress("DEPRECATION")
                            geocoder.getFromLocation(latitude, longitude, 1)
                                ?.firstOrNull()
                                ?.let { address ->
                                    address.subLocality
                                        ?: address.locality
                                        ?: address.subAdminArea
                                        ?: address.adminArea
                                }
                        } catch (e: Exception){
                            null
                        }
                    }?:"Current Location"

                    val currentLocation = SelectedLocation(cityName=cityName,latitude=latitude, longitude = longitude,locationType="DEVICE")

                    selectedLocationRepository.saveSelectedLocation(currentLocation)

                    viewModel.selectCity(currentLocation.cityName,currentLocation.latitude, currentLocation.longitude)
                }
            } else {
                Toast.makeText(
                    this,
                    "Unable to get location. Try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener { error ->

            Toast.makeText(this,"Location error:${error.message}", Toast.LENGTH_SHORT).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkLocationSettings(){
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000L).build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                getCurrentLocation()
            }.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val request = IntentSenderRequest.Builder(
                            exception.resolution
                        ).build()

                        locationSettingsLauncher.launch(request)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Please enable Location in phone settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please enable Location in phone settings.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
