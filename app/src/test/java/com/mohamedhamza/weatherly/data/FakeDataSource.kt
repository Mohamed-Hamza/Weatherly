package com.mohamedhamza.weatherly.data

import androidx.lifecycle.LiveData
import com.mohamedhamza.productsviewmodel.model.LocalSource
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDataSource(var alerts: MutableList<Alert>? = mutableListOf()) : LocalSource {

    private val favoriteLocations: MutableList<FavouriteLocation> = mutableListOf()
//    private val alerts: MutableList<Alert> = mutableListOf()


    override suspend fun getFavoriteLocations(): Flow<List<FavouriteLocation>> {
        return flow { emit(favoriteLocations) }
    }

    override suspend fun deleteLocation(favouriteLocation: FavouriteLocation) {
        favoriteLocations.remove(favouriteLocation)
    }

    override suspend fun insertLocation(favouriteLocation: FavouriteLocation) {
        favoriteLocations.add(favouriteLocation)
    }

    override suspend fun deleteAllLocations() {
        favoriteLocations.clear()
    }

    override fun getAlerts(): LiveData<List<Alert>> {
        return object : LiveData<List<Alert>>() {
            override fun getValue(): List<Alert> {
                return alerts!!
            }
        }
    }

    override suspend fun insertAlert(alert: Alert) {
        alerts?.add(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        alerts?.remove(alert)
    }

    override suspend fun deleteAllAlerts() {
        alerts?.clear()
    }

    override suspend fun getAlertById(id: Int): Alert {
        return alerts?.find { it.id == id }!!
    }
}