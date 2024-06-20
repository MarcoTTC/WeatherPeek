package br.com.marcottc.weatherpeek.repository

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

class WeatherPeekRepository(
    private val weatherCacheDao: WeatherCacheDao,
    private val currentWeatherCacheDao: CurrentWeatherDao,
    private val hourlyWeatherCacheDao: HourlyWeatherCacheDao,
    private val dailyWeatherCacheDao: DailyWeatherCacheDao
) {

    val currentWeatherCache: LiveData<CurrentWeatherCache> = liveData {
        emitSource(currentWeatherCacheDao.getLiveData())
    }

    val weatherListCache: LiveData<List<WeatherCache>> = liveData {
        emitSource(weatherCacheDao.getAll())
    }

    val hourlyWeatherListCache: LiveData<List<HourlyWeatherCache>> = liveData {
        emitSource(hourlyWeatherCacheDao.getAllLiveData())
    }

    val dailyWeatherListCache: LiveData<List<DailyWeatherCache>> = liveData {
        emitSource(dailyWeatherCacheDao.getAllLiveData())
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

    suspend fun databaseRefreshRequired(currentLat: Double, currentLon: Double): Boolean {
        val currentWeather = currentWeatherCacheDao.getValues()
        val hourlyWeatherList = hourlyWeatherCacheDao.getAll()
        return if (
            currentWeather == null ||
            hourlyWeatherList.isNullOrEmpty()
        ) {
            true
        } else {
            if (abs(currentWeather.latitude - currentLat) >= 1 ||
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