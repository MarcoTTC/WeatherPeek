package br.com.marcottc.weatherpeek.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.marcottc.weatherpeek.model.room.RoomDatabaseInstance
import br.com.marcottc.weatherpeek.network.RetrofitClientInstance
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.oneCallAppId
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException

class UpdateWeatherCache(private val appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (oneCallAppId.isEmpty()) {
            try {
                val database = RoomDatabaseInstance.getRoomInstance(appContext)
                val currentWeatherCacheDao = database.getCurrentWeatherDao()
                val currentWeather = currentWeatherCacheDao.getValues()

                val oneCallService = RetrofitClientInstance.getRetrofitInstance()
                    .create(OneCallService::class.java)
                val response = oneCallService.getWeatherData(
                    lat = currentWeather.latitude,
                    lon = currentWeather.longitude
                )
                return if (response.isSuccessful) {
                    val availableWeatherData = response.body()
                    if (availableWeatherData != null) {
                        val weatherPeekRepository = WeatherPeekRepository(appContext)
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
                            WeatherDataViewModel::class.java.canonicalName,
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