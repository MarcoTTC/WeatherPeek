package br.com.marcottc.weatherpeek.model.room

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.marcottc.weatherpeek.model.dco.WeatherCache

@Dao
interface WeatherCacheDao {

    @Query("SELECT * FROM WeatherCache ORDER BY id ASC")
    fun getAll(): LiveData<List<WeatherCache>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weather: WeatherCache)

    @Query("DELETE FROM WeatherCache")
    suspend fun clear()
}