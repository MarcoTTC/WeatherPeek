package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Single record ("data[0]") of the One Call 4.0 "onecall/current" response.
// The record also carries "uvi", which is only displayed by this app as a daily value, so it is
// left out here and silently dropped by Gson while parsing.
//
// "sunrise"/"sunset" are omitted during polar day/night, and "wind_gust", "rain" and "snow" are
// only sent where available: Gson leaves the missing primitives at 0 and the objects at null.
//
// Unlike 3.0, 4.0 sends fractional values for fields that used to be integers (e.g.
// "pressure": 1017.64), so every non-id numeric field is a Double: Gson parses integer JSON values
// into a Double just fine, but fails a whole response on a fractional value for an Int field.
data class CurrentWeatherData(
    var dt: Long,
    var sunrise: Long,
    var sunset: Long,
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
    var rain: RainData?,
    var snow: SnowData?,
    // New in 4.0: ids of the weather alerts active for this record; the full alert is available
    // at "onecall/alert/{id}". Nullable because Gson ignores Kotlin default values.
    @SerializedName("alerts") var alertIds: List<String>? = null
) {
    constructor(
        dt: Long,
        sunrise: Long,
        sunset: Long,
        temp: Double,
        pressure: Int,
        humidity: Int,
        clouds: Int
    ) :
            this(
                dt, sunrise, sunset, temp, 0.0,
                pressure.toDouble(), humidity.toDouble(), 0.0, clouds.toDouble(), 0.0,
                0.0, 0.0, 0.0, listOf(WeatherData(304, "Sunny", "Clear sunny", "08n")),
                RainData(0.0), SnowData(0.0)
            )
}