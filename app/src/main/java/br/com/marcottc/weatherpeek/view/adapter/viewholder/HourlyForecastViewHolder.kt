package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.databinding.ItemHourlyForecastBinding
import br.com.marcottc.weatherpeek.model.HourlyWeatherData

class HourlyForecastViewHolder(private var binding: ItemHourlyForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(hourlyData: HourlyWeatherData) {
        binding.tempValue.text = String.format("%.0f°", hourlyData.temp)
        if (String.format("%tH", System.currentTimeMillis()) == String.format("%tH", hourlyData.dt)) {
            binding.hourValue.text = "NOW"
        } else {
            binding.hourValue.text =
                String.format("%tH:%tM%Tp", hourlyData.dt, hourlyData.dt, hourlyData.dt)
        }
    }

    companion object {
        fun inflate(parent: ViewGroup): HourlyForecastViewHolder {
            val binding = ItemHourlyForecastBinding.inflate(LayoutInflater.from(parent.context))
            return HourlyForecastViewHolder(binding)
        }
    }
}