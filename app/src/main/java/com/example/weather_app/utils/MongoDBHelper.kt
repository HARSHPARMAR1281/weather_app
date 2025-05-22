package com.example.weather_app.utils

import android.content.Context
import com.example.weather_app.models.WeatherData
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MongoDBHelper(private val context: Context? = null) {
    private var realm: Realm? = null

    init {
        val config = RealmConfiguration.Builder(schema = setOf(WeatherData::class))
            .name("weather.realm")
            .build()
        realm = Realm.open(config)
    }

    suspend fun saveWeatherData(weatherData: WeatherData) = withContext(Dispatchers.IO) {
        realm?.write {
            copyToRealm(weatherData)
        }
    }

    suspend fun getUserWeatherHistory(userId: String): List<WeatherData> = withContext(Dispatchers.IO) {
        realm?.query<WeatherData>("userId == $0", userId)
            ?.sort("timestamp", Sort.DESCENDING)
            ?.find()
            ?.toList() ?: emptyList()
    }

    suspend fun getCityWeatherHistory(cityName: String): List<WeatherData> = withContext(Dispatchers.IO) {
        realm?.query<WeatherData>("cityName == $0", cityName)
            ?.sort("timestamp", Sort.DESCENDING)
            ?.find()
            ?.toList() ?: emptyList()
    }

    fun close() {
        realm?.close()
    }
} 