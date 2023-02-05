package br.com.marcottc.weatherpeek.model.dco

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO

// Cache object for CurrentWeatherData
@Entity
data class CurrentWeatherCache(
    @PrimaryKey
    var dt: Long,
    var latitude: Double,
    var longitude: Double,
    var timezone: String,
    var sunrise: Long,
    var sunset: Long,
    var temp: Double,
    var pressure: Int,
    var humidity: Int,
    var clouds: Int
) {
    constructor(data: OneCallWeatherDTO) :
            this(
                data.current.dt,
                data.lat,
                data.lon,
                data.timezone,
                data.current.sunrise,
                data.current.sunset,
                data.current.temp,
                data.current.pressure,
                data.current.humidity,
                data.current.clouds
            )
}