package com.mohamedhamza.productsviewmodel.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkManager
import com.mohamedhamza.weatherly.data.WeatherDatabase
import com.mohamedhamza.weatherly.data.WeatherDao
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import kotlinx.coroutines.flow.Flow


class ConcreteLocalSource(context: Context) : LocalSource {

    private val weatherDao: WeatherDao = WeatherDatabase.getDatabase(context).weatherInfoDao()
    private val workManager = WorkManager.getInstance(context)

    companion object {
        private var concreteLocalSource: ConcreteLocalSource? = null

        fun getInstance(context: Context): ConcreteLocalSource {
            if (concreteLocalSource == null) {
                concreteLocalSource = ConcreteLocalSource(context)
            }
            return concreteLocalSource!!
        }
    }

    override suspend fun getFavoriteLocations(): Flow<List<FavouriteLocation>> {
        return weatherDao.getAllFavouriteLocations()
    }

    override suspend fun deleteLocation(favouriteLocation: FavouriteLocation) {
        weatherDao.deleteLocation(favouriteLocation.id)
    }

    override suspend fun insertLocation(favouriteLocation: FavouriteLocation) {
        weatherDao.insertLocation(favouriteLocation)
    }

    override suspend fun deleteAllLocations() {
        weatherDao.deleteAllLocations()
    }

    override fun getAlerts(): LiveData<List<Alert>> {
        return weatherDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: Alert) {
        weatherDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        weatherDao.deleteAlert(alert.id)
        workManager.cancelAllWorkByTag("alert${alert.id}")
    }

    override suspend fun deleteAllAlerts() {
        weatherDao.deleteAllAlerts()
    }

    override suspend fun getAlertById(id: Int): Alert {
        return weatherDao.getAlertById(id)
    }




}