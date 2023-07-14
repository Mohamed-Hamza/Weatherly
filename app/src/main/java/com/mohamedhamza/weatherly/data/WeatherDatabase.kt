package com.mohamedhamza.weatherly.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.model.WeatherInfo

@Database(entities = [WeatherInfo::class, FavouriteLocation::class, Alert::class], version = 2)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherInfoDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
