package br.com.marcottc.weatherpeek.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache

@Dao
interface DailyWeatherCacheDao {
    @Query("SELECT * FROM DailyWeatherCache ORDER BY dt ASC")
    suspend fun getAll(): List<DailyWeatherCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weather: DailyWeatherCache)

    @Query("DELETE FROM DailyWeatherCache")
    suspend fun clear()
}