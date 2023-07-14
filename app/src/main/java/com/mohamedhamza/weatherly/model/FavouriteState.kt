package com.mohamedhamza.weatherly.model

sealed class FavouriteLocationsState{
    object Loading:FavouriteLocationsState()
    object Empty:FavouriteLocationsState()
    data class HasElements(val favouriteLocations:List<FavouriteLocation>):FavouriteLocationsState()
}
