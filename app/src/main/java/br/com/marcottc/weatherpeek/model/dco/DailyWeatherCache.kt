package br.com.marcottc.weatherpeek.model.dco

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import java.util.stream.Collectors

@Entity
data class DailyWeatherCache(
    @PrimaryKey
    var dt: Long,
    var minTemp: Double,
    var maxTemp: Double,
    var weatherList: List<WeatherCache>,
    var uvi: Double
) {
    constructor(data: DailyWeatherData) : this(
        data.dt, data.temp.min, data.temp.max, data.weatherList.stream().map {
            WeatherCache(it)
        }.collect(Collectors.toList()), data.uvi
    )
}