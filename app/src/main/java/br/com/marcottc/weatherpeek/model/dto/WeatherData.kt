package br.com.marcottc.weatherpeek.model.dto


data class WeatherData(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
) {
    fun getIconUrl(): String {
        return "$iconBaseUrl$icon@2x.png"
    }

    companion object {
        private const val iconBaseUrl = "http://openweathermap.org/img/wn/"
    }
}