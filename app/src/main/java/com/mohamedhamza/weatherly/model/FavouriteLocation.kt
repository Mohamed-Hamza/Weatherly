package com.mohamedhamza.weatherly.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale.IsoCountryCode


@Entity(tableName = "favourite_location")
data class FavouriteLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String? = "Unknown",
    val arName: String? = "غير معروف",
    val countryCode: String,
    val lat: Double,
    val lon: Double,
    //boolean to check if the location is current location or not
    val timeZone: String,
    val timeZoneOffset: Int,
    val isCurrentLocation: Boolean = false,
    val isAlertEnabled: Boolean = false
)
