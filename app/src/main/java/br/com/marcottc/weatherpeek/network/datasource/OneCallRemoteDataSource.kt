package br.com.marcottc.weatherpeek.network.datasource

import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallTimelineDTO
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.network.service.OneCallService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Response

// Builds the weather snapshot that One Call 3.0 used to return in a single request out of the
// One Call 4.0 "current", "timeline/1h" and "timeline/1day" endpoints.
//
// The result keeps the Retrofit Response contract callers already rely on:
// - success with a body, when every required call succeeded;
// - success without a body, when the API answered but sent no current weather record;
// - the error response of the first failing call otherwise, so its ErrorResponse body can be read.
class OneCallRemoteDataSource(
    private val oneCallService: OneCallService,
    private val hourlyForecastHours: Int = DEFAULT_HOURLY_FORECAST_HOURS
) {

    suspend fun getWeatherData(
        lat: Double,
        lon: Double,
        appid: String
    ): Response<OneCallWeatherDTO> = coroutineScope {
        val currentCall = async { oneCallService.getCurrentWeather(lat = lat, lon = lon, appid = appid) }
        val hourlyCall = async { getHourlyTimeline(lat, lon, appid) }
        val dailyCall = async { oneCallService.getDailyTimeline(lat = lat, lon = lon, appid = appid) }

        val current = currentCall.await()
        val hourly = hourlyCall.await()
        val daily = dailyCall.await()

        val failed = listOf(current, hourly, daily).firstOrNull { !it.isSuccessful }
        if (failed != null) {
            return@coroutineScope Response.error(failed.errorBody()!!, failed.raw())
        }

        val currentBody = current.body()
        val hourlyBody = hourly.body()
        val dailyBody = daily.body()
        val snapshot = if (currentBody != null && hourlyBody != null && dailyBody != null) {
            OneCallWeatherDTO.fromResponses(currentBody, hourlyBody, dailyBody)
        } else {
            null
        }
        Response.success(snapshot, current.raw())
    }

    // 4.0 serves at most 20 hourly records per page, while this app shows a 48h forecast like 3.0
    // did. The following pages are requested with "start" set right after the last received
    // record, while the API reports there is more data ("next").
    //
    // A failing follow-up page does not fail the refresh: the hours already received are kept.
    private suspend fun getHourlyTimeline(
        lat: Double,
        lon: Double,
        appid: String
    ): Response<OneCallTimelineDTO<HourlyWeatherData>> {
        val firstPage = oneCallService.getHourlyTimeline(lat = lat, lon = lon, appid = appid)
        val firstBody = firstPage.body()
        if (!firstPage.isSuccessful || firstBody == null) {
            return firstPage
        }

        val records = firstBody.data.toMutableList()
        var hasNextPage = firstBody.next != null
        while (hasNextPage && records.isNotEmpty() && records.size < hourlyForecastHours) {
            val lastDt = records.last().dt
            val page = oneCallService.getHourlyTimeline(
                lat = lat,
                lon = lon,
                start = lastDt + SECONDS_IN_AN_HOUR,
                appid = appid
            )
            val pageBody = page.body()
            if (!page.isSuccessful || pageBody == null) break

            val newRecords = pageBody.data.filter { it.dt > lastDt }
            if (newRecords.isEmpty()) break
            records += newRecords
            hasNextPage = pageBody.next != null
        }

        return Response.success(
            firstBody.copy(data = records.take(hourlyForecastHours), next = null),
            firstPage.raw()
        )
    }

    companion object {
        const val DEFAULT_HOURLY_FORECAST_HOURS = 48
        private const val SECONDS_IN_AN_HOUR = 3600L
    }
}
