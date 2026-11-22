package br.com.marcottc.weatherpeek.model.dto

// Weather snapshot of a location, assembled from the One Call 4.0 "current", "timeline/1h" and
// "timeline/1day" responses (see OneCallRemoteDataSource).
//
// This is not an API payload by itself: One Call 4.0 has no single endpoint returning everything,
// so this aggregate keeps the shape the repository and the cache objects were built around.
data class OneCallWeatherDTO(
    var lat: Double,
    var lon: Double,
    var timezone: String,
    var timezoneOffset: Int,
    var current: CurrentWeatherData,
    var hourlyDataList: List<HourlyWeatherData>,
    var dailyDataList: List<DailyWeatherData>
) {
    companion object {
        // Returns null when the "current" response carries no record, which leaves the app
        // without anything to display as the current weather.
        fun fromResponses(
            current: OneCallTimelineDTO<CurrentWeatherData>,
            hourly: OneCallTimelineDTO<HourlyWeatherData>,
            daily: OneCallTimelineDTO<DailyWeatherData>
        ): OneCallWeatherDTO? {
            val currentData = current.data.firstOrNull() ?: return null
            return OneCallWeatherDTO(
                current.lat,
                current.lon,
                current.timezone,
                current.timezoneOffset,
                currentData,
                hourly.data,
                daily.data
            )
        }
    }
}
