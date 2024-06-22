package br.com.marcottc.weatherpeek.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.LoggerUtil
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException

class UpdateWeatherCache(
    appContext: Context,
    params: WorkerParameters,
    private val appKeyUtil: AppKeyUtil,
    private val oneCallService: OneCallService,
    private val weatherPeekRepository: WeatherPeekRepository,
    private val logger: LoggerUtil
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!appKeyUtil.isEmpty()) {
            try {
                val locationValue = weatherPeekRepository.getLastLatitudeAndLongitude() ?:
                    return Result.failure()

                val response = oneCallService.getWeatherData(
                    lat = locationValue.first,
                    lon = locationValue.second,
                    appid = appKeyUtil.getAppKey()
                )
                return if (response.isSuccessful) {
                    val availableWeatherData = response.body()
                    if (availableWeatherData != null) {
                        weatherPeekRepository.updateRepository(availableWeatherData)

                        Result.success()
                    } else {
                        Result.failure()
                    }
                } else {
                    Result.failure()
                }
            } catch (exception: Exception) {
                when (exception) {
                    is JsonSyntaxException,
                    is JsonIOException -> {
                        logger.e(
                            UpdateWeatherCache::class.java.simpleName,
                            exception.message ?: "",
                            exception
                        )
                        return Result.failure()
                    }
                    else -> throw exception
                }
            }
        } else {
            return Result.failure()
        }
    }
}