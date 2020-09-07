package br.com.marcottc.weatherpeek.model

import com.google.gson.annotations.SerializedName

data class OneCallWeatherData(
    var lat: Double,
    var lon: Double,
    var timezone: String,
    @SerializedName("timezone_offset") var timezoneOffset: String,
    var current: CurrentWeatherData,
    @SerializedName("hourly") var hourlyDataList: List<HourlyWeatherData>,
    @SerializedName("daily") var dailyDataList: List<DailyWeatherData>
)