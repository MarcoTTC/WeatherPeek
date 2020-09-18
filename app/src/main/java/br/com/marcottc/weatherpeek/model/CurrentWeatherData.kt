package br.com.marcottc.weatherpeek.model

import com.google.gson.annotations.SerializedName

data class CurrentWeatherData(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    var clouds: Int,
    var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Int,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var rain: RainData
) {
    constructor(dt: Long, sunrise: Long, sunset: Long, temp: Double, pressure: Int, humidity: Int, clouds: Int) :
        this(dt, sunrise, sunset, temp, 0.0, pressure, humidity, 0.0, clouds, 0, 0, 0.0,
        listOf(WeatherData(304, "Sunny", "Clear sunny", "08n")), RainData(0.0))
}