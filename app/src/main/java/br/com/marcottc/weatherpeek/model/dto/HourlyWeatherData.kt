package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Single record ("data[]") of the One Call 4.0 "onecall/timeline/1h" response.
// The record also carries "uvi" and "snow". Neither has a view able to display it in this app, so
// both are left out here and silently dropped by Gson while parsing.
// Non-id numeric fields are Doubles, since 4.0 may send fractional values (see CurrentWeatherData).
data class HourlyWeatherData(
    var dt: Long,
    var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    var pressure: Double,
    var humidity: Double,
    @SerializedName("dew_point") var dewPoint: Double,
    var clouds: Double,
    var visibility: Double,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var pop: Double,
    var rain: RainData?,
    // New in 4.0: ids of the weather alerts active during this hour.
    @SerializedName("alerts") var alertIds: List<String>? = null
) {
    constructor(dt: Long, temp: Double) : this(
        dt, temp, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, emptyList(), 0.0,
        RainData(0.0)
    )
}