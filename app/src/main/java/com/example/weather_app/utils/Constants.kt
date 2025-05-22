package com.example.weather_app.utils

import com.example.weather_app.BuildConfig

object Constants {

    const val OPENWEATHER_API_KEY = BuildConfig.OPENWEATHER_API_KEY
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    

    const val PREF_NAME = "WeatherAppPrefs"
    const val KEY_IS_LOGGED_IN = "isLoggedIn"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_FAVORITE_CITIES = "favoriteCities"
    

    const val EXTRA_CITY_NAME = "cityName"
    const val EXTRA_LATITUDE = "latitude"
    const val EXTRA_LONGITUDE = "longitude"
    
    // Animation Durations
    const val SPLASH_DELAY = 2000L // 2 seconds
    
    // Weather Units
    const val TEMP_UNIT = "metric" // Celsius
    const val WIND_UNIT = "metric" // m/s
    
    // Location
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val DEFAULT_LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
    const val DEFAULT_LOCATION_FASTEST_INTERVAL = 5000L // 5 seconds
    
    // MongoDB Configuration
    const val MONGODB_USERNAME = "parmarharsh1281"
    const val MONGODB_PASSWORD = "hloindia@123"
    const val MONGODB_CLUSTER = "cluster0.hxgdujd"
    const val MONGODB_DATABASE = "weather_data"
    
    // Construct MongoDB connection string
    val MONGODB_CONNECTION_STRING = "mongodb+srv://$MONGODB_USERNAME:$MONGODB_PASSWORD@$MONGODB_CLUSTER.mongodb.net/$MONGODB_DATABASE?retryWrites=true&w=majority&appName=Cluster0"
} 