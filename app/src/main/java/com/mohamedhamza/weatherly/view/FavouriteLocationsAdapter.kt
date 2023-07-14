package com.mohamedhamza.weatherly.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mohamedhamza.weatherly.R
import com.mohamedhamza.weatherly.databinding.FavouriteLocationItemBinding
import com.mohamedhamza.weatherly.model.FavouriteLocation
import com.mohamedhamza.weatherly.view.util.DateUtils

class FavouriteLocationsAdapter(
    private val onFavouriteLocationClick: (FavouriteLocation) -> Unit,
    private val onFavouriteLocationAlertClick: (FavouriteLocation) -> Unit,
    private val onFavouriteLocationDelete: (FavouriteLocation) -> Unit
) : ListAdapter<FavouriteLocation, FavouriteLocationsAdapter.FavouriteLocationsViewHolder>(
    FavItemDiffCallback()
) {

    private var currentLanguage: String = "English"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteLocationsViewHolder {
        val binding =
            FavouriteLocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteLocationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteLocationsViewHolder, position: Int) {
        val favouriteLocation = getItem(position)
        holder.bind(favouriteLocation)
    }

    fun updateLanguage(language: String) {
        currentLanguage = language
        notifyDataSetChanged()
    }



    inner class FavouriteLocationsViewHolder(var binding: FavouriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favouriteLocation: FavouriteLocation) {
            binding.cityName.text =
                if (currentLanguage == "English") favouriteLocation.name else favouriteLocation.arName
            val flagUrl = "https://www.flagsapi.com/${favouriteLocation.countryCode}/flat/64.png"
            Glide.with(binding.root.context)
                .load(flagUrl)
                .into(binding.favouriteLocationFlag)


            binding.favouriteLocationAlert.isActivated = favouriteLocation.isAlertEnabled
            binding.favouriteLocationAlert.setOnClickListener {
                onFavouriteLocationAlertClick.invoke(favouriteLocation)
            }

            binding.cityTime.text = DateUtils.getTimeFromTimeZone(favouriteLocation.timeZone)

            binding.favouriteLocationDelete.setOnClickListener {
                onFavouriteLocationDelete.invoke(favouriteLocation)
            }

            binding.root.setOnClickListener {

                onFavouriteLocationClick.invoke(favouriteLocation)
            }

        }
    }

    class FavItemDiffCallback : DiffUtil.ItemCallback<FavouriteLocation>() {
        override fun areItemsTheSame(
            oldItem: FavouriteLocation,
            newItem: FavouriteLocation
        ): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FavouriteLocation,
            newItem: FavouriteLocation
        ): Boolean {

            return oldItem == newItem
        }
    }

}


