package com.mohamedhamza.weatherly.view

import android.icu.text.DecimalFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohamedhamza.weatherly.R
import com.mohamedhamza.weatherly.databinding.ItemHourlyBinding
import com.mohamedhamza.weatherly.model.HourlyWeatherInfo
import com.mohamedhamza.weatherly.view.util.DateUtils.formatHour
import com.mohamedhamza.weatherly.view.util.DateUtils.formatTemperature
import com.mohamedhamza.weatherly.view.util.WeatherIconMapper.getWeatherIconResourceId

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    private var hourlyWeatherList: List<HourlyWeatherInfo> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {

        val binding = ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourlyWeather = hourlyWeatherList[position]

        holder.bind(hourlyWeather)
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

    fun submitList(hourlyWeatherList: List<HourlyWeatherInfo>) {
        this.hourlyWeatherList = hourlyWeatherList
        notifyDataSetChanged()
    }

    class HourlyViewHolder(var binding: ItemHourlyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hourlyWeather: HourlyWeatherInfo) {
            binding.hourlyTime.text = formatHour(hourlyWeather.dt)
            binding.hourlyTemp.text = formatTemperature(hourlyWeather.temp)
            binding.hourlyIcon.setImageResource(getWeatherIconResourceId(hourlyWeather.weather[0].icon))

        }
    }





}