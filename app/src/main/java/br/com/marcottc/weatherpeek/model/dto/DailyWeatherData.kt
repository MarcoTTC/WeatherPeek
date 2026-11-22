package br.com.marcottc.weatherpeek.model.dto

import br.com.marcottc.weatherpeek.model.dto.adapter.PrecipitationDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

// Single record ("data[]") of the One Call 4.0 "onecall/timeline/1day" response.
//
// The record also carries "moonrise", "moonset" and "moon_phase". None of them has a view able to
// display it in this app, so they are intentionally left out of this class and silently dropped by
// Gson while parsing the response. The 3.0 "summary" field no longer exists in 4.0.
// Non-id numeric fields are Doubles, since 4.0 may send fractional values (see CurrentWeatherData).
data class DailyWeatherData(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: TempData,
    @SerializedName("feels_like") var feelsLike: FeelsLikeData,
    var pressure: Double,
    var humidity: Double,
    @SerializedName("dew_point") var dewPoint: Double,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var clouds: Double,
    var pop: Double,
    @field:JsonAdapter(PrecipitationDeserializer::class) var rain: Double,
    var uvi: Double,
    @field:JsonAdapter(PrecipitationDeserializer::class) var snow: Double = 0.0,
    // New in 4.0: visibility of the day in metres (max 10000). The 3.0 daily section had none.
    var visibility: Double = 0.0,
    // New in 4.0: ids of the weather alerts active during this day.
    @SerializedName("alerts") var alertIds: List<String>? = null
) {
    constructor(dt: Long, maxTemp: Double, minTemp: Double) : this(
        dt, 0, 0, TempData(0.0, minTemp, maxTemp, 0.0, 0.0, 0.0),
        FeelsLikeData(0.0, 0.0, 0.0, 0.0), 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, emptyList(), 0.0, 0.0, 0.0,
        0.0
    )
}
