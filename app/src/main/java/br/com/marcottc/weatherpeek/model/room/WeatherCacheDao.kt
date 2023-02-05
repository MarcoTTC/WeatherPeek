package br.com.marcottc.weatherpeek.model.room

import androidx.room.*
import br.com.marcottc.weatherpeek.model.dco.WeatherCache

@Dao
interface WeatherCacheDao {

    @Query("SELECT * FROM WeatherCache")
    suspend fun getAll(): List<WeatherCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weather: WeatherCache)

    @Query("DELETE FROM WeatherCache")
    suspend fun clear()
}