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
import kotlinx.coroutines.test.runTest
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
    fun updateRepository_insertsDataCorrectly() = runTest {
        coEvery { currentWeatherDao.clear() } returns Unit
        coEvery { currentWeatherDao.insert(any()) } returns Unit
        coEvery { weatherCacheDao.clear() } returns Unit
        coEvery { weatherCacheDao.insertAll(*anyVararg()) } just Runs
        coEvery { hourlyWeatherCacheDao.clear() } returns Unit
        coEvery { hourlyWeatherCacheDao.insertAll(*anyVararg()) } just Runs
        coEvery { dailyWeatherCacheDao.clear() } returns Unit
        coEvery { dailyWeatherCacheDao.insertAll(*anyVararg()) } just Runs

        repository.updateRepository(MockGenerator.generateOneCallWeatherData())

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
    fun databaseRefreshRequired_noCachedData() = runTest {
        val currentWeather: CurrentWeatherCache? = null
        val hourlyWeatherList: List<HourlyWeatherCache> = emptyList()

        coEvery { currentWeatherDao.getValues() } returns currentWeather
        coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherList

        val isDatabaseRefreshRequired: Boolean = repository
            .databaseRefreshRequired(37.422, -122.084)

        assertTrue(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }

    @Test
    fun databaseRefreshRequired_cachedDataLocationChanged() = runTest {
        val currentWeather = MockGenerator.generateCurrentWeatherCache()
        val hourlyWeatherList = MockGenerator.generateHourlyWeatherCacheList()

        coEvery { currentWeatherDao.getValues() } returns currentWeather
        coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherList

        val isDatabaseRefreshRequired: Boolean = repository
            .databaseRefreshRequired(66.581, -94.154)

        assertTrue(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }

    @Test
    fun databaseRefreshRequired_cachedDataLocationUnchangedForecastNotExpired() = runTest {
        val hourlyWeatherCacheList = MockGenerator.generateHourlyWeatherCacheList()
        hourlyWeatherCacheList[0].dt = System.currentTimeMillis() / 1000
        val currentWeather = MockGenerator.generateCurrentWeatherCache()

        coEvery { currentWeatherDao.getValues() } returns currentWeather
        coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherCacheList

        val isDatabaseRefreshRequired: Boolean = repository
            .databaseRefreshRequired(37.422, -122.084)

        assertFalse(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }
    }

    @Test
    fun databaseRefreshRequired_cachedDataLocationUnchangedForecastExpired() = runTest {
        val hourlyWeatherCacheList = MockGenerator.generateHourlyWeatherCacheList()
        hourlyWeatherCacheList[0].dt = (System.currentTimeMillis() / 1000) - 3601
        val currentWeather = MockGenerator.generateCurrentWeatherCache()
        val hourlyWeatherListLiveData = MutableLiveData(hourlyWeatherCacheList)

        coEvery { currentWeatherDao.getValues() } returns currentWeather
        coEvery { hourlyWeatherCacheDao.getAll() } returns hourlyWeatherCacheList

        hourlyWeatherListLiveData.observeForever {  }

        val isDatabaseRefreshRequired: Boolean = repository
            .databaseRefreshRequired(37.422, -122.084)

        assertTrue(isDatabaseRefreshRequired)
        coVerify { currentWeatherDao.getValues() }
        coVerify { hourlyWeatherCacheDao.getAll() }

        hourlyWeatherListLiveData.removeObserver {  }
    }

    @Test
    fun getLastLatitudeAndLongitude_noCachedData() = runTest {
        coEvery { currentWeatherDao.getValues() } returns null

        val locationValue: Pair<Double, Double>? = repository.getLastLatitudeAndLongitude()

        assertNull(locationValue)
        coVerify { currentWeatherDao.getValues() }
    }

    @Test
    fun getLastLatitudeAndLongitude_locationCachedData() = runTest {
        val currentWeather = MockGenerator.generateCurrentWeatherCache()
        coEvery { currentWeatherDao.getValues() } returns currentWeather

        val locationValue: Pair<Double, Double>? = repository.getLastLatitudeAndLongitude()

        assertEquals(37.422, locationValue?.first)
        assertEquals(-122.084, locationValue?.second)
        coVerify { currentWeatherDao.getValues() }
    }
}