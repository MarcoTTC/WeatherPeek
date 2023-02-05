package br.com.marcottc.weatherpeek.model.dco

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData

// Cache object for CurrentWeatherData
@Entity
data class CurrentWeatherCache(
    @PrimaryKey
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: Double,
    var pressure: Int,
    var humidity: Int,
    var clouds: Int
) {
    constructor(data: CurrentWeatherData, timezone: String) :
            this(
                data.dt,
                data.sunrise,
                data.sunset,
                data.temp,
                data.pressure,
                data.humidity,
                data.clouds
            )
}