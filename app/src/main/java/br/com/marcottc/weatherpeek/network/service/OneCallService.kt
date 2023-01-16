package br.com.marcottc.weatherpeek.network.service

import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.util.oneCallAppId
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OneCallService {

    @GET("data/2.5/onecall")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br",
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = oneCallAppId
    ) : Response<OneCallWeatherDTO>
}