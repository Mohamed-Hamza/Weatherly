package com.mohamedhamza.weatherly.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey
    val id: Int, //same as the location id
//    val locationId: Int,
    val locationName: String,
    val locationArName: String,
    val locationCountryCode: String,
    val timeZone: String,
    val startDate: Long,
    val endDate: Long

)