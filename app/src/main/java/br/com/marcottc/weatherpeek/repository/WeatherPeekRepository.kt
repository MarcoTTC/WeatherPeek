package br.com.marcottc.weatherpeek.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.model.room.*
import java.util.stream.Collectors
import kotlin.math.abs

class WeatherPeekRepository(applicationContext: Context) {

    val currentWeatherCache: LiveData<CurrentWeatherCache> = liveData {
        emitSource(currentWeatherCacheDao.get())
    }

    val weatherListCache: LiveData<List<WeatherCache>> = liveData {
        emitSource(weatherCacheDao.getAll())
    }

    val hourlyWeatherListCache: LiveData<List<HourlyWeatherCache>> = liveData {
        emitSource(hourlyWeatherCacheDao.getAll())
    }

    val dailyWeatherListCache: LiveData<List<DailyWeatherCache>> = liveData {
        emitSource(dailyWeatherCacheDao.getAll())
    }

    private var database: WeatherPeekDatabase
    private var weatherCacheDao: WeatherCacheDao
    private var currentWeatherCacheDao: CurrentWeatherDao
    private var hourlyWeatherCacheDao: HourlyWeatherCacheDao
    private var dailyWeatherCacheDao: DailyWeatherCacheDao

    init {
        database = RoomDatabaseInstance.getRoomInstance(applicationContext)
        weatherCacheDao = database.getWeatherCacheDao()
        currentWeatherCacheDao = database.getCurrentWeatherDao()
        hourlyWeatherCacheDao = database.getHourlyWeatherCacheDao()
        dailyWeatherCacheDao = database.getDailyWeatherCacheDao()
    }

    suspend fun updateRepository(oneCallWeatherDTO: OneCallWeatherDTO) {
        val currentWeather = CurrentWeatherCache(
            oneCallWeatherDTO
        )
        currentWeatherCacheDao.clear()
        currentWeatherCacheDao.insert(currentWeather)

        val weatherList =
            oneCallWeatherDTO.current.weatherList.stream().map { data ->
                WeatherCache(data)
            }.collect(Collectors.toList())
        weatherCacheDao.clear()
        weatherCacheDao.insertAll(*weatherList.toTypedArray())

        val hourlyWeatherList =
            oneCallWeatherDTO.hourlyDataList.stream().map { data ->
                HourlyWeatherCache(data)
            }.collect(Collectors.toList())
        hourlyWeatherCacheDao.clear()
        hourlyWeatherCacheDao.insertAll(*hourlyWeatherList.toTypedArray())

        val dailyWeatherList =
            oneCallWeatherDTO.dailyDataList.stream().map { data ->
                DailyWeatherCache(data)
            }.collect(Collectors.toList())
        dailyWeatherCacheDao.clear()
        dailyWeatherCacheDao.insertAll(*dailyWeatherList.toTypedArray())
    }

    fun databaseRefreshRequired(currentLat: Double, currentLon: Double): Boolean {
        val currentWeather = currentWeatherCache.value
        val hourlyWeatherList = hourlyWeatherListCache.value
        if (
            currentWeather == null ||
            hourlyWeatherList == null ||
            hourlyWeatherList.isEmpty()
        ) {
            return true
        } else {
            return if (abs(currentWeather.latitude - currentLat) >= 1 ||
                abs(currentWeather.longitude - currentLon) >= 1) {
                true
            } else {
                val currentTimeMillis = System.currentTimeMillis()
                val firstHourlyForecastTimeMillis = hourlyWeatherList[0].dt * 1000
                currentTimeMillis - firstHourlyForecastTimeMillis >= 3600000
            }
        }
    }
}