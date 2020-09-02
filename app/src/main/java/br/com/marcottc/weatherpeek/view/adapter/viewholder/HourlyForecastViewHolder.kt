package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.databinding.ItemHourlyForecastBinding
import br.com.marcottc.weatherpeek.model.SingleHourForecastData

class HourlyForecastViewHolder(private var binding: ItemHourlyForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(forecast: SingleHourForecastData) {
        binding.tempValue.text = forecast.temperature
        binding.hourValue.text = forecast.hour
    }

    companion object {
        fun inflate(parent: ViewGroup): HourlyForecastViewHolder {
            val binding = ItemHourlyForecastBinding.inflate(LayoutInflater.from(parent.context))
            return HourlyForecastViewHolder(binding)
        }
    }
}