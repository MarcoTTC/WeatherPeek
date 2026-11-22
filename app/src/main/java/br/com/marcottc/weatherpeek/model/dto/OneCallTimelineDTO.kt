package br.com.marcottc.weatherpeek.model.dto

import com.google.gson.annotations.SerializedName

// Envelope shared by every One Call 4.0 response ("onecall/current" and "onecall/timeline/*").
// See https://openweathermap.org/api/one-call-4
//
// Unlike 3.0, 4.0 has no "current"/"hourly"/"daily" sections: each endpoint returns its records in
// the "data" array ("current" carries exactly one record). Timelines are paginated: "next" and
// "prev" are only present when more records exist beyond the ones returned in this page.
data class OneCallTimelineDTO<T>(
    var lat: Double,
    var lon: Double,
    var timezone: String,
    @SerializedName("timezone_offset") var timezoneOffset: Int,
    var data: List<T>,
    var prev: String? = null,
    var next: String? = null
)
