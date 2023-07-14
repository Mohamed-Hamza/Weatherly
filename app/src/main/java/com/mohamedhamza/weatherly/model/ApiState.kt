package com.mohamedhamza.weatherly.model

sealed class ApiState{
    data class Success(val data: WeatherDataResponse):ApiState()
    data class Error(val message:String):ApiState()
    object Loading:ApiState()
}
