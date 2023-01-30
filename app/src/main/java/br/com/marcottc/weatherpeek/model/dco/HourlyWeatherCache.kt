package br.com.marcottc.weatherpeek.model.dco

import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData

data class HourlyWeatherCache(
    var dt: Long,
    var temp: Double
) {
    constructor(data: HourlyWeatherData) : this(
        data.dt, data.temp
    )
}