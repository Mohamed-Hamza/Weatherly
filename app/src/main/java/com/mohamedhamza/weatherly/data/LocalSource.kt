package com.mohamedhamza.productsviewmodel.model

import androidx.lifecycle.LiveData
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    suspend fun getFavoriteLocations(): Flow<List<FavouriteLocation>>
    suspend fun deleteLocation(favouriteLocation: FavouriteLocation)
    suspend fun insertLocation(favouriteLocation: FavouriteLocation)
    suspend fun deleteAllLocations()


    fun getAlerts(): LiveData<List<Alert>>
    suspend fun insertAlert(alert: Alert)
    suspend fun deleteAlert(alert: Alert)
    suspend fun deleteAllAlerts()
    suspend fun getAlertById(id: Int): Alert

}