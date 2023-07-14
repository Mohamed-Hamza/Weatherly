package com.mohamedhamza.weatherly.data

import androidx.lifecycle.LiveData
import com.mohamedhamza.weatherly.model.CurrentWeatherInfo
import kotlinx.coroutines.flow.Flow

interface RemoteSource {
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherInfo
}