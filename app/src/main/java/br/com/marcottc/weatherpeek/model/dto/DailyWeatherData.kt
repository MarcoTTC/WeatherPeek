package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Day based weather data.
//
// The One Call 3.0 daily section also carries "moonrise", "moonset", "moon_phase", "summary" and
// "snow". None of them has a view able to display it in this app, so they are intentionally left
// out of this class and silently dropped by Gson while parsing the response.
data class DailyWeatherData(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: TempData,
    @SerializedName("feels_like") var feelsLike: FeelsLikeData,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var clouds: Int,
    var pop: Double,
    var rain: Double,
    var uvi: Double
) {
    constructor(dt: Long, maxTemp: Double, minTemp: Double) : this(
        dt, 0, 0, TempData(0.0, minTemp, maxTemp, 0.0, 0.0, 0.0),
        FeelsLikeData(0.0, 0.0, 0.0, 0.0), 0, 0, 0.0,
        0.0, 0.0, 0.0, emptyList(), 0, 0.0, 0.0,
        0.0
    )
}
