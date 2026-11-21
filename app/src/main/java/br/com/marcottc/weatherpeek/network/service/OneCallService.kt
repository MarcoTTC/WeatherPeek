package br.com.marcottc.weatherpeek.network.service

import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.util.oneCallAppId
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OneCallService {

    // "minutely" and "alerts" are excluded on purpose: this app has no view able to display a
    // minute by minute precipitation forecast, nor the government issued weather alerts, so they
    // are dropped at the request level instead of being downloaded and thrown away afterwards.
    @GET("data/3.0/onecall")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br",
        @Query("exclude") exclude: String = "minutely,alerts",
        @Query("appid") appid: String = oneCallAppId
    ) : Response<OneCallWeatherDTO>
}
