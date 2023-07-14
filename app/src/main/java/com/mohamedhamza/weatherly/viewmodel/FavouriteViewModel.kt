package com.mohamedhamza.weatherly.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.FavouriteLocationsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _favouriteLocations : MutableStateFlow<FavouriteLocationsState> = MutableStateFlow(FavouriteLocationsState.Loading)
    val favouriteLocations = _favouriteLocations

    init {
        getFavouriteLocations()
    }

    private fun getFavouriteLocations() {

        viewModelScope.launch {
            weatherRepository.getFavouriteLocations().collect {
                if (it.isEmpty()){
                    _favouriteLocations.value = FavouriteLocationsState.Empty
                }else{
                    _favouriteLocations.value = FavouriteLocationsState.HasElements(it)
                }
            }
        }
    }

    fun deleteFavouriteLocation(favouriteLocation: FavouriteLocation){
        viewModelScope.launch {
            weatherRepository.deleteLocation(favouriteLocation)
            //notify the ui that the item is deleted
//            getFavouriteLocations()
        }
    }



    fun insertFavouriteLocation(favouriteLocation: FavouriteLocation){
        viewModelScope.launch {
            val timeZonePair= weatherRepository.getTimeZoneOfLocation(favouriteLocation.lat,favouriteLocation.lon)
            Log.d("FavouriteViewModel", "insertFavouriteLocation: $timeZonePair")
            if (timeZonePair != null) //should not be null
                weatherRepository.insertLocation(favouriteLocation.copy(timeZone = timeZonePair.first,timeZoneOffset = timeZonePair.second))
        }
    }


}


class FavouriteViewModelFactory(private val repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            return FavouriteViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

