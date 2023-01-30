package br.com.marcottc.weatherpeek.view.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.databinding.ItemDailyForecastBinding
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import com.bumptech.glide.Glide

class DailyForecastViewHolder(private var binding: ItemDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(dailyCache: DailyWeatherCache) {
        binding.dayValue.text = String.format("%Ta", dailyCache.dt*1000)
        val resources = binding.root.context.resources
        val iconRatio = (resources.displayMetrics.density * 48).toInt()
        Glide.with(binding.root)
            .load(dailyCache.weatherList[0].getIconUrl())
            .override(iconRatio, iconRatio)
            .centerInside()
            .into(binding.weatherIcon)
        binding.maxTempValue.text = String.format("%.0f°", dailyCache.maxTemp)
        binding.minTempValue.text = String.format("%.0f°", dailyCache.minTemp)
    }

    companion object {
        fun inflate(parent: ViewGroup): DailyForecastViewHolder {
            val binding = ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context))
            return DailyForecastViewHolder(binding)
        }
    }
}