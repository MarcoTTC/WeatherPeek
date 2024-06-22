package br.com.marcottc.weatherpeek

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.marcottc.weatherpeek.mock.MockGenerator
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.room.CurrentWeatherDao
import br.com.marcottc.weatherpeek.model.room.DailyWeatherCacheDao
import br.com.marcottc.weatherpeek.model.room.HourlyWeatherCacheDao
import br.com.marcottc.weatherpeek.model.room.WeatherCacheDao
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherPeekRepositoryTest {

    private val weatherCacheDao: WeatherCacheDao = mockk()
    private val currentWeatherDao: CurrentWeatherDao = mockk()
    private val hourlyWeatherCacheDao: HourlyWeatherCacheDao = mockk()
    private val dailyWeatherCacheDao: DailyWeatherCacheDao = mockk()

    private lateinit var repository: WeatherPeekRepository

    @get:Rule
    var instantRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = WeatherPeekRepository(
            weatherCacheDao,
            currentWeatherDao,
            hourlyWeatherCacheDao,
            dailyWeatherCacheDao
        )
    }

    @Test
    fun updateRepository_insertsDataCorrectly() {
        runBlocking {
            coEvery { currentWeatherDao.clear() } returns Unit
            coEvery { currentWeatherDao.insert(any()) } returns Unit
            coEvery { weatherCacheDao.clear() } returns Unit
            coEvery { weatherCacheDao.insertAll(*anyVararg()) } just Runs
            coEvery { hourlyWeatherCacheDao.clear() } returns Unit
            coEvery { hourlyWeatherCacheDao.insertAll(*anyVararg()) } just Runs
            coEvery { dailyWeatherCacheDao.clear() } returns Unit
            coEvery { dailyWeatherCacheDao.insertAll(*anyVararg()) } just Runs

            repository.updateRepository(MockGenerator.generateOneCallWeatherData())
        }

        coVerify { currentWeatherDao.clear() }
        coVerify { currentWeatherDao.insert(any()) }
        coVerify { weatherCacheDao.clear() }
        coVerify { weatherCacheDao.insertAll(*anyVararg())}
        coVerify { hourlyWeatherCacheDao.clear() }
        coVerify { hourlyWeatherCacheDao.insertAll(*anyVararg())}
        coVerify { dailyWeatherCacheDao.clear() }
        coVerify { dailyWeatherCacheDao.insertAll(*anyVararg())}
    }

    @Test
    fun databaseRefreshRequired_noCachedData() {
        var isDatabaseRefreshRequired = false
        runBlocking {
            val currentWeather: CurrentWeatherCache? = null
            val hourlyWeatherList: List<HourlyWeatherCache> = emptyList()
            coEvery { currentWeatherDao.getValues() } returns currentWeather
            coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherList

            isDatabaseRefreshRequired = repository
                .databaseRefreshRequired(37.422, -122.084)
        }

        assertTrue(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }

    @Test
    fun databaseRefreshRequired_cachedDataLocationChanged() {
        var isDatabaseRefreshRequired = false
        runBlocking {
            val currentWeather = br.com.marcottc.weatherpeek.mock.MockGenerator.generateCurrentWeatherCache()
            val hourlyWeatherList = br.com.marcottc.weatherpeek.mock.MockGenerator.generateHourlyWeatherCacheList()
            coEvery { currentWeatherDao.getValues() } returns currentWeather
            coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherList

            isDatabaseRefreshRequired = repository
                .databaseRefreshRequired(66.581, -94.154)
        }

        assertTrue(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }

    @Test
    fun databaseRefreshRequired_cachedDataLocationUnchangedForecastNotExpired() {
        var isDatabaseRefreshRequired = true
        val hourlyWeatherCacheList = br.com.marcottc.weatherpeek.mock.MockGenerator.generateHourlyWeatherCacheList()
        hourlyWeatherCacheList[0].dt = System.currentTimeMillis() / 1000
        runBlocking {
            val currentWeather = br.com.marcottc.weatherpeek.mock.MockGenerator.generateCurrentWeatherCache()
            coEvery { currentWeatherDao.getValues() } returns currentWeather
            coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherCacheList

            isDatabaseRefreshRequired = repository
                .databaseRefreshRequired(37.422, -122.084)
        }

        assertFalse(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }

    @Test
    fun databaseRefreshRequired_cachedDataLocationUnchangedForecastExpired() {
        var isDatabaseRefreshRequired = false
        val hourlyWeatherCacheList = br.com.marcottc.weatherpeek.mock.MockGenerator.generateHourlyWeatherCacheList()
        hourlyWeatherCacheList[0].dt = (System.currentTimeMillis() / 1000) - 3601
        runBlocking {
            val currentWeather = br.com.marcottc.weatherpeek.mock.MockGenerator.generateCurrentWeatherCache()
            val hourlyWeatherListLiveData = MutableLiveData(hourlyWeatherCacheList)
            coEvery { currentWeatherDao.getValues() } returns currentWeather
            coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherCacheList

            hourlyWeatherListLiveData.observeForever {  }

            isDatabaseRefreshRequired = repository
                .databaseRefreshRequired(37.422, -122.084)

            hourlyWeatherListLiveData.removeObserver {  }
        }

        assertTrue(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }
}