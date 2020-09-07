package br.com.marcottc.weatherpeek.model

import br.com.marcottc.weatherpeek.model.RainData
import br.com.marcottc.weatherpeek.model.WeatherData
import com.google.gson.annotations.SerializedName

data class HourlyWeatherData(
    var dt: Long,
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
    var pop: Double,
    var rain: RainData
)