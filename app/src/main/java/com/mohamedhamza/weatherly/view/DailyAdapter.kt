package com.mohamedhamza.weatherly.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mohamedhamza.weatherly.databinding.ItemDailyBinding
import com.mohamedhamza.weatherly.model.DailyWeatherInfo
import com.mohamedhamza.weatherly.view.util.DateUtils.formatTemperature
import com.mohamedhamza.weatherly.view.util.DateUtils.formatWeekDay
import com.mohamedhamza.weatherly.view.util.WeatherIconMapper.getWeatherIconResourceId

class DailyAdapter: RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {
    private var dailyWeatherList: List<DailyWeatherInfo> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        //using view binding
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val dailyWeather = dailyWeatherList[position]
        holder.bind(dailyWeather)
    }

    override fun getItemCount(): Int {
        return dailyWeatherList.size
    }

    fun submitList(dailyWeatherList: List<DailyWeatherInfo>) {
        this.dailyWeatherList = dailyWeatherList
        notifyDataSetChanged()
    }

    class DailyViewHolder(var binding: ItemDailyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dailyWeather: DailyWeatherInfo) {
            binding.dailyDay.text = formatWeekDay(dailyWeather.dt)
            binding.dailyDescription.text = dailyWeather.weather[0].main
            binding.dailyMax.text = "${dailyWeather.temp.max.toInt()}°"
            binding.dailyMin.text = "${dailyWeather.temp.min.toInt()}°"
            binding.dailyIcon.setImageResource(getWeatherIconResourceId(dailyWeather.weather[0].icon))

        }
    }
}