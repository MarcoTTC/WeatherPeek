package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Root object of the One Call 3.0 "current and forecasts weather data" response.
//
// The "minutely" and "alerts" sections are not requested (see OneCallService) and are not mapped
// here, since this app has no view able to display them.
data class OneCallWeatherDTO(
    var lat: Double,
    var lon: Double,
    var timezone: String,
    @SerializedName("timezone_offset") var timezoneOffset: String,
    var current: CurrentWeatherData,
    @SerializedName("hourly") var hourlyDataList: List<HourlyWeatherData>,
    @SerializedName("daily") var dailyDataList: List<DailyWeatherData>
)
