package com.mohamedhamza.weatherly.model

data class WeatherResponse(
    val current: Current,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int
)

data class Current(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
)

data class Minutely(
    val dt: Int,
    val precipitation: Int
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)