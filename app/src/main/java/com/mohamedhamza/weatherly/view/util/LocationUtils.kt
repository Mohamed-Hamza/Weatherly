package com.mohamedhamza.weatherly.view.util
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*

object LocationUtils {

    fun getCityCountryFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var cityCountry: String? = null

        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val city = address.locality
                val country = address.countryName

                if (city != null && country != null) {
                    cityCountry = "$city, $country"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return cityCountry
    }

    //get only the city name
    fun getCityFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var city: String? = null

        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
//                city = address.locality?: address.subLocality ?: address.subAdminArea ?: address.adminArea
                city = address.subAdminArea ?: address.adminArea
                //log all the four addresses to see which one is available
                Log.d("LocationUtils", "getCityFromLatLng (locality): ${address.locality}") //output: null
                Log.d("LocationUtils", "getCityFromLatLng (subLocality): ${address.subLocality}") //output: null
                Log.d("LocationUtils", "getCityFromLatLng (subAdminArea): ${address.subAdminArea}") //output: 10th of Ramadan City 1
                Log.d("LocationUtils", "getCityFromLatLng (adminArea): ${address.adminArea}") //output: Ash Sharqia Governorate
                Log.d("LocationUtils", "getCityFromLatLng (found): $city")  //output: 10th of Ramadan City 1
                //log country name too
                Log.d("LocationUtils", "getCityFromLatLng (countryName): ${address.countryName}") //output: Egypt
                //country code
                Log.d("LocationUtils", "getCityFromLatLng (countryCode): ${address.countryCode}") //output: EG
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return city
    }


    fun getCityFromLatLngArabic(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale("ar"))
        var city: String? = null

        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 3) as List<Address>
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[2] //Somehow this works!!! I don't know why but it works
                city = address.subAdminArea ?: address.adminArea
//                city = address.locality?: address.subLocality ?: address.subAdminArea ?: address.adminArea
                //log all the four addresses to see which one is available
                Log.d("LocationUtils", "getCityFromLatLng ARRR (locality): ${address.locality}")
                Log.d("LocationUtils", "getCityFromLatLng ARRR (subLocality): ${address.subLocality}")
                Log.d("LocationUtils", "getCityFromLatLng ARRR (subAdminArea): ${address.subAdminArea}")
                Log.d("LocationUtils", "getCityFromLatLng ARRR (adminArea): ${address.adminArea}")
                Log.d("LocationUtils", "getCityFromLatLng ARRR (found): $city")
                //log country name too
                Log.d("LocationUtils", "getCityFromLatLng ARRR (countryName): ${address.countryName}")
                //country code
                Log.d("LocationUtils", "getCityFromLatLng ARRR (countryCode): ${address.countryCode}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return city
    }



}