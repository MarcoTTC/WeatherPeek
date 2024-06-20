package br.com.marcottc.weatherpeek.model.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache

@Dao
interface HourlyWeatherCacheDao {

    @Query("SELECT * FROM HourlyWeatherCache ORDER BY dt ASC")
    fun getAllLiveData(): LiveData<List<HourlyWeatherCache>>

    @Query("SELECT * FROM HourlyWeatherCache ORDER BY dt ASC")
    suspend fun getAll(): List<HourlyWeatherCache>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weather: HourlyWeatherCache)

    @Query("DELETE FROM HourlyWeatherCache")
    suspend fun clear()
}