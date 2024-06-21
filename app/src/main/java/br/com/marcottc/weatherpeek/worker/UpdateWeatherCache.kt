package br.com.marcottc.weatherpeek.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.marcottc.weatherpeek.model.room.CurrentWeatherDao
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.viewmodel.WeatherPeekViewModel
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import org.koin.java.KoinJavaComponent.inject

class UpdateWeatherCache(private val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val currentWeatherCacheDao: CurrentWeatherDao by inject(CurrentWeatherDao::class.java)

    private val appKeyUtil: AppKeyUtil by inject(AppKeyUtil::class.java)
    private val oneCallService: OneCallService by inject(OneCallService::class.java)

    private val weatherPeekRepository: WeatherPeekRepository by inject(WeatherPeekRepository::class.java)


    override suspend fun doWork(): Result {
        if (appKeyUtil.isEmpty()) {
            try {
                val currentWeather = currentWeatherCacheDao.getValues()
                val latitude = currentWeather?.latitude
                val longitude = currentWeather?.longitude

                if (latitude == null || longitude == null) {
                    return Result.failure()
                }
                val response = oneCallService.getWeatherData(
                    lat = latitude,
                    lon = longitude
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
                        Log.e(
                            WeatherPeekViewModel::class.java.canonicalName,
                            exception.message,
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