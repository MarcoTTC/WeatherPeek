package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.databinding.ItemDailyForecastBinding
import br.com.marcottc.weatherpeek.model.DailyWeatherData

class DailyForecastViewHolder(private var binding: ItemDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(dailyData: DailyWeatherData) {
        binding.dayValue.text = String.format("%Ta", dailyData.dt)
        binding.maxTempValue.text = String.format("%.0f°", dailyData.temp.max)
        binding.minTempValue.text = String.format("%.0f°", dailyData.temp.min)
    }

    companion object {
        fun inflate(parent: ViewGroup): DailyForecastViewHolder {
            val binding = ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context))
            return DailyForecastViewHolder(binding)
        }
    }
}