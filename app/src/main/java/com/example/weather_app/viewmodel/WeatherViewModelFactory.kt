package com.example.weather_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_app.WeatherApplication
import com.example.weather_app.network.WeatherApiService
import com.example.weather_app.utils.MongoDBHelper

class WeatherViewModelFactory(private val apiService: WeatherApiService, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val mongoDBHelper = (application as WeatherApplication).mongoDBHelper
            return WeatherViewModel(apiService, mongoDBHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 