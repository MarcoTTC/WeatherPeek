package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Hour based weather data
data class HourlyWeatherData(
    var dt: Long,
    var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    var clouds: Int,
    var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var pop: Double,
    var rain: RainData?
) {
    constructor(dt: Long, temp: Double) : this(
        dt, temp, 0.0, 0, 0, 0.0, 0,
        0, 0.0, 0.0, 0.0, emptyList(), 0.0,
        RainData(0.0)
    )
}