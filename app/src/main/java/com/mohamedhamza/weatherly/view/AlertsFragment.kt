package com.mohamedhamza.weatherly.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mohamedhamza.productsviewmodel.model.ConcreteLocalSource
import com.mohamedhamza.weatherly.MyApplication
import com.mohamedhamza.weatherly.R
import com.mohamedhamza.weatherly.data.WeatherRepository
import com.mohamedhamza.weatherly.databinding.FragmentAlertsBinding
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.viewmodel.AlertsViewModel
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModel
import com.mohamedhamza.weatherly.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertsFragment : Fragment() {
    lateinit var binding: FragmentAlertsBinding
    lateinit var alertsViewModel: AlertsViewModel
    lateinit var alertListAdapter: AlertListAdapter
    lateinit var settingsViewModel: SettingsViewModel
    private var currentList: List<Alert> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceWrapper = (activity?.application as MyApplication).settingsStore
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(preferenceWrapper)
        )[SettingsViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val viewModelFactory = AlertsViewModel.AlertsViewModelFactory(
            WeatherRepository.getInstance(ConcreteLocalSource(requireContext()))
        )
        alertsViewModel = viewModelFactory.create(AlertsViewModel::class.java)


        setupRecyclerView()

    }

    fun setupRecyclerView() {
        alertListAdapter = AlertListAdapter(
            onAlertClickListener = { alert ->
                showDateRangePickerForAlert(alert)
            },
            onDeleteClickListener = { alert ->
                showDeleteConfirmationDialog(alert)

            }
        )
        binding.alertsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alertListAdapter
        }



        alertsViewModel.alerts.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.alertsGroup.visibility = View.GONE
                binding.emptyStateGroup.visibility = View.VISIBLE
            } else {
                binding.alertsGroup.visibility = View.VISIBLE
                binding.emptyStateGroup.visibility = View.GONE
                currentList = it
                alertListAdapter.submitList(currentList)
            }
        }

        lifecycleScope.launch {
            settingsViewModel.currentLanguage.collectLatest {
                alertListAdapter.updateLanguage(it)
                alertListAdapter.submitList(null)
                alertListAdapter.submitList(currentList)
            }
        }

    }

    private fun showDateRangePickerForAlert(alert: Alert) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val startDate = alert.startDate
        val endDate = alert.endDate
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setSelection(Pair(startDate, endDate))
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        dateRangePicker.show(childFragmentManager, "date_range_picker")
        dateRangePicker.addOnPositiveButtonClickListener {
            val newStartDate = it.first
            val newEndDate = it.second
            if (newStartDate != null && newEndDate != null) {
                val newAlert = alert.copy(startDate = newStartDate, endDate = newEndDate)
                alertsViewModel.insertAlert(newAlert)
            }
        }

    }

    private fun showDeleteConfirmationDialog(alert: Alert) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_alert))
            .setMessage(getString(R.string.delete_alert_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                alertsViewModel.deleteAlert(alert)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}