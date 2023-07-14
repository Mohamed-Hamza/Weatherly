package com.mohamedhamza.weatherly

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.mohamedhamza.productsviewmodel.model.ConcreteLocalSource
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.databinding.FragmentHomeBinding
import com.mohamedhamza.weatherly.model.ApiState
import com.mohamedhamza.weatherly.model.WeatherDataResponse
import com.mohamedhamza.weatherly.view.DailyAdapter
import com.mohamedhamza.weatherly.view.HourlyAdapter
import com.mohamedhamza.weatherly.view.util.DateUtils
import com.mohamedhamza.weatherly.view.util.LocationUtils
import com.mohamedhamza.weatherly.view.util.WeatherIconMapper
import com.mohamedhamza.weatherly.viewmodel.HomeViewModel
import com.mohamedhamza.weatherly.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.launch


class FavoriteLocationFragment : Fragment() {

    companion object {
        const val TAG = "FavoriteLocationFragment"
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        return inflater.inflate(R.layout.fragment_favorite_location, container, false)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()


        binding.recyclerViewHourlyForecast.adapter = hourlyAdapter
        binding.recyclerViewHourlyForecast.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.recyclerViewDailyForecast.adapter = dailyAdapter
        binding.recyclerViewDailyForecast.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        binding.recyclerViewDailyForecast.isNestedScrollingEnabled = false

        val preferenceWrapper = (activity?.application as MyApplication).settingsStore

        val viewModelFactory = HomeViewModelFactory(
            WeatherRepository.getInstance(
                ConcreteLocalSource.getInstance(requireContext())
            ),preferenceWrapper
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]


        lifecycleScope.launch {
            viewModel.weatherDataState.collect {
                Log.d(TAG, "onViewCreated, weatherDataState collected: $it")
                when (it) {
                    is ApiState.Success -> {
                        binding.constraintGroupLoading.visibility = View.GONE
                        binding.constraintGroupContent.visibility = View.VISIBLE
                        populateViewsWithWeatherData(it.data)

                    }

                    is ApiState.Error -> {
                        if(it.message =="NO INTERNET CONNECTION"){
                            binding.offlineMessage.visibility = View.VISIBLE
                            binding.constraintGroupLoading.visibility = View.GONE
                        }else if (it.message =="BACK ONLINE" && binding.offlineMessage.visibility == View.VISIBLE){
                            binding.offlineMessage.setBackgroundResource(R.color.green_light)
                            binding.offlineMessageText.text =  resources.getString(R.string.back_online)
                            binding.offlineMessage.postDelayed({
                                binding.offlineMessage.visibility = View.GONE
                            }, 2000)

                        }
                    }

                    is ApiState.Loading -> {
                        binding.constraintGroupLoading.visibility = View.VISIBLE
                        binding.constraintGroupContent.visibility = View.GONE
                    }
                }
            }

        }

    }

    @SuppressLint("SetTextI18n")
    fun populateViewsWithWeatherData(wholeWeather: WeatherDataResponse) {

        val temperature = wholeWeather.current.temp
        val windSpeed = wholeWeather.current.wind_speed
        val humidity = wholeWeather.current.humidity
        binding.temperature.text = "$temperature °C"
        binding.weatherDescription.text = wholeWeather.current.weather[0].description

//        binding.cityName.text = wholeWeather.timezone
        binding.cityName.text = LocationUtils.getCityFromLatLng(
            activity?.applicationContext!!,
            wholeWeather.lat,
            wholeWeather.lon
        )




        val date = wholeWeather.current.dt
        val dateText = DateUtils.formatDate(date)
        binding.todayDate.text = dateText
        val iconCode = wholeWeather.current.weather[0].icon
        val animationResId = WeatherIconMapper.getWeatherLottieAnimation(iconCode)
        binding.weatherConditionAnimation.setAnimation(animationResId)
        binding.weatherConditionAnimation.repeatCount = LottieDrawable.REVERSE
        binding.weatherConditionAnimation.playAnimation()

        binding.textViewWindSpeedValue.text = "$windSpeed m/s"
        binding.textViewHumidityValue.text = "$humidity %"
        binding.textViewCloudValue.text = "${wholeWeather.current.clouds} %"
        binding.textViewPressureValue.text = "${wholeWeather.current.pressure} hPa"
        binding.textViewVisibilityValue.text = "${wholeWeather.current.visibility} m"
        binding.textViewUltravioletValue.text = "${wholeWeather.current.uvi}"


        hourlyAdapter.submitList(wholeWeather.hourly)

        dailyAdapter.submitList(wholeWeather.daily)
    }

    override fun onResume() {
        super.onResume()
        //get latitude and longitude from args
        val latitude = arguments?.getFloat("latitude")?.toDouble()
        val longitude = arguments?.getFloat("longitude")?.toDouble()
        viewModel.fetchWholeWeather(latitude!!, longitude!!, "metric") //TODO

    }

}


