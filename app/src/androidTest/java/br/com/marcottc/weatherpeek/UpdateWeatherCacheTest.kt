package br.com.marcottc.weatherpeek

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import br.com.marcottc.weatherpeek.factory.TestInjectionWorkerFactory
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.LoggerUtil
import br.com.marcottc.weatherpeek.worker.UpdateWeatherCache
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class UpdateWeatherCacheTest {

    private lateinit var context: Context
    private lateinit var executor: Executor

    private val appKeyUtil: AppKeyUtil = mockk<AppKeyUtil>()
    private val oneCallService: OneCallService = mockk<OneCallService>()

    private val weatherPeekRepository: WeatherPeekRepository = mockk<WeatherPeekRepository>()

    private val logger: LoggerUtil = mockk<LoggerUtil>()

    private lateinit var updateWeatherCacheWorker: UpdateWeatherCache

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        executor = Executors.newSingleThreadExecutor()

        val testInjectionWorkerFactory = TestInjectionWorkerFactory(
            appKeyUtil,
            oneCallService,
            weatherPeekRepository,
            logger
        )

        updateWeatherCacheWorker = TestListenableWorkerBuilder<UpdateWeatherCache>(context)
            .setWorkerFactory(testInjectionWorkerFactory)
            .build()
    }

    @Test
    fun updateWeatherCache_noAppId_fails() {
        runTest {
            every { appKeyUtil.isEmpty() } returns true
            val result = updateWeatherCacheWorker.doWork()

            assertTrue(result is ListenableWorker.Result.Failure)
            verify { appKeyUtil.isEmpty() }
        }
    }

    @Test
    fun updateWeatherCache_noLocationStored_fails() {
        runTest {
            every { appKeyUtil.isEmpty() } returns false
            coEvery { weatherPeekRepository.getLastLatitudeAndLongitude() } returns null
            val result = updateWeatherCacheWorker.doWork()

            assertTrue(result is ListenableWorker.Result.Failure)
            verify { appKeyUtil.isEmpty() }
            coVerify { weatherPeekRepository.getLastLatitudeAndLongitude() }
        }
    }
}