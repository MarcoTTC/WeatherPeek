package br.com.marcottc.weatherpeek.network.service

import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallTimelineDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// One Call 4.0 (https://openweathermap.org/api/one-call-4).
//
// 4.0 replaced the single "onecall" endpoint (and its "exclude" parameter) with one endpoint per
// data set. Only the ones backing a view are declared here: the 1min/15min timelines and the alert
// details endpoint ("onecall/alert/{id}") have no view able to display them yet.
//
// Every request is billed separately, so prefer OneCallRemoteDataSource, which combines these
// calls into the snapshot the app caches.
interface OneCallService {

    @GET("data/4.0/onecall/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br",
        @Query("appid") appid: String
    ): Response<OneCallTimelineDTO<CurrentWeatherData>>

    // Up to 20 records per page. "start" (unix UTC seconds) defaults to now on the API side.
    @GET("data/4.0/onecall/timeline/1h")
    suspend fun getHourlyTimeline(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("start") start: Long? = null,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br",
        @Query("appid") appid: String
    ): Response<OneCallTimelineDTO<HourlyWeatherData>>

    // Up to 10 records per page. "start" (unix UTC seconds) defaults to now on the API side.
    @GET("data/4.0/onecall/timeline/1day")
    suspend fun getDailyTimeline(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("start") start: Long? = null,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br",
        @Query("appid") appid: String
    ): Response<OneCallTimelineDTO<DailyWeatherData>>
}
