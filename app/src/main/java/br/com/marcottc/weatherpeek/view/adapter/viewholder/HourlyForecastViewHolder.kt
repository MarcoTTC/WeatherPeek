package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ItemHourlyForecastBinding
import br.com.marcottc.weatherpeek.model.HourlyWeatherData

class HourlyForecastViewHolder(private var binding: ItemHourlyForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(hourlyData: HourlyWeatherData) {
        binding.tempValue.text = String.format("%.0f°c", hourlyData.temp)

        if (String.format(
                "%te%tH",
                System.currentTimeMillis(),
                System.currentTimeMillis()
            ) == String.format("%te%tH", hourlyData.dt * 1000, hourlyData.dt * 1000)
        ) {
            binding.hourValue.text = binding.root.context.resources.getString(R.string.now_label)
        } else {
            binding.hourValue.text =
                String.format(
                    "%tH:%tM%Tp",
                    hourlyData.dt * 1000,
                    hourlyData.dt * 1000,
                    hourlyData.dt * 1000
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