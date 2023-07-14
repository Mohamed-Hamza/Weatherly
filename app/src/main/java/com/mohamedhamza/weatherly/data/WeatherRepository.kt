package com.mohamedhamza.weatherly.data

import com.mohamedhamza.productsviewmodel.model.LocalSource
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.CurrentWeatherInfo
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.HourlyWeatherInfo
import com.mohamedhamza.weatherly.network.WeatherApiClient

class WeatherRepository(private val localSource: LocalSource) : IWeatherRepository {

    companion object {
        private var repo: WeatherRepository? = null

        fun getInstance(localSource: LocalSource): WeatherRepository {
            if (repo == null) {
                repo = WeatherRepository(localSource)
            }
            return repo!!
        }
    }

//    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherInfo? {
//        return WeatherApiClient.getCurrentWeather(lat, lon)
//    }


    override suspend fun getWholeWeatherResponse(lat:Double, lon: Double, units:String, lang:String) = WeatherApiClient.getWholeWeatherResponse(lat,lon,units,lang)

    override suspend fun getTimeZoneOfLocation(lat: Double, lon: Double) = WeatherApiClient.getTimeZoneAndOffsetOfLocation(lat,lon)


    override suspend fun getFavouriteLocations() = localSource.getFavoriteLocations()

    override suspend fun insertLocation(favouriteLocation: FavouriteLocation) = localSource.insertLocation(favouriteLocation)

    override suspend fun deleteLocation(favouriteLocation: FavouriteLocation) = localSource.deleteLocation(favouriteLocation)

    override suspend fun deleteAllLocations() = localSource.deleteAllLocations()


    override fun getAlerts() = localSource.getAlerts()

    override suspend fun insertAlert(alert: Alert) = localSource.insertAlert(alert)

    override suspend fun deleteAlert(alert: Alert) = localSource.deleteAlert(alert)

    override suspend fun deleteAllAlerts() = localSource.deleteAllAlerts()
    override suspend fun getAlertById(id: Int) = localSource.getAlertById(id)



}
