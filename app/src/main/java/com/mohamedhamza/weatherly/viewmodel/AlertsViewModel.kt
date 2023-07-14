package com.mohamedhamza.weatherly.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.mohamedhamza.weatherly.data.IWeatherRepository
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.model.Alert
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: IWeatherRepository) : ViewModel() {

    val alerts : LiveData<List<Alert>> = repository.getAlerts()

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }

    fun deleteAllAlerts() {
        viewModelScope.launch {
            repository.deleteAllAlerts()
        }
    }

    fun insertAlert(alert: Alert) {
        viewModelScope.launch {
            repository.insertAlert(alert)
        }
    }



    class AlertsViewModelFactory(private val repository: IWeatherRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}