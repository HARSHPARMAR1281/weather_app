package com.example.weather_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.models.WeatherData
import com.example.weather_app.models.WeatherResponse
import com.example.weather_app.network.WeatherApiService
import com.example.weather_app.utils.MongoDBHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.UUID

class WeatherViewModel(private val apiService: WeatherApiService, private val mongoDBHelper: MongoDBHelper) : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val auth = FirebaseAuth.getInstance()

    fun getWeatherByCity(city: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getWeatherByCity(city)
                handleWeatherResponse(response)
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getWeatherByLocation(lat, lon)
                handleWeatherResponse(response)
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { weatherResponse ->
                val weatherData = WeatherData(
                    cityName = weatherResponse.cityName,
                    country = weatherResponse.sys.country,
                    temperature = weatherResponse.main.temperature,
                    feelsLike = weatherResponse.main.feelsLike,
                    humidity = weatherResponse.main.humidity,
                    windSpeed = weatherResponse.wind.speed,
                    description = weatherResponse.weather.firstOrNull()?.description?.capitalize() ?: "",
                    weatherId = weatherResponse.weather.firstOrNull()?.id ?: 800,
                    userId = auth.currentUser?.uid ?: ""
                )
                _weatherData.value = weatherData
            }
        } else {
            _error.value = "Failed to fetch weather data: ${response.code()}"
        }
    }

    fun saveWeatherData(weatherData: WeatherData) {
         viewModelScope.launch {
            try {
                mongoDBHelper.saveWeatherData(weatherData)
            } catch (e: Exception) {
                _error.postValue("Error saving weather data: ${e.message}")
            }
        }
    }

    fun getUserWeatherHistory(userId: String) {
        viewModelScope.launch {
            try {
                val history = mongoDBHelper.getUserWeatherHistory(userId)
                
            } catch (e: Exception) {
                _error.postValue("Error fetching weather history: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
} 
