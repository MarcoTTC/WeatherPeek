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
)