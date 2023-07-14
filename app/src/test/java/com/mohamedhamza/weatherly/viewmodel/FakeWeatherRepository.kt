package com.mohamedhamza.weatherly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamedhamza.weatherly.data.IWeatherRepository
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.WeatherDataResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepository: IWeatherRepository {

    private val favoriteLocationsFlow = MutableStateFlow<List<FavouriteLocation>>(emptyList())
    private val alertsLiveData = MutableLiveData<List<Alert>>(emptyList())
    private val alertsList = mutableListOf<Alert>()


    override suspend fun getWholeWeatherResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherDataResponse? {
        return null
    }

    override suspend fun getTimeZoneOfLocation(lat: Double, lon: Double): Pair<String, Int>? {
        TODO("Not yet implemented")
    }

    override suspend fun getFavouriteLocations(): Flow<List<FavouriteLocation>> {
        return favoriteLocationsFlow
    }

    override suspend fun insertLocation(favouriteLocation: FavouriteLocation) {
        val currentList = favoriteLocationsFlow.value.toMutableList()
        currentList.add(favouriteLocation)
        favoriteLocationsFlow.value = currentList

    }

    override suspend fun deleteLocation(favouriteLocation: FavouriteLocation) {
        val currentList = favoriteLocationsFlow.value.toMutableList()
        currentList.remove(favouriteLocation)
        favoriteLocationsFlow.value = currentList

    }

    override suspend fun deleteAllLocations() {
        favoriteLocationsFlow.value = emptyList()

    }

    override fun getAlerts(): LiveData<List<Alert>> {
        return alertsLiveData
    }

    override suspend fun insertAlert(alert: Alert) {
        alertsList.add(alert)
        alertsLiveData.value = alertsList
    }

    override suspend fun deleteAlert(alert: Alert) {
        alertsList.remove(alert)
        alertsLiveData.value = alertsList
    }

    override suspend fun deleteAllAlerts() {
        alertsList.clear()
        alertsLiveData.value = alertsList

    }

    override suspend fun getAlertById(id: Int): Alert {
        return alertsList.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Alert not found")

    }


}