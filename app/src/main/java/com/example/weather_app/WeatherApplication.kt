package com.example.weather_app

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.example.weather_app.utils.MongoDBHelper

class WeatherApplication : MultiDexApplication() {

    lateinit var mongoDBHelper: MongoDBHelper
        private set

    override fun onCreate() {
        super.onCreate()
        
        mongoDBHelper = MongoDBHelper(applicationContext)
    }
} 
