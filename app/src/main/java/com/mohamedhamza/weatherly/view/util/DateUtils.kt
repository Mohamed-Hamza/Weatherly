package com.mohamedhamza.weatherly.view.util

import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import java.util.Locale

object DateUtils {

    fun formatHour(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000

        val amPm = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) "AM" else "PM"
        val hour = calendar.get(Calendar.HOUR)
        return if (hour == 0) "12 $amPm" else "$hour $amPm"
    }




    fun formatTemperature(temperature: Double): String {
        val decimalFormat = DecimalFormat("0.#")
        return "${decimalFormat.format(temperature)}°C"
    }

    //13 July, Sunday
    fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val monthName = when (month) {
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> ""
        }

        val dayOfWeekName = when (dayOfWeek) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> ""
        }

        return "$day $monthName, $dayOfWeekName"
    }

    //Sun
    fun formatWeekDay(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val dayOfWeekName = when (dayOfWeek) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> ""
        }

        return dayOfWeekName
    }

    //return time from timezone as 5:00 PM
    fun getTimeFromTimeZone(timezone: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone(timezone)
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) "AM" else "PM"
        return "$hour:$minute $amPm"
    }


    fun getDayAndMonthFromTimeZonedTimeStamp(timestamp: Long, timezone: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone(timezone)
        calendar.timeInMillis = timestamp * 1000

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)

        val monthName = when (month) {
            0 -> "JAN"
            1 -> "FEB"
            2 -> "MAR"
            3 -> "APR"
            4 -> "MAY"
            5 -> "JUN"
            6 -> "JUL"
            7 -> "AUG"
            8 -> "SEP"
            9 -> "OCT"
            10 -> "NOV"
            11 -> "DEC"
            else -> ""
        }

        return "$day $monthName"
    }


    //long dates from the date picker results
    fun convertLongToDateString(time: Long, timezone: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone(timezone)
        calendar.timeInMillis = time
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val monthName = when (month) {
            0 -> "JAN"
            1 -> "FEB"
            2 -> "MAR"
            3 -> "APR"
            4 -> "MAY"
            5 -> "JUN"
            6 -> "JUL"
            7 -> "AUG"
            8 -> "SEP"
            9 -> "OCT"
            10 -> "NOV"
            11 -> "DEC"
            else -> ""
        }

//        return "$day $monthName $year"
        return "$day $monthName"
    }
}