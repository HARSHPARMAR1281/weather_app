package com.example.weather_app.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class WeatherData(
    @PrimaryKey
    var _id: ObjectId? = null,
    var cityName: String = "",
    var country: String = "",
    var temperature: Double = 0.0,
    var feelsLike: Double = 0.0,
    var humidity: Int = 0,
    var windSpeed: Double = 0.0,
    var description: String = "",
    var weatherId: Int = 0,
    var timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    var userId: String = "" // To associate weather data with Firebase user
) : RealmObject {
    constructor() : this(null) // Required no-arg constructor for Realm 
} 