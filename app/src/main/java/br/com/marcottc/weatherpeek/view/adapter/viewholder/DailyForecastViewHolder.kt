package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.databinding.ItemDailyForecastBinding
import br.com.marcottc.weatherpeek.model.SingleDayForecastData

class DailyForecastViewHolder(private var binding: ItemDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(forecast: SingleDayForecastData) {
        binding.dayValue.text = forecast.day
        binding.maxTempValue.text = forecast.maxTemperature
        binding.minTempValue.text = forecast.minTemperature
    }

    companion object {
        fun inflate(parent: ViewGroup): DailyForecastViewHolder {
            val binding = ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context))
            return DailyForecastViewHolder(binding)
        }
    }
}