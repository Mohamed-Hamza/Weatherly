package com.mohamedhamza.weatherly.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mohamedhamza.weatherly.databinding.AlertItemBinding
import com.mohamedhamza.weatherly.model.Alert
import com.mohamedhamza.weatherly.view.util.DateUtils

class AlertListAdapter(
    private val onAlertClickListener: (Alert) -> Unit,
    private val onDeleteClickListener: (Alert) -> Unit,

    ) : ListAdapter<Alert, AlertListAdapter.AlertViewHolder>(AlertDiffCallback()) {

    private var currentLanguage: String = "English"


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertListAdapter.AlertViewHolder {
        val binding =
            AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AlertListAdapter.AlertViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert)
    }

    inner class AlertViewHolder(private val binding: AlertItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert) {
            binding.root.setOnClickListener {
                onAlertClickListener(alert)
            }
            binding.deleteIcon.setOnClickListener {
                onDeleteClickListener(alert)
            }

            Glide.with(binding.root.context)
                .load("https://www.flagsapi.com/${alert.locationCountryCode}/flat/64.png")
                .into(binding.locationFlag)
            binding.locationName.text = if (currentLanguage == "English") alert.locationName else alert.locationArName
            binding.startDate.text = DateUtils.convertLongToDateString(alert.startDate,alert.timeZone)
            binding.endDate.text = DateUtils.convertLongToDateString(alert.endDate,alert.timeZone)
        }
    }

    fun updateLanguage(language: String) {
        Log.d("AlertListAdapter", "updateLanguage: $language")
        currentLanguage = language
//        notifyDataSetChanged()
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }

    }


}