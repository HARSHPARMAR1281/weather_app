package com.example.weather_app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.example.weather_app.R
import com.example.weather_app.databinding.ActivityWeatherBinding
import com.example.weather_app.models.WeatherResponse
import com.example.weather_app.network.RetrofitClient
import com.example.weather_app.utils.Constants
import com.example.weather_app.utils.LocationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.weather_app.viewmodel.WeatherViewModel
import com.example.weather_app.viewmodel.WeatherViewModelFactory
import com.example.weather_app.models.WeatherData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.weather_app.utils.MongoDBHelper

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var locationHelper: LocationHelper
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mongoDBHelper: MongoDBHelper? = null
    private var isManualSearchActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            mongoDBHelper = MongoDBHelper(this)

            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Weather"

            fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
            locationHelper = com.example.weather_app.utils.LocationHelper(this)
            
            val factory = WeatherViewModelFactory(RetrofitClient.apiService, application)
            weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

            setupClickListeners()

            binding.refreshButton.setOnClickListener {
                isManualSearchActive = false 
                checkLocationPermission()
            }
            
            weatherViewModel.weatherData.observe(this) { weatherData ->
                weatherData?.let { updateWeatherUI(it) }
            }
            weatherViewModel.error.observe(this) { errorMsg ->
                errorMsg?.let { showError(it) }
            }

            checkLocationPermission()

        } catch (e: Exception) {
            Log.e("WeatherActivity", "Error initializing WeatherActivity", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupClickListeners() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val cityName = binding.etSearch.text.toString()
                if (cityName.isNotEmpty()) {
                    isManualSearchActive = true 
                    fetchWeatherByCity(cityName)
                } else {
                    isManualSearchActive = false 
                    checkLocationPermission() 
                }
                true
            } else {
                false
            }
        }

        binding.btnLocation.setOnClickListener {
            isManualSearchActive = false 
            checkLocationPermission() 
        }
    }

    private fun checkLocationPermission() {
        lifecycleScope.launch {
            if (locationHelper.hasLocationPermission()) {
                val lastLocation = locationHelper.getLastLocation()
                if (lastLocation != null) {
                    fetchWeatherByLocation(lastLocation.latitude, lastLocation.longitude)
                }
                startLocationUpdates()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission()
            } else {
                Toast.makeText(this, "Location permission required for weather updates", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startLocationUpdates() {

        lifecycleScope.launch {
            locationHelper.getLocationUpdates()
                .collectLatest { location ->
                    if (!isManualSearchActive) {
                        fetchWeatherByLocation(location.latitude, location.longitude)
                    }
                }
        }
    }

    private fun fetchWeatherByLocation(latitude: Double, longitude: Double) {
        showLoading(true)
        weatherViewModel.fetchWeatherByLocation(latitude, longitude)
    }

    private fun fetchWeatherByCity(cityName: String) {
        showLoading(true)
        weatherViewModel.getWeatherByCity(cityName)
    }

    private fun updateWeatherUI(weatherData: WeatherData) {
        try {
            binding.locationTextView.text = "${weatherData.cityName}, ${weatherData.country}"

            binding.temperatureTextView.text = getString(R.string.temperature_format, weatherData.temperature.toInt())
            binding.weatherDescriptionTextView.text = weatherData.description

            binding.humidityTextView.text = getString(R.string.humidity_format, weatherData.humidity)
            binding.windSpeedTextView.text = getString(R.string.wind_speed_format, weatherData.windSpeed)
            binding.feelsLikeTextView.text = getString(R.string.temperature_format, weatherData.feelsLike.toInt())

            saveWeatherDataToMongoDB(weatherData)

            updateBackground(weatherData.weatherId)

            setWeatherAnimation(weatherData.weatherId)

            binding.progressBar.visibility = View.GONE
            binding.errorTextView.visibility = View.GONE
        } catch (e: Exception) {
            Log.e("WeatherActivity", "Error updating UI", e)
            showError("Error updating weather display")
        }
    }

    private fun saveWeatherDataToMongoDB(weatherData: WeatherData) {
        lifecycleScope.launch {
            try {
                mongoDBHelper?.saveWeatherData(weatherData)
            } catch (e: Exception) {
                Log.e("WeatherActivity", "Error saving weather data to Realm", e)
            }
        }
    }

    private fun updateBackground(weatherId: Int) {
        val backgroundDrawable = when {
            weatherId in 200..232 -> R.drawable.weather_background_rainy
            weatherId in 300..321 -> R.drawable.weather_background_drizzle
            weatherId in 500..531 -> R.drawable.weather_background_rainy
            weatherId in 600..622 -> R.drawable.weather_background_snowy
            weatherId in 701..781 -> R.drawable.weather_background_foggy
            weatherId == 800 -> R.drawable.weather_background_sunny
            weatherId in 801..804 -> R.drawable.weather_background_cloudy
            else -> R.drawable.weather_background
        }

        binding.root.setBackgroundResource(backgroundDrawable)
    }

    private fun setWeatherAnimation(weatherId: Int) {
        try {
            val animationFile = when {
                weatherId in 200..232 -> "thunderstorm.json"
                weatherId in 300..321 -> "drizzle.json"
                weatherId in 500..531 -> "rainy.json"
                weatherId in 600..622 -> "snowy.json"
                weatherId in 701..781 -> "foggy.json"
                weatherId == 800 -> "sunny.json"
                weatherId in 801..804 -> "cloudy.json"
                else -> "sunny.json"
            }

            binding.weatherAnimationView.setAnimation(animationFile)
            binding.weatherAnimationView.playAnimation()
            binding.weatherAnimationView.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e("WeatherActivity", "Error setting weather animation", e)
            binding.weatherAnimationView.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_weather, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mongoDBHelper?.close()
        } catch (e: Exception) {
            Log.e("WeatherActivity", "Error closing MongoDB connection", e)
        }
    }
} 
