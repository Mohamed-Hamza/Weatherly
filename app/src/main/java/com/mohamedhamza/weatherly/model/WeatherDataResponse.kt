package com.mohamedhamza.weatherly.model

import com.google.gson.annotations.SerializedName

class WeatherDataResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeatherInfo,
    val hourly: List<HourlyWeatherInfo>,
    val daily: List<DailyWeatherInfo>
    )


data class CurrentWeatherInfo(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>,
    val rain: Rain?
)

data class HourlyWeatherInfo(
    val dt: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>,
    val pop: Double
)

data class DailyWeatherInfo(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: DailyTemp,
    val feels_like: DailyFeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>,
    val clouds: Int,
    val pop: Double,
    val rain: Double,
    val uvi: Double
)

data class DailyTemp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class DailyFeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)


data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Rain(
    @SerializedName("1h") val oneHour: Double
)


