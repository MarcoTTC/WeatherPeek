package br.com.marcottc.weatherpeek.model

import com.google.gson.annotations.SerializedName

data class DailyWeatherData(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: TempData,
    @SerializedName("feels_like") var feelsLike: FeelsLikeData,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Int,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var clouds: Int,
    var pop: Int,
    var rain: RainData,
    var uvi: Double
)