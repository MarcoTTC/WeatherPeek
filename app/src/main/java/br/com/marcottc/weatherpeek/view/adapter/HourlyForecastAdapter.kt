package br.com.marcottc.weatherpeek.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.marcottc.weatherpeek.model.HourlyWeatherData
import br.com.marcottc.weatherpeek.view.adapter.viewholder.HourlyForecastViewHolder

class HourlyForecastAdapter : RecyclerView.Adapter<HourlyForecastViewHolder>() {

    private var hourlyForecastDataList: List<HourlyWeatherData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        return HourlyForecastViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        holder.bind(hourlyForecastDataList!![position])
    }

    override fun getItemCount(): Int {
        return if (hourlyForecastDataList == null) {
            0
        } else {
            hourlyForecastDataList!!.size
        }
    }

    fun setHourlyForecastDataList(list: List<HourlyWeatherData>?) {
        hourlyForecastDataList = list
        notifyDataSetChanged()
    }
}