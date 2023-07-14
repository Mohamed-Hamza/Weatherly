package com.mohamedhamza.weatherly.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsStore")

val KEY_TEMP_UNIT = stringPreferencesKey("temp_unit_key")
val KEY_WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit_key")
val KEY_ALERT_MODE = stringPreferencesKey("alert_mode_key")
val KEY_ALERT_ENABLED = booleanPreferencesKey("alert_enabled_key")


enum class TemperatureUnit(val value: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    KELVIN("K")
}

enum class WindSpeedUnit(val value: String) {
    MPH("mph"),
    MS("m/s")
}

enum class AlertMode(val value: String) {
    NOTIFICATION("Notification"),
    DIALOG("Dialog")
}

class SettingsStore(private val context: Context) {

    val alertEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_ALERT_ENABLED] ?: true
        }

    suspend fun saveAlertEnabled(alertEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ALERT_ENABLED] = alertEnabled
        }
    }

    val alertMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_ALERT_MODE] ?: AlertMode.NOTIFICATION.value
        }

    suspend fun saveAlertMode(alertMode: AlertMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ALERT_MODE] = alertMode.value
        }
    }


    val tempUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_TEMP_UNIT] ?: TemperatureUnit.CELSIUS.value
        }

    suspend fun saveTempUnit(tempUnit: TemperatureUnit) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TEMP_UNIT] = tempUnit.value
        }
    }

    val windSpeedUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            Log.d("SettingsStore", "windSpeedUnit: ${preferences[KEY_WIND_SPEED_UNIT]}")
            preferences[KEY_WIND_SPEED_UNIT] ?: WindSpeedUnit.MS.value
        }

    suspend fun saveWindSpeedUnit(windSpeedUnit: WindSpeedUnit) {
        Log.d("SettingsStore", "saveWindSpeedUnit: ${windSpeedUnit.value}")
        context.dataStore.edit { preferences ->
            preferences[KEY_WIND_SPEED_UNIT] = windSpeedUnit.value
        }
    }
}