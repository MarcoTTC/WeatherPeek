package br.com.marcottc.weatherpeek.model.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache

@Database(
    entities = [CurrentWeatherCache::class, WeatherCache::class, HourlyWeatherCache::class, DailyWeatherCache::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        // One Call 4.0: alert ids on every cache, plus the daily visibility. Only new columns
        // with default values, so Room can generate the migration without an AutoMigrationSpec.
        AutoMigration(from = 4, to = 5)
    ]
)
@TypeConverters(WeatherPeekConverters::class)
abstract class WeatherPeekDatabase : RoomDatabase() {
    abstract fun getCurrentWeatherDao(): CurrentWeatherDao
    abstract fun getWeatherCacheDao(): WeatherCacheDao
    abstract fun getHourlyWeatherCacheDao(): HourlyWeatherCacheDao
    abstract fun getDailyWeatherCacheDao(): DailyWeatherCacheDao
}