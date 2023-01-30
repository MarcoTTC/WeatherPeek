package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ItemHourlyForecastBinding
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache

class HourlyForecastViewHolder(private var binding: ItemHourlyForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(hourlyCache: HourlyWeatherCache) {
        binding.tempValue.text = String.format("%.0f°c", hourlyCache.temp)

        if (String.format(
                "%te%tH",
                System.currentTimeMillis(),
                System.currentTimeMillis()
            ) == String.format("%te%tH", hourlyCache.dt * 1000, hourlyCache.dt * 1000)
        ) {
            binding.hourValue.text = binding.root.context.resources.getString(R.string.now_label)
        } else {
            binding.hourValue.text =
                String.format(
                    "%tH:%tM%Tp",
                    hourlyCache.dt * 1000,
                    hourlyCache.dt * 1000,
                    hourlyCache.dt * 1000
                )
        }
    }

    companion object {
        fun inflate(parent: ViewGroup): HourlyForecastViewHolder {
            val binding = ItemHourlyForecastBinding.inflate(LayoutInflater.from(parent.context))
            return HourlyForecastViewHolder(binding)
        }
    }
}