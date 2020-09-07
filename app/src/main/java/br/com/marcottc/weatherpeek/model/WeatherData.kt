package br.com.marcottc.weatherpeek.model

data class WeatherData(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
)