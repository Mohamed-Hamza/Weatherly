package com.mohamedhamza.weatherly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_info")
class WeatherInfo(
    @PrimaryKey
    val id: Int,
    val city: String,
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val description: String,
    val icon: String
)