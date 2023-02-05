package br.com.marcottc.weatherpeek.model.dco

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData

@Entity
data class DailyWeatherCache(
    @PrimaryKey
    var dt: Long,
    var minTemp: Double,
    var maxTemp: Double,
    var iconUrl: String,
    var uvi: Double
) {
    constructor(data: DailyWeatherData) : this(
        data.dt, data.temp.min, data.temp.max, data.weatherList[0].getIconUrl(), data.uvi
    )
}