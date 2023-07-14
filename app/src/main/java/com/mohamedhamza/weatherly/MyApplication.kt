package com.mohamedhamza.weatherly

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.multidex.MultiDexApplication
import com.google.android.gms.maps.MapsInitializer
import com.mohamedhamza.weatherly.data.SettingsManager
import com.mohamedhamza.weatherly.data.SettingsStore
import com.mohamedhamza.weatherly.data.UserPreferencesRepository
import java.util.Locale


class MyApplication : MultiDexApplication() {

    lateinit var settingsStore: SettingsStore

    companion object {
        lateinit var appContext: Context
            private set
    }



    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)

//        val dataStore: DataStore<Preferences> = dataStore
//        UserPreferencesRepository.init(dataStore)

        SettingsManager.getInstance(this)
        settingsStore = SettingsStore(this)
        appContext = applicationContext

    }


}
