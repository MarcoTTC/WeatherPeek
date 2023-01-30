package br.com.marcottc.weatherpeek.model.dco

import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import java.util.stream.Collectors

data class DailyWeatherCache(
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