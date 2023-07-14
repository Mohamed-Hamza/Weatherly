package com.mohamedhamza.weatherly.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mohamedhamza.productsviewmodel.model.ConcreteLocalSource
import com.mohamedhamza.weatherly.MyApplication
import com.mohamedhamza.weatherly.R
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.databinding.FragmentHomeBinding
import com.mohamedhamza.weatherly.model.ApiState
import com.mohamedhamza.weatherly.model.WeatherDataResponse
import com.mohamedhamza.weatherly.view.util.DateUtils
import com.mohamedhamza.weatherly.view.util.LocationUtils.getCityCountryFromLatLng
import com.mohamedhamza.weatherly.view.util.LocationUtils.getCityFromLatLng
import com.mohamedhamza.weatherly.view.util.WeatherIconMapper
import com.mohamedhamza.weatherly.viewmodel.HomeViewModel
import com.mohamedhamza.weatherly.viewmodel.HomeViewModelFactory
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModel
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private val PERMISSION_ID = 97
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private val TAG = "HomeFragment"
    private lateinit var settingsViewModel: SettingsViewModel
    private var measurementUnit: String? = null




    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location? = locationResult.lastLocation
            fetchWeather(location?.latitude, location?.longitude)
        }
    }

    private fun fetchWeather(latitude: Double?, longitude: Double?) {

        if (latitude != null && longitude != null && measurementUnit != null  ) {
            viewModel.fetchWholeWeather(latitude, longitude, measurementUnit!!)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

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
            ), preferenceWrapper
        )
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[HomeViewModel::class.java]

        viewModel.measureUnit.observe(viewLifecycleOwner) {
            measurementUnit = it
        }




        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
//            }

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
        binding.cityName.text = getCityFromLatLng(activity?.applicationContext!!, wholeWeather.lat, wholeWeather.lon)




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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onResume() {
        super.onResume()
        getLastLocation()
    }


    private fun requestPermission() {
        val permission = arrayOf(
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            permission,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                requestNewLocationData()
            } else {
                // Permission denied
            }
        }
    }


    private fun checkPermission(): Boolean {
        if (
            (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            return true
        }
        return false
    }


    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                Toast.makeText(activity, "Please turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0

        mFusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }


}


