package com.mohamedhamza.weatherly.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.mohamedhamza.weatherly.BuildConfig
import com.mohamedhamza.weatherly.MyApplication
import com.mohamedhamza.weatherly.model.WeatherDataResponse
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


object WeatherApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val API_KEY = BuildConfig.OPEN_WEATHER_API_KEY




    private fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    private val weatherApi: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }

//    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherInfo? {
//        val exclude = "minutely,hourly,daily,alerts"
//        //return celcius
//        val units = "metric" // Set the units parameter to "metric" for Celsius
//
//        return try {
//            val response = weatherApi.getWeatherData(lat, lon, exclude,units, API_KEY)
//            response.current
//
//        } catch (e: Exception) {
//            // Handle the exception appropriately
//            null
//        }
//    }


    suspend fun getWholeWeatherResponse(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherDataResponse? {
        val exclude = "minutely,alerts"
        return try {
            val response = weatherApi.getWeatherData(lat, lon, exclude, units, lang, API_KEY)
            response

        } catch (e: Exception) {
            Log.d("WeatherApiClient", e.toString())
            null
        }
    }


    suspend fun getTimeZoneAndOffsetOfLocation(lat: Double, lon: Double): Pair<String,Int>? {
        val exclude = "current,minutely,hourly,daily,alerts"
        return try {
            val response = weatherApi.getWeatherData(lat, lon, exclude, "metric", "en", API_KEY)
            Pair(response.timezone,response.timezone_offset)

        } catch (e: Exception) {
            Log.d("WeatherApiClient", e.toString())
            null
        }
    }



}