package br.com.marcottc.weatherpeek.model.dco

import br.com.marcottc.weatherpeek.model.dto.WeatherData

data class WeatherCache(
    var main: String,
    var description: String,
    var icon: String
) {
    constructor(data: WeatherData) :
            this(
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