package com.mohamedhamza.weatherly.view.util

import com.mohamedhamza.weatherly.R

object WeatherIconMapper {

    fun getWeatherIconResourceId(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.clear_day
            "01n" -> R.drawable.clear_night
            "02d" -> R.drawable.few_clouds_day
            "02n" -> R.drawable.few_clouds_night
            "03d" -> R.drawable.scattered_clouds
            "03n" -> R.drawable.scattered_clouds
            "04d" -> R.drawable.broken_clouds
            "04n" -> R.drawable.broken_clouds
            "09d" -> R.drawable.shower_rain
            "09n" -> R.drawable.shower_rain
            "10d" -> R.drawable.rain_day
            "10n" -> R.drawable.rain_night
            "11d" -> R.drawable.thunderstorm
            "11n" -> R.drawable.thunderstorm
            "13d" -> R.drawable.snow
            "13n" -> R.drawable.snow
            "50d" -> R.drawable.mist
            "50n" -> R.drawable.mist

            else -> R.drawable.clear_day
        }
    }


    //get weather lottie animation from icon code
    fun getWeatherLottieAnimation(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.raw.clear_day
            "01n" -> R.raw.clear_night
            "02d" -> R.raw.few_clouds_day
            "02n" -> R.raw.few_clouds_night
            "03d" -> R.raw.scattered_clouds
            "03n" -> R.raw.scattered_clouds
            "04d" -> R.raw.broken_clouds
            "04n" -> R.raw.broken_clouds
            "09d" -> R.raw.shower_rain
            "09n" -> R.raw.shower_rain
            "10d" -> R.raw.rain_day
            "10n" -> R.raw.rain_night
            "11d" -> R.raw.thunderstorm
            "11n" -> R.raw.thunderstorm
            "13d" -> R.raw.snow
            "13n" -> R.raw.snow
            "50d" -> R.raw.mist
            "50n" -> R.raw.mist
            else -> R.raw.clear_day
        }
    }
}