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

class WeatherPeekRepository(applicationContext: Context) {

    var currentWeatherCache: LiveData<CurrentWeatherCache?> = liveData {
        val currentWeather = currentWeatherCacheDao.get()
        emit(currentWeather)
    }
        private set

    var weatherListCache: LiveData<List<WeatherCache>?> = liveData {
        val weather = weatherCacheDao.getAll()
        emit(weather)
    }
        private set

    var hourlyWeatherListCache: LiveData<List<HourlyWeatherCache>?> = liveData {
        val hourlyWeatherCache = hourlyWeatherCacheDao.getAll()
        emit(hourlyWeatherCache)
    }
        private set

    var dailyWeatherListCache: LiveData<List<DailyWeatherCache>?> = liveData {
        val dailyWeatherCache = dailyWeatherCacheDao.getAll()
        emit(dailyWeatherCache)
    }
        private set

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

    fun databaseRefreshRequired(): Boolean {
        val hourlyWeatherList = hourlyWeatherListCache.value
        return if (hourlyWeatherList == null || hourlyWeatherList.isEmpty()) {
            true
        } else {
            val currentTimeMillis = System.currentTimeMillis()
            val firstHourlyForecastTimeMillis = hourlyWeatherList[0].dt * 1000
            currentTimeMillis - firstHourlyForecastTimeMillis >= 3600000
        }
    }
}