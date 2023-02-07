package br.com.marcottc.weatherpeek.model.dco

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.marcottc.weatherpeek.model.dto.WeatherData

@Entity
data class WeatherCache(
    @PrimaryKey
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
) {
    constructor(data: WeatherData) :
            this(
                data.id,
                data.main,
                data.description,
                data.icon
            )

    fun getIconUrl(): String {
        return "${iconBaseUrl}$icon@2x.png"
    }

    companion object {
        private const val iconBaseUrl = "http://openweathermap.org/img/wn/"
    }
}