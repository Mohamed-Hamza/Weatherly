package com.mohamedhamza.weatherly.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mohamedhamza.weatherly.MyApplication
import com.mohamedhamza.weatherly.data.SettingsStore
import com.mohamedhamza.weatherly.data.TemperatureUnit
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.model.ApiState
import com.mohamedhamza.weatherly.model.CurrentWeatherInfo
import com.mohamedhamza.weatherly.model.HourlyWeatherInfo
import com.mohamedhamza.weatherly.model.WeatherDataResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository, private val settingsStore: SettingsStore) : ViewModel() {

    private val _weatherDataState: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val weatherDataState: StateFlow<ApiState> = _weatherDataState

    private val _measureUnit = MutableLiveData<String>()   //metric or imperial
    val measureUnit : MutableLiveData<String> = _measureUnit


    init {

        viewModelScope.launch {
            settingsStore.tempUnit.collect {
                Log.d("HomeViewModel", "init tempUnit: $it")
                when (it) {
                    "°C" -> _measureUnit.value = "metric"
                    "°F" -> _measureUnit.value = "imperial"
                    "K" -> _measureUnit.value = "standard"
                }
            }
        }
    }


    private var previousWeatherData: WeatherDataResponse? = null


    private val fileName = "weather_data.json" //to save the last fetched weather data in a file



    fun fetchWholeWeather(lat: Double, lon: Double, measureUnit: String) {

        viewModelScope.launch {

//            val units = "metric"  //Todo get from settings
            val lang = "en" //Todo get from settings
            val wholeWeather = weatherRepository.getWholeWeatherResponse(lat, lon, measureUnit, lang)
//            _weatherDataState.value = ApiState.Success(wholeWeather!!)
            if (wholeWeather == null){ //no internet connection or error
                //Todo, check if there is a saved weather data in the file and use it
                val savedWeatherData = readWeatherDataFromFile()
                if (savedWeatherData != null){
                    _weatherDataState.value = ApiState.Error(
                        "NO INTERNET CONNECTION"
                    )
                    _weatherDataState.value = ApiState.Success(savedWeatherData)
                }else{
                    _weatherDataState.value = ApiState.Error(
                        "Error fetching weather data"
                    )
                }
            }
              else {
                Log.d("HomeViewModel", "fetchWholeWeather: weather fetched")
                if (isWeatherDataChanged(wholeWeather)) {
                    _weatherDataState.value = ApiState.Error(
                        "BACK ONLINE"
                    )
                    previousWeatherData = wholeWeather
                    saveWeatherDataToFile(wholeWeather)
                    _weatherDataState.value = ApiState.Success(wholeWeather)
                }
            }


        }
    }

    private fun isWeatherDataChanged(newWeatherData: WeatherDataResponse?): Boolean {
        return previousWeatherData?.current?.temp != newWeatherData?.current?.temp
    }


    private fun saveWeatherDataToFile(weatherData: WeatherDataResponse) {
        val json = Gson().toJson(weatherData)
        MyApplication.appContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    private fun readWeatherDataFromFile(): WeatherDataResponse? {
        return try {
            val fileInputStream = MyApplication.appContext.openFileInput(fileName)
            val size = fileInputStream.available()
            val buffer = ByteArray(size)
            fileInputStream.read(buffer)
            fileInputStream.close()

            val json = String(buffer)
            Gson().fromJson(json, WeatherDataResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

}



class HomeViewModelFactory(private val iRepo: WeatherRepository, private val settingsStore: SettingsStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(iRepo, settingsStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}


