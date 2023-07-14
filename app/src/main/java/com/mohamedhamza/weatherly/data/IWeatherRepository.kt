package com.mohamedhamza.weatherly.data

import androidx.lifecycle.LiveData
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.WeatherDataResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getWholeWeatherResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherDataResponse?

    suspend fun getTimeZoneOfLocation(lat: Double, lon: Double): Pair<String, Int>?

    suspend fun getFavouriteLocations(): Flow<List<FavouriteLocation>>

    suspend fun insertLocation(favouriteLocation: FavouriteLocation)

    suspend fun deleteLocation(favouriteLocation: FavouriteLocation)

    suspend fun deleteAllLocations()
    fun getAlerts(): LiveData<List<Alert>>

    suspend fun insertAlert(alert: Alert)

    suspend fun deleteAlert(alert: Alert)

    suspend fun deleteAllAlerts()

    suspend fun getAlertById(id: Int): Alert
}

