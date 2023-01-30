package br.com.marcottc.weatherpeek.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.view.adapter.viewholder.DailyForecastViewHolder

class DailyForecastAdapter : RecyclerView.Adapter<DailyForecastViewHolder>() {

    private var dailyForecastDataList: List<DailyWeatherCache>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        return DailyForecastViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        holder.bind(dailyForecastDataList!![position])
    }

    override fun getItemCount(): Int {
        return if (dailyForecastDataList == null) {
            0
        } else {
            dailyForecastDataList!!.size
        }
    }

    fun setDailyForecastDataList(list: List<DailyWeatherCache>?) {
        dailyForecastDataList = list
        notifyDataSetChanged()
    }
}