package br.com.marcottc.weatherpeek.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.marcottc.weatherpeek.model.dco.WeatherCache

@Database(entities = [WeatherCache::class], version = 1)
abstract class WeatherPeekDatabase : RoomDatabase() {
    abstract fun getWeatherCacheDao(): WeatherCacheDao
}