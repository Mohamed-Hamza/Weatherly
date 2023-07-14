package com.mohamedhamza.weatherly.alert

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mohamedhamza.productsviewmodel.model.ConcreteLocalSource
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale.IsoCountryCode
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class ReminderWorker(
    val context: Context,
    val params: WorkerParameters,
) : CoroutineWorker(context, params) {

    private var weatherRepository = WeatherRepository.getInstance(ConcreteLocalSource(context))

    override suspend fun doWork(): Result {


        //Log that the work is starting
        Log.d("ReminderWorker", "doWork: starting")

        val timeZone = inputData.getString("timeZone") ?: "UTC"

//        val startDate = inputData.getLong("startDate", 0L)
//        val endDate = inputData.getLong("endDate", 0L)

        val locationId = inputData.getInt("locationId", 0)
        //get alert from database using location id and check if it still exists in the database
        val alert = weatherRepository.getAlertById(locationId)

        val startDate = alert.startDate
        val endDate = alert.endDate


        val startDateZoned =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.of(timeZone))
        val endDateTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDate), ZoneId.of(timeZone))


        val currentTime = ZonedDateTime.now(ZoneId.of(timeZone))


        if (currentTime < startDateZoned) {
            return Result.success()
        }

        if (currentTime > endDateTime) {
            WorkManager.getInstance(context).cancelAllWorkByTag("alert${locationId}")
            weatherRepository.deleteAlert(alert)
            return Result.success()
        }

        //If the alert's endtime is today, then cancel all future alerts but still show today's alert
        if (currentTime.toLocalDate() == endDateTime.toLocalDate()) {
            WorkManager.getInstance(context).cancelAllWorkByTag("alert${locationId}")
            weatherRepository.deleteAlert(alert)

        }

        NotificationHelper(context).createNotification(
            inputData.getString("title").toString(),
            inputData.getString("message").toString()
        )




        return Result.success()
    }

    companion object {
        fun cancelAlert(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("alert")
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun scheduleNotificationWork(
            context: Context,
            location: FavouriteLocation,
            startDate: Long,
            endDate: Long
        ) {
            val inputData = Data.Builder()
                .putString("timeZone", location.timeZone)
                .putInt("locationId", location.id)
                .putLong("startDate", startDate)
                .putLong("endDate", endDate)
                .putString("title", "Weatherly")
                .putString("message", "Don't forget to check the weather today!")
                .build()

            val currentTime = ZonedDateTime.now(ZoneId.of(location.timeZone))
            val alertTime =
                currentTime.withHour(9).withMinute(51).withSecond(0).withNano(0) // 3:15 PM

            var nextDelay = Duration.between(currentTime, alertTime)
            //if the next delay is negative, add 1 day
            if (nextDelay.isNegative) {
                nextDelay = nextDelay.plus(Duration.ofDays(1))
            }

            //next delay in seconds
            Log.d("ReminderWorker", "scheduleNotificationWork: ${nextDelay.seconds}")


            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            //make peridic work request with interval (1 day) with initial delay of next delay
            val alertWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(nextDelay.toMillis(), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(constraints)
                .addTag("alert${location.id}")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork( //only one work request at a time
                    "alert${location.id}",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    alertWorkRequest
                )

            val repository = WeatherRepository.getInstance(ConcreteLocalSource(context))
            //add alert to the database
            val alert = Alert(
                location.id,
                location.name!!,
                location.arName!!,
                location.countryCode,
                location.timeZone,
                startDate,
                endDate
            )

            GlobalScope.launch {
                repository.insertAlert(alert)
            }

        }

    }
}


