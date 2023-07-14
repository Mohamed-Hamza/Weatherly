package com.mohamedhamza.weatherly.network

import com.mohamedhamza.weatherly.model.WeatherDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


    @GET("onecall")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String, // "metric" for Celsius
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String

    ): WeatherDataResponse


}