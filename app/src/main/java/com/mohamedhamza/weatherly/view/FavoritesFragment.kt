package com.mohamedhamza.weatherly.view

import android.app.Activity
import android.location.Address
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adevinta.leku.ADDRESS
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LOCATION_ADDRESS
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.adevinta.leku.TIME_ZONE_DISPLAY_NAME
import com.adevinta.leku.TIME_ZONE_ID
import com.adevinta.leku.TRANSITION_BUNDLE
import com.adevinta.leku.ZIPCODE
import com.adevinta.leku.locale.SearchZoneRect
import com.google.android.gms.common.internal.Objects.ToStringHelper
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohamedhamza.productsviewmodel.model.ConcreteLocalSource
import com.mohamedhamza.weatherly.MainActivity
import com.mohamedhamza.weatherly.R
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.databinding.FragmentFavoritesBinding
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.FavouriteLocationsState
import com.mohamedhamza.weatherly.view.util.LocationUtils
import com.mohamedhamza.weatherly.viewmodel.FavouriteViewModel
import com.mohamedhamza.weatherly.viewmodel.FavouriteViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.mohamedhamza.weatherly.MyApplication
import com.mohamedhamza.weatherly.alert.ReminderWorker
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModel
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModelFactory
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class FavoritesFragment : Fragment() {
    lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var settingsViewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceWrapper = (activity?.application as MyApplication).settingsStore
        settingsViewModel = ViewModelProvider(
            requireActivity(),
            SettingsViewModelFactory(preferenceWrapper)
        )[SettingsViewModel::class.java]

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deleteLocationLambda: (FavouriteLocation) -> Unit = {
            showDeleteLocationDialog(it)
        }

        val navigateToFavoriteFragment: (FavouriteLocation) -> Unit = {
            navigateToFavoriteFragment(it)
        }

        val locationAlertLambda: (FavouriteLocation) -> Unit = {
            showLocationAlertDatePicker(it)
            //reverse the boolean of alert property in the location
            viewModel.insertFavouriteLocation(it.copy(isAlertEnabled = !it.isAlertEnabled))
        }

        val favouritesAdapter = FavouriteLocationsAdapter(
            navigateToFavoriteFragment,
            locationAlertLambda,
            deleteLocationLambda
        )

        binding.favoritesRecyclerView.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

        binding.favoritesRecyclerView.adapter = favouritesAdapter

        val viewModelFactory = FavouriteViewModelFactory(
            WeatherRepository.getInstance(
                ConcreteLocalSource.getInstance(requireContext()),
            )
        )
        viewModel  = ViewModelProvider(this,viewModelFactory)[FavouriteViewModel::class.java]


        lifecycleScope.launch{
            settingsViewModel.currentLanguage.collect{
                favouritesAdapter.updateLanguage(it)
            }
        }


        lifecycleScope.launch {
            viewModel.favouriteLocations.collectLatest {

                when (it) {
                    is FavouriteLocationsState.Empty -> {
                        //show the empty state group
                        binding.emptyStateGroup.visibility = View.VISIBLE
                        binding.favoritesGroup.visibility = View.GONE
                    }
                    is FavouriteLocationsState.Loading -> {
//                        binding.emptyStateGroup.visibility = View.GONE
//                        binding.favoritesGroup.visibility = View.GONE
                    }
                    is FavouriteLocationsState.HasElements -> {
                        binding.emptyStateGroup.visibility = View.GONE
                        binding.favoritesGroup.visibility = View.VISIBLE
                        favouritesAdapter.submitList(it.favouriteLocations)
                    }
                }
            }
        }


        val lekuActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data

                    Log.d("RESULT****", "OK")
                    val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
                    Log.d("LATITUDE****", latitude.toString())
                    val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
                    Log.d("LONGITUDE****", longitude.toString())
                    val address = data?.getStringExtra(LOCATION_ADDRESS)
                    Log.d("ADDRESS****", address.toString())
                    val fullAddress = data?.getParcelableExtra<Address>(ADDRESS)
                    if (fullAddress != null) {
                        Log.d("FULL ADDRESS****", fullAddress.toString())
                    }
                    val timeZoneDisplayName = data?.getStringExtra(TIME_ZONE_DISPLAY_NAME)
                    if (timeZoneDisplayName != null) {
                        Log.d("TIME ZONE NAME****", timeZoneDisplayName)
                    }

                    //Log timezone id, it might be null
                    val timeZoneId = data?.getStringExtra(TIME_ZONE_ID)
                    if (timeZoneId != null) {
                        Log.d("TIME ZONE ID****", timeZoneId)
                    }


                    val cityName = LocationUtils.getCityFromLatLng(
                        requireContext(),
                        latitude!!, longitude!!
                    )
                    val arabicCityName = LocationUtils.getCityFromLatLngArabic(requireContext(),latitude,longitude)

                    val favouriteLocation = FavouriteLocation(
                        0,
                        cityName,
                        arabicCityName?: cityName, //if the arabic city name is null, use the english one
                        fullAddress?.countryCode.toString(),
                        latitude,
                        longitude,
                        "UTC",
                        0,
                        isCurrentLocation = false,
                        isAlertEnabled = true
                    )

                    if (arabicCityName != null) {
                        Log.d("ARABIC CITY NAME****", arabicCityName)
                    }else{
                        Log.d("ARABIC CITY NAME****", "null")
                    }

                    //print the favouriteLocation object
                    Log.d("FAVOURITE LOCATION****", favouriteLocation.toString())

                    viewModel.insertFavouriteLocation(favouriteLocation)


                } else {
                    Log.d("RESULT****", "CANCELLED")
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_location_was_selected),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        binding.addLocationButton.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
//                .withLegacyLayout() // the old layout is enabled by default
                .withLocation(41.4036299, 2.1743558)
//                .withGeolocApiKey("AIzaSyCv5C6MST7YmZr-z86X4NlUX1oWN2XVhis") //my api key
//                .withGooglePlacesApiKey("AIzaSyCv5C6MST7YmZr-z86X4NlUX1oWN2XVhis") //my api key
//                .withSearchZone("es_ES")
//                .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
                .withDefaultLocaleSearchZone()
                .shouldReturnOkOnBackPressed()
                .withStreetHidden()
//                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .withSearchBarHidden()
                .build(requireContext())


            lekuActivityResultLauncher.launch(locationPickerIntent)




        }

    }

    private fun showLocationAlertDatePicker(clickedLocation: FavouriteLocation) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select alert date range for this location")

                .setSelection(
                    Pair(
                        MaterialDatePicker.todayInUtcMilliseconds(),
                        null
                    )
                )
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.show(parentFragmentManager, "DATE_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            val startDate = dateRange.first?: return@addOnPositiveButtonClickListener
            val endDate = dateRange.second?: return@addOnPositiveButtonClickListener

            //what does start date and end date represent? is this milliseconds?
            Log.d("DATE RANGE", "start date: $startDate, end date: $endDate")

            ReminderWorker.scheduleNotificationWork(
                requireContext(),
                clickedLocation,
                startDate,
                endDate
            )
        }


    }


    private fun navigateToFavoriteFragment(favouriteLocation: FavouriteLocation) {
        //navigate to FavoriteLocationFragment
        val action = FavoritesFragmentDirections
            .actionFavoritesFragmentToFavoriteLocationFragment(favouriteLocation.lat.toFloat(), favouriteLocation.lon.toFloat())
        findNavController().navigate(action)

    }

    private fun showDeleteLocationDialog(favouriteLocation: FavouriteLocation){
        MaterialAlertDialogBuilder(requireContext(), R.style.DeleteConfirmationDialog)
            .setIcon(R.drawable.delete)
            .setTitle(getString(R.string.delete_location))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_location))
            .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                // Delete the location from the database
                viewModel.deleteFavouriteLocation(favouriteLocation)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
            }
            .show()

    }



}