package br.com.marcottc.weatherpeek.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache

@Database(
    entities = [CurrentWeatherCache::class, WeatherCache::class, HourlyWeatherCache::class],
    version = 1
)
abstract class WeatherPeekDatabase : RoomDatabase() {
    abstract fun getCurrentWeatherDao(): CurrentWeatherDao
    abstract fun getWeatherCacheDao(): WeatherCacheDao

    abstract fun getHourlyWeatherCacheDao(): HourlyWeatherCacheDao
}