package br.com.marcottc.weatherpeek

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import br.com.marcottc.weatherpeek.worker.factory.WeatherPeekWorkerFactory
import br.com.marcottc.weatherpeek.mock.MockGenerator
import br.com.marcottc.weatherpeek.model.ErrorResponse
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.LoggerUtil
import br.com.marcottc.weatherpeek.util.oneCallAppId
import br.com.marcottc.weatherpeek.worker.UpdateWeatherCache
import com.google.gson.Gson
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
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

        val weatherPeekWorkerFactory = WeatherPeekWorkerFactory(
            appKeyUtil,
            oneCallService,
            weatherPeekRepository,
            logger
        )

        updateWeatherCacheWorker = TestListenableWorkerBuilder<UpdateWeatherCache>(context)
            .setWorkerFactory(weatherPeekWorkerFactory)
            .build()
    }

    @Test
    fun doWork_noAppId_fails() = runTest {
        every { appKeyUtil.isEmpty() } returns true
        val result = updateWeatherCacheWorker.doWork()

        assertTrue(result is ListenableWorker.Result.Failure)
        verify { appKeyUtil.isEmpty() }
    }

    @Test
    fun doWork_noLocationStored_fails() = runTest {
        every { appKeyUtil.isEmpty() } returns false
        coEvery { weatherPeekRepository.getLastLatitudeAndLongitude() } returns null
        val result = updateWeatherCacheWorker.doWork()

        assertTrue(result is ListenableWorker.Result.Failure)
        verify { appKeyUtil.isEmpty() }
        coVerify { weatherPeekRepository.getLastLatitudeAndLongitude() }
    }

    @Test
    fun doWork_networkCall_showsSuccess() = runTest {
        every { appKeyUtil.isEmpty() } returns false
        coEvery { weatherPeekRepository.getLastLatitudeAndLongitude() } returns MockGenerator.generateLatitudeAndLongitude()
        every {appKeyUtil.getAppKey() } returns oneCallAppId
        coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
            Response.success(MockGenerator.generateOneCallWeatherData())
        }
        coEvery { weatherPeekRepository.updateRepository(any()) } just Runs

        val result = updateWeatherCacheWorker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)
        verify { appKeyUtil.isEmpty() }
        coVerify { weatherPeekRepository.getLastLatitudeAndLongitude() }
        verify {appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
        coVerify { weatherPeekRepository.updateRepository(any()) }
    }

    @Test
    fun doWork_networkCall_notFound_showsFailure() = runTest {
        every { appKeyUtil.isEmpty() } returns false
        coEvery { weatherPeekRepository.getLastLatitudeAndLongitude() } returns MockGenerator.generateLatitudeAndLongitude()
        every {appKeyUtil.getAppKey() } returns oneCallAppId
        coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
            val gson = Gson()
            val errorResponse = ErrorResponse(404, "Internal error")
            val bodyContent = gson.toJson(errorResponse)
            Response.error(404, ResponseBody.create(MediaType.get("application/json; charset=utf-8"), bodyContent))
        }

        val result = updateWeatherCacheWorker.doWork()

        assertTrue(result is ListenableWorker.Result.Failure)
        verify { appKeyUtil.isEmpty() }
        coVerify { weatherPeekRepository.getLastLatitudeAndLongitude() }
        verify {appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
    }

    @Test
    fun doWork_networkCall_throwsIllegalStateException() = runTest {
        every { appKeyUtil.isEmpty() } returns false
        coEvery { weatherPeekRepository.getLastLatitudeAndLongitude() } returns MockGenerator.generateLatitudeAndLongitude()
        every {appKeyUtil.getAppKey() } returns oneCallAppId
        coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
            Response.error(404, ResponseBody.create(MediaType.get("text/plain"), ""))
        }

        val result = updateWeatherCacheWorker.doWork()

        assertTrue(result is ListenableWorker.Result.Failure)
        verify { appKeyUtil.isEmpty() }
        coVerify { weatherPeekRepository.getLastLatitudeAndLongitude() }
        verify {appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
    }
}