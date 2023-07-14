package com.mohamedhamza.weatherly

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.navigation.NavigationBarView
import com.mohamedhamza.weatherly.alert.ReminderWorker
import com.mohamedhamza.weatherly.data.SettingsManager
import com.mohamedhamza.weatherly.data.UserPreferencesRepository
import com.mohamedhamza.weatherly.databinding.ActivityMainBinding
import com.mohamedhamza.weatherly.view.LanguageChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), LanguageChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var settingsManager: SettingsManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager.getInstance(this)

//        updateResources(baseContext)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.bottomNavigation.itemIconTintList = null
        //how to hide non-selected labels in bottom navigation view
        binding.bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        setSupportActionBar(binding.topAppBar)
        // Add a flag to keep track of the last selected destination
        var lastSelectedDestinationId = -1


        //change the title of the toolbar when the fragment changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.favoritesFragment -> {
                    supportActionBar?.title = resources.getString(R.string.favorites)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    lastSelectedDestinationId = destination.id

                }

                R.id.homeFragment -> {
                    supportActionBar?.title = "Weatherly"
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    lastSelectedDestinationId = destination.id
                }

                R.id.settingsFragment -> {
                    supportActionBar?.title = resources.getString(R.string.settings)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    lastSelectedDestinationId = destination.id
                }

                R.id.alertsFragment -> {
                    supportActionBar?.title = resources.getString(R.string.alerts)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    lastSelectedDestinationId = destination.id
                }

                R.id.favoriteLocationFragment -> {
                    supportActionBar?.title = resources.getString(R.string.favorite_location)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    lastSelectedDestinationId = destination.id
                }

            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == lastSelectedDestinationId) {
                // Prevent re-selection of the same item
                return@setOnItemSelectedListener true
            }

            NavigationUI.onNavDestinationSelected(item, navController)
            lastSelectedDestinationId = item.itemId
            true
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }


    override fun onLanguageChanged(locale: Locale) {
//        updateResources(baseContext)
    }





}

