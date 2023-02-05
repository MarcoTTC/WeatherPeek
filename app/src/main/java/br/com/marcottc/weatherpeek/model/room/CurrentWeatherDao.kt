package br.com.marcottc.weatherpeek.model.room

import androidx.room.*
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache

@Dao
interface CurrentWeatherDao {
    @Query("SELECT * FROM CurrentWeatherCache ORDER BY dt ASC")
    suspend fun get(): CurrentWeatherCache

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: CurrentWeatherCache)

    @Query("DELETE FROM CurrentWeatherCache")
    suspend fun clear()
}