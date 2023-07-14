package com.mohamedhamza.weatherly.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mohamedhamza.weatherly.MyApplication
import com.mohamedhamza.weatherly.R
import com.mohamedhamza.weatherly.data.AlertMode
import com.mohamedhamza.weatherly.data.SettingsManager
import com.mohamedhamza.weatherly.data.TemperatureUnit
import com.mohamedhamza.weatherly.data.WindSpeedUnit
import com.mohamedhamza.weatherly.databinding.FragmentSettingsBinding
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModel
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewmodel: SettingsViewModel
    private var languageChangeListener: LanguageChangeListener? = null

    private val OVERLAY_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferenceWrapper = (activity?.application as MyApplication).settingsStore
        viewmodel = ViewModelProvider(
            requireActivity(),
            SettingsViewModelFactory(preferenceWrapper)
        )[SettingsViewModel::class.java]


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewmodel.currentLanguage.collectLatest {
                binding.languageButton.text = it
            }
        }

        binding.languageButton.setOnClickListener {
            showLanguagePopup(it, R.menu.lang_popup_menu)
        }

        setUpTemperatureButton()
        setUpWindSpeedButton()
        setUpAlertSwitch()
        setUpAlertModeButton()


    }

    private fun setUpAlertSwitch() {
        viewmodel.alertEnabled.observe(viewLifecycleOwner) {
            binding.alertsSwitch.isChecked = it
        }
        binding.alertsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewmodel.saveAlertEnabled(isChecked)
        }
    }

    private fun setUpAlertModeButton() {
        binding.alertModeButton.setOnClickListener {
            showAlertModePopup(it, R.menu.alert_mode_popup_menu)
        }
        viewmodel.alertMode.observe(viewLifecycleOwner) {
            when (it) {
                "Notification" -> {
                    binding.alertModeButton.text = getString(R.string.notifications)
                }
                "Dialog" -> {
                    binding.alertModeButton.text = getString(R.string.dialog)
                }
            }
        }
    }

    private fun showAlertModePopup(it: View?, alertModePopupMenu: Int) {
        val popup = PopupMenu(requireContext(), it)
        popup.menuInflater.inflate(alertModePopupMenu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.notifications -> {
                    viewmodel.saveAlertMode(AlertMode.NOTIFICATION)
                    true
                }
                R.id.dialog -> {

                    if (hasOverlayPermission()) {
                        viewmodel.saveAlertMode(AlertMode.DIALOG)
                    } else {
                        requestOverlayPermission()
                    }
                    viewmodel.saveAlertMode(AlertMode.DIALOG)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun requestOverlayPermission() {
        //request the overlay permission from the user
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + requireActivity().packageName)
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(requireContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission()) {
                // Overlay permission granted
                viewmodel.saveAlertMode(AlertMode.DIALOG)

            } else {
                // Overlay permission not granted
                viewmodel.saveAlertMode(AlertMode.NOTIFICATION)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.overlay_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUpWindSpeedButton() {
        binding.windSpeedButton.setOnClickListener {
            showWindSpeedPopup(it, R.menu.windspeed_popup_menu)
        }
        viewmodel.windSpeedUnit.observe(viewLifecycleOwner) {
            Log.d("TAG", "setUpWindSpeedButton: $it outside")
            when (it) {

                "mph" -> {
                    Log.d("TAG", "setUpWindSpeedButton: $it")
                    binding.windSpeedButton.text = getString(R.string.miles_by_hour)
                }
                "m/s" -> {
                    Log.d("TAG", "setUpWindSpeedButton: $it")
                    binding.windSpeedButton.text = getString(R.string.meter_by_sec)
                }
            }
        }
    }


    private fun showWindSpeedPopup(it: View?, windspeedPopupMenu: Int) {
        val popup = PopupMenu(requireContext(), it)
        popup.menuInflater.inflate(windspeedPopupMenu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.mph -> {
                    Log.d("TAG", "showWindSpeedPopup: mph")
                    viewmodel.saveWindSpeedUnit(WindSpeedUnit.MPH)
                    true
                }
                R.id.ms -> {
                    Log.d("TAG", "showWindSpeedPopup: ms")
                    viewmodel.saveWindSpeedUnit(WindSpeedUnit.MS)
                    true
                }

                else -> false
            }
        }
        popup.show()

    }

    private fun setUpTemperatureButton(){
        binding.tempratureButton.setOnClickListener {
            showTemperaturePopup(it, R.menu.temp_popup_menu)
        }

        viewmodel.temperatureUnit.observe(viewLifecycleOwner) {
            when (it) {
                "°C" -> {
                    binding.tempratureButton.text = getString(R.string.celsius)
                }
                "°F" -> {
                    binding.tempratureButton.text = getString(R.string.fahrenheit)
                }
                "K" -> {
                    binding.tempratureButton.text = getString(R.string.kelvin)
                }
            }
        }
    }



    private fun showTemperaturePopup(view: View, menuRes: Int) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.celsius -> {
//                    settingsManager.temperatureUnit = getString(R.string.celsius)
                    viewmodel.saveTemperatureUnit(TemperatureUnit.CELSIUS)
                    true
                }

                R.id.fahrenheit -> {
                    viewmodel.saveTemperatureUnit(TemperatureUnit.FAHRENHEIT)
                    true
                }
                R.id.kelvin -> {
                    viewmodel.saveTemperatureUnit(TemperatureUnit.KELVIN)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }




    private fun showLanguagePopup(view: View, menuRes: Int) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.english -> {
                    viewmodel.changeLanguage(getString(R.string.english))
//                    binding.languageButton.text = getString(R.string.english)
//                    settingsManager.language = getString(R.string.english)
//                    languageChangeListener?.onLanguageChanged(
//                        Locale("en")
//                    )

                    true
                }

                R.id.arabic -> {
                    viewmodel.changeLanguage(getString(R.string.arabic))
//                    binding.languageButton.text = getString(R.string.arabic)
//                    settingsManager.language = getString(R.string.arabic)
//                    languageChangeListener?.onLanguageChanged(
//                        Locale("ar")
//                    )
                    true
                }

                else -> false
            }
        }
        popup.show()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LanguageChangeListener) {
            languageChangeListener = context
        } else {
            throw RuntimeException("$context must implement LanguageChangeListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        languageChangeListener = null
    }


}