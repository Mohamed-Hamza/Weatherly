package com.mohamedhamza.weatherly.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mohamedhamza.weatherly.data.AlertMode
import com.mohamedhamza.weatherly.data.SettingsManager
import com.mohamedhamza.weatherly.data.SettingsStore
import com.mohamedhamza.weatherly.data.TemperatureUnit
import com.mohamedhamza.weatherly.data.TemperatureUnits
import com.mohamedhamza.weatherly.data.UserPreferencesRepository
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.data.WindSpeedUnit
import com.mohamedhamza.weatherly.data.WindSpeedUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsStore: SettingsStore) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(
        getBetterDisplayName(
            AppCompatDelegate.getApplicationLocales().get(0)?.displayName
        )
    )
    val currentLanguage: MutableStateFlow<String> = _currentLanguage

    private val _temperatureUnit = MutableLiveData<String>()
    val temperatureUnit: MutableLiveData<String> = _temperatureUnit

    private val _windSpeedUnit = MutableLiveData<String>()
    val windSpeedUnit: MutableLiveData<String> = _windSpeedUnit

    private val _alertMode = MutableLiveData<String>()
    val alertMode: MutableLiveData<String> = _alertMode

    private val _alertEnabled = MutableLiveData<Boolean>()
    val alertEnabled: MutableLiveData<Boolean> = _alertEnabled


    init {
        viewModelScope.launch {
            settingsStore.windSpeedUnit.collect {
                _windSpeedUnit.value = it
            }
        }

        viewModelScope.launch {
            settingsStore.tempUnit.collect {
                _temperatureUnit.value = it
            }
        }

        viewModelScope.launch {
            settingsStore.alertMode.collect {
                _alertMode.value = it
            }
        }

        viewModelScope.launch {
            settingsStore.alertEnabled.collect {
                _alertEnabled.value = it
            }
        }
    }

    fun saveAlertEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsStore.saveAlertEnabled(enabled)
        }
    }

    fun saveAlertMode(mode: AlertMode) {
        viewModelScope.launch {
            settingsStore.saveAlertMode(mode)
        }
    }

    fun saveTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            settingsStore.saveTempUnit(unit)
        }
    }

    fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            settingsStore.saveWindSpeedUnit(unit)
        }
    }


    private fun getBetterDisplayName(language: String?): String {
        return if (language == "Arabic") {
            "العربية"
        } else {
            "English"
        }
    }

    fun changeLanguage(language: String) {
        if (language == "English") {
            val localeList = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(localeList)
        } else {
            val localeList = LocaleListCompat.forLanguageTags("ar")
            AppCompatDelegate.setApplicationLocales(localeList)
        }
        _currentLanguage.value =
            getBetterDisplayName(AppCompatDelegate.getApplicationLocales().get(0)?.displayName)

    }

}


class SettingsViewModelFactory(private val settingsStore: SettingsStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


