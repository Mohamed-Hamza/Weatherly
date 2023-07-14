package com.mohamedhamza.weatherly.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherInfo(weatherInfo: WeatherInfo)

    @Query("SELECT * FROM weather_info")
    fun getAllWeatherInfo(): Flow<List<WeatherInfo>>

    @Query("SELECT * FROM favourite_location")
    fun getAllFavouriteLocations(): Flow<List<FavouriteLocation>>

    @Query("DELETE FROM favourite_location WHERE id = :id")
    suspend fun deleteLocation(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(favouriteLocation: FavouriteLocation)

    @Query("DELETE FROM favourite_location")
    suspend fun deleteAllLocations()

    //Alerts
    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): LiveData<List<Alert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert)

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlert(id: Int)

    @Query("DELETE FROM alerts")
    suspend fun deleteAllAlerts()

    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getAlertById(id: Int): Alert




}