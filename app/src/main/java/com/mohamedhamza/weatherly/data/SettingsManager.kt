package com.mohamedhamza.weatherly.data

import android.content.Context
import android.content.SharedPreferences


class SettingsManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val DEFAULT_LANGUAGE = "en"

        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    var language: String
        get() = sharedPreferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()

    var temperatureUnit: String
        get() = sharedPreferences.getString(KEY_TEMPERATURE_UNIT, "metric") ?: "metric"
        set(value) = sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, value).apply()
}

