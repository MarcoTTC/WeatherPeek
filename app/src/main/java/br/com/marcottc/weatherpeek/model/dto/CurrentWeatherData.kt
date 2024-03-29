package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Current weather data at the service call time and location
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
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weatherList: List<WeatherData>,
    var rain: RainData?,
    var snow: SnowData?
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
                pressure, humidity, 0.0, clouds, 0,
                0.0, 0.0, 0.0, listOf(WeatherData(304, "Sunny", "Clear sunny", "08n")),
                RainData(0.0), SnowData(0.0)
            )
}