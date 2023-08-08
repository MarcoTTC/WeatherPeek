package br.com.marcottc.weatherpeek.model.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache

@Database(
    entities = [CurrentWeatherCache::class, WeatherCache::class, HourlyWeatherCache::class, DailyWeatherCache::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class WeatherPeekDatabase : RoomDatabase() {
    abstract fun getCurrentWeatherDao(): CurrentWeatherDao
    abstract fun getWeatherCacheDao(): WeatherCacheDao
    abstract fun getHourlyWeatherCacheDao(): HourlyWeatherCacheDao
    abstract fun getDailyWeatherCacheDao(): DailyWeatherCacheDao
}