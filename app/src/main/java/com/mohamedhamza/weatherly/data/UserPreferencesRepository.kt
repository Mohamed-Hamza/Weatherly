package com.mohamedhamza.weatherly.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException


enum class WindSpeedUnits(val value: String) {
    MPH("mph"),
    MS("m/s")
}
//
enum class TemperatureUnits(val value: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    KELVIN("K")
}

data class UserPreferences(
    val language: String,
    val temperatureUnits: TemperatureUnits,
    val windSpeedUnits: WindSpeedUnits,
//    val location: String
)

object  UserPreferencesRepository {

    private lateinit var dataStore: DataStore<Preferences>

    private val TAG: String = "UserPreferencesRepository"

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val TEMPERATURE_UNITS =stringPreferencesKey("temperature_units")
        val WIND_SPEED_UNITS = stringPreferencesKey("wind_speed_units")
    }

    fun init(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }


    fun getUserPreferencesFlow(): Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }



    suspend fun setTemperatureUnits(temperatureUnits: TemperatureUnits) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEMPERATURE_UNITS] = temperatureUnits.value
        }
    }

    suspend fun setWindSpeedUnits(windSpeedUnits: WindSpeedUnits) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WIND_SPEED_UNITS] = windSpeedUnits.value
        }
    }

    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())


    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val language = preferences[PreferencesKeys.LANGUAGE] ?: "English"
        val temperatureUnits = preferences[PreferencesKeys.TEMPERATURE_UNITS]?.let {
            TemperatureUnits.valueOf(it)
        } ?: TemperatureUnits.CELSIUS
        val windSpeedUnits = preferences[PreferencesKeys.WIND_SPEED_UNITS]?.let {
            WindSpeedUnits.valueOf(it)
        } ?: WindSpeedUnits.MS
        return UserPreferences(language, temperatureUnits, windSpeedUnits)

    }

}

