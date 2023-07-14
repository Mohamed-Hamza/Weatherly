package com.mohamedhamza.weatherly.view.util

import com.ibm.icu.util.TimeZone
import com.ibm.icu.util.ULocale
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


fun getCurrentTimeByTimezone(timezone: String): String {
    val zoneId = ZoneId.of(timezone)
    val currentTime = LocalDateTime.now()
    val zonedDateTime = currentTime.atZone(zoneId)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    return zonedDateTime.format(formatter)
}

fun getTimeFromTimeZoneUsingInstant(timezone: String): String {
    val instant = Instant.now()
    val zoneId = ZoneId.of(timezone)
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    return zonedDateTime.format(formatter)
}

//fun getTimeFromTimeZone(timezone: String): String {
//    val calendar = Calendar.getInstance()
//    calendar.timeZone = TimeZone.getTimeZone(timezone)
//    val hour = calendar.get(Calendar.HOUR)
//    val minute = calendar.get(Calendar.MINUTE)
//    val amPm = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) "AM" else "PM"
//    return "$hour:$minute $amPm"
//}

//another function to get time from timezone, taking into account the daylight saving time (DST) offset (like summer time in egypt)
fun getCurrentTimeByTimezoneWithDSTOffset(timezone: String): String {
    val zoneId = ZoneId.of(timezone)
    val currentTime = ZonedDateTime.now(zoneId)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    return currentTime.format(formatter)
}



fun getTimeZoneFromCountryCode(countryCode: String) {

//    val timeZoneID = TimeZone.getAvailableIDs(countryCode)
    //guess timezone by country code if there is more than one timezone
    //print all timezones for country code "EG"
    val availableTimeZones = TimeZone.getAvailableIDs(countryCode)
    for (timezoneId in availableTimeZones) {
        val tz = TimeZone.getTimeZone(timezoneId)
        val region = tz.displayName
        //get continent from timezone id
        val continent = timezoneId.split("/")[0]

        println("timezoneId $timezoneId")
        println("region $region")


        if (region.contains(countryCode, ignoreCase = true)) {
            println("timezoneId $timezoneId")
            println("region $region")
        }

    }



//    return
}

fun guessTimezoneByCountryCode(countryCode: String): String? {
    val availableTimeZones = TimeZone.getAvailableIDs("US")

    for (timezoneId in availableTimeZones) {
        val tz = TimeZone.getTimeZone(timezoneId)
        val region = tz.getDisplayName(false, TimeZone.SHORT, ULocale.ENGLISH)

        if (region.contains(countryCode, ignoreCase = true)) {
            return timezoneId
        }
    }

    return null
}


fun main(){
//    val currentTime = getCurrentTimeByTimezone("Africa/Cairo") //current time is 12:00 PM
////    val time = getTimeFromTimeZone("Africa/Cairo") //time is 0:0 PM 👎
//    val timeWithDSTOffset = getCurrentTimeByTimezoneWithDSTOffset("Africa/Cairo") //time with DST offset is 12:00 PM
//    println("current time is $currentTime")
////    println("time is $time")
//    println("time with DST offset is $timeWithDSTOffset")
//    println("time with DST using Instant ${getTimeFromTimeZoneUsingInstant("Africa/Cairo")}")

    //get timezone from country code
    getTimeZoneFromCountryCode("DZ")


}