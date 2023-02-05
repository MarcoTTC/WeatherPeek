package br.com.marcottc.weatherpeek.model.dco

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData

@Entity
data class HourlyWeatherCache(
    @PrimaryKey
    var dt: Long,
    var temp: Double
) {
    constructor(data: HourlyWeatherData) : this(
        data.dt, data.temp
    )
}