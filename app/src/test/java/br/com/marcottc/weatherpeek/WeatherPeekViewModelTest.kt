package br.com.marcottc.weatherpeek

import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.marcottc.weatherpeek.mock.MockGenerator
import br.com.marcottc.weatherpeek.model.ErrorResponse
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.LoggerUtil
import br.com.marcottc.weatherpeek.util.NetworkUtil
import br.com.marcottc.weatherpeek.util.PermissionUtil
import br.com.marcottc.weatherpeek.util.forceRefreshSettings
import br.com.marcottc.weatherpeek.util.oneCallAppId
import br.com.marcottc.weatherpeek.viewmodel.WeatherPeekViewModel
import com.google.gson.Gson
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Response
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherPeekViewModelTest {

    private val repository: WeatherPeekRepository = mockk<WeatherPeekRepository>()
    private val oneCallService: OneCallService = mockk<OneCallService>()
    private val locationManager: LocationManager = mockk<LocationManager>()
    private val networkUtil: NetworkUtil = mockk<NetworkUtil>()
    private val permissionUtil: PermissionUtil = mockk<PermissionUtil>()
    private val appKeyUtil: AppKeyUtil = mockk<AppKeyUtil>()
    private val logger: LoggerUtil = mockk<LoggerUtil>()
    private val sharedPreferences: SharedPreferences = mockk<SharedPreferences>()
    private val resources: Resources = mockk<Resources>()

    private lateinit var viewModel: WeatherPeekViewModel

    private val locationListenerSlot = slot<LocationListener>()
    private val messageSlot = slot<String>()
    private val location = mockk<Location>()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val mainTestDispatcher = UnconfinedTestDispatcher()
        viewModel = WeatherPeekViewModel(
            repository,
            oneCallService,
            locationManager,
            networkUtil,
            permissionUtil,
            appKeyUtil,
            sharedPreferences,
            resources,
            mainDispatcher = mainTestDispatcher,
            logger
        )
    }

    @Test
    fun weatherPeekViewModel_isInitialized() {
        assertEquals(WeatherPeekViewModel.State.LOADING, viewModel.viewModelState.value)
        assertEquals(false, viewModel.mustRequestPermissionFirst.value)
        assertEquals(false, viewModel.requestingWeatherData.value)
        assertEquals("", viewModel.showMessage.value)
    }

    @Test
    fun getWeatherData_noAppId_showsErrorMessage() {
        runTest {
            every { appKeyUtil.isEmpty() } returns true
            viewModel.getWeatherData()
        }
        assertEquals("Please set up an app id before building the project!", viewModel.showMessage.value)
        assertEquals(false, viewModel.requestingWeatherData.value)
        assertEquals(WeatherPeekViewModel.State.FAILED, viewModel.viewModelState.value)
        verify { appKeyUtil.isEmpty() }
    }

    @Test
    fun getWeatherData_noInternetConnectivity_showsErrorMessage() {
        runTest {
            every { appKeyUtil.isEmpty() } returns false
            every { networkUtil.hasConnectivity() } returns false
            every { resources.getString(R.string.no_internet_connectivity) } returns "No internet connectivity!"
            viewModel.getWeatherData()
        }
        assertEquals("No internet connectivity!", viewModel.showMessage.value)
        assertEquals(false, viewModel.requestingWeatherData.value)
        assertEquals(WeatherPeekViewModel.State.FAILED, viewModel.viewModelState.value)
        verify { appKeyUtil.isEmpty() }
        verify { networkUtil.hasConnectivity() }
        verify { resources.getString(R.string.no_internet_connectivity) }
    }

    @Test
    fun getWeatherData_noLocationPermission_showsErrorMessage() {
        runTest {
            every { appKeyUtil.isEmpty() } returns false
            every { networkUtil.hasConnectivity() } returns true
            every { permissionUtil.hasLocationPermission() } returns false
            viewModel.getWeatherData()
        }
        assertEquals(true, viewModel.mustRequestPermissionFirst.value)
        assertEquals(false, viewModel.requestingWeatherData.value)
        assertEquals(WeatherPeekViewModel.State.FAILED, viewModel.viewModelState.value)
        verify { appKeyUtil.isEmpty() }
        verify { networkUtil.hasConnectivity() }
        verify { permissionUtil.hasLocationPermission() }
    }

    @Test
    fun getWeatherData_networkCall_showsSuccess() {
        runTest {
            testScheduler.runCurrent()
            viewModel.viewModelState.observeForever {  }
            viewModel.requestingWeatherData.observeForever {  }

            every { appKeyUtil.isEmpty() } returns false
            every { networkUtil.hasConnectivity() } returns true
            every { permissionUtil.hasLocationPermission() } returns true
            every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
            every { location.accuracy } returns 1000000.0F
            every { location.latitude } returns 37.422
            every { location.longitude } returns -122.084
            every { locationManager.requestLocationUpdates(
                any(),
                1000,
                0.0F,
                capture(locationListenerSlot)
            ) } answers {
                locationListenerSlot.captured.onLocationChanged(location)
            }
            every { locationManager.removeUpdates(any<LocationListener>()) } just Runs
            every { sharedPreferences.getBoolean(forceRefreshSettings, any()) } returns false
            coEvery { repository.databaseRefreshRequired(any(), any()) } returns true
            every { appKeyUtil.getAppKey() } returns oneCallAppId
            coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
                Response.success(MockGenerator.generateOneCallWeatherData())
            }
            coEvery { repository.updateRepository(any()) } just Runs

            viewModel.getWeatherData()
            viewModel.suspendUntilWeatherDataIsRetrieved()
        }

        viewModel.viewModelState.removeObserver {  }
        viewModel.requestingWeatherData.removeObserver {  }

        assertEquals(WeatherPeekViewModel.State.SUCCESS, viewModel.viewModelState.value)
        assertEquals(false, viewModel.requestingWeatherData.value)

        verify { appKeyUtil.isEmpty() }
        verify { networkUtil.hasConnectivity() }
        verify { permissionUtil.hasLocationPermission() }
        verify { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) }
        verify { locationManager.requestLocationUpdates(
            any(),
            1000,
            0.0F,
            locationListenerSlot.captured
        ) }
        verify { locationManager.removeUpdates(any<LocationListener>()) }
        verify { sharedPreferences.getBoolean(forceRefreshSettings, any()) }
        coVerify { repository.databaseRefreshRequired(any(), any()) }
        verify { appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
        coVerify { repository.updateRepository(any()) }
    }

    @Test
    fun getWeatherData_networkCall_showsFailureNotFound() {
        runTest {
            testScheduler.runCurrent()
            viewModel.viewModelState.observeForever {  }
            viewModel.requestingWeatherData.observeForever {  }

            every { appKeyUtil.isEmpty() } returns false
            every { networkUtil.hasConnectivity() } returns true
            every { permissionUtil.hasLocationPermission() } returns true
            every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
            every { location.accuracy } returns 1000000.0F
            every { location.latitude } returns 37.422
            every { location.longitude } returns -122.084
            every { locationManager.requestLocationUpdates(
                any(),
                1000,
                0.0F,
                capture(locationListenerSlot)
            ) } answers {
                locationListenerSlot.captured.onLocationChanged(location)
            }
            every { locationManager.removeUpdates(any<LocationListener>()) } just Runs
            every { sharedPreferences.getBoolean(forceRefreshSettings, any()) } returns false
            coEvery { repository.databaseRefreshRequired(any(), any()) } returns true
            every { appKeyUtil.getAppKey() } returns oneCallAppId
            coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
                val gson = Gson()
                val errorResponse = ErrorResponse(404, "Internal error")
                val bodyContent = gson.toJson(errorResponse)
                Response.error(404, ResponseBody.create(MediaType.get("application/json; charset=utf-8"), bodyContent))
            }

            viewModel.getWeatherData()
            viewModel.suspendUntilWeatherDataIsRetrieved()
        }

        viewModel.viewModelState.removeObserver {  }
        viewModel.requestingWeatherData.removeObserver {  }

        assertEquals(WeatherPeekViewModel.State.FAILED, viewModel.viewModelState.value)
        assertEquals(false, viewModel.requestingWeatherData.value)
        assertEquals("Internal error", viewModel.showMessage.value)

        verify { appKeyUtil.isEmpty() }
        verify { networkUtil.hasConnectivity() }
        verify { permissionUtil.hasLocationPermission() }
        verify { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) }
        verify { locationManager.requestLocationUpdates(
            any(),
            1000,
            0.0F,
            locationListenerSlot.captured
        ) }
        verify { locationManager.removeUpdates(any<LocationListener>()) }
        verify { sharedPreferences.getBoolean(forceRefreshSettings, any()) }
        coVerify { repository.databaseRefreshRequired(any(), any()) }
        verify { appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
    }

    @Test
    fun getWeatherData_networkCall_throwsIllegalStateException() {
        runTest {
            testScheduler.runCurrent()
            viewModel.viewModelState.observeForever {  }
            viewModel.requestingWeatherData.observeForever {  }

            every { appKeyUtil.isEmpty() } returns false
            every { networkUtil.hasConnectivity() } returns true
            every { permissionUtil.hasLocationPermission() } returns true
            every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
            every { location.accuracy } returns 1000000.0F
            every { location.latitude } returns 37.422
            every { location.longitude } returns -122.084
            every { locationManager.requestLocationUpdates(
                any(),
                1000,
                0.0F,
                capture(locationListenerSlot)
            ) } answers {
                locationListenerSlot.captured.onLocationChanged(location)
            }
            every { locationManager.removeUpdates(any<LocationListener>()) } just Runs
            every { sharedPreferences.getBoolean(forceRefreshSettings, any()) } returns false
            coEvery { repository.databaseRefreshRequired(any(), any()) } returns true
            every { appKeyUtil.getAppKey() } returns oneCallAppId
            coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
                Response.error(404, ResponseBody.create(MediaType.get("text/plain"), ""))
            }

            viewModel.getWeatherData()
            viewModel.suspendUntilWeatherDataIsRetrieved()
        }

        viewModel.viewModelState.removeObserver {  }
        viewModel.requestingWeatherData.removeObserver {  }

        assertEquals(WeatherPeekViewModel.State.FAILED, viewModel.viewModelState.value)
        assertEquals(false, viewModel.requestingWeatherData.value)

        verify { appKeyUtil.isEmpty() }
        verify { networkUtil.hasConnectivity() }
        verify { permissionUtil.hasLocationPermission() }
        verify { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) }
        verify { locationManager.requestLocationUpdates(
            any(),
            1000,
            0.0F,
            locationListenerSlot.captured
        ) }
        verify { locationManager.removeUpdates(any<LocationListener>()) }
        verify { sharedPreferences.getBoolean(forceRefreshSettings, any()) }
        coVerify { repository.databaseRefreshRequired(any(), any()) }
        verify { appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
    }

    @Test
    fun getWeatherData_networkCall_throwsJsonSyntaxException() {
        runTest {
            testScheduler.runCurrent()
            viewModel.viewModelState.observeForever {  }
            viewModel.requestingWeatherData.observeForever {  }

            every { appKeyUtil.isEmpty() } returns false
            every { networkUtil.hasConnectivity() } returns true
            every { permissionUtil.hasLocationPermission() } returns true
            every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
            every { location.accuracy } returns 1000000.0F
            every { location.latitude } returns 37.422
            every { location.longitude } returns -122.084
            every { locationManager.requestLocationUpdates(
                any(),
                1000,
                0.0F,
                capture(locationListenerSlot)
            ) } answers {
                locationListenerSlot.captured.onLocationChanged(location)
            }
            every { locationManager.removeUpdates(any<LocationListener>()) } just Runs
            every { sharedPreferences.getBoolean(forceRefreshSettings, any()) } returns false
            coEvery { repository.databaseRefreshRequired(any(), any()) } returns true
            every { appKeyUtil.getAppKey() } returns oneCallAppId
            coEvery { oneCallService.getWeatherData(any(), any(), any()) } answers {
                val gson = Gson()
                val errorResponse = ErrorResponse(404, "Internal error")
                val bodyContent = gson.toJson(errorResponse)
                Response.error(404, ResponseBody.create(MediaType.get("application/json; charset=utf-8"),
                    "$bodyContent {{}"
                ))
            }
            every { logger.e(
                capture<String>(messageSlot),
                any<Exception>()
            ) } answers {
                println(messageSlot.captured)
            }

            viewModel.getWeatherData()
            viewModel.suspendUntilWeatherDataIsRetrieved()
        }

        viewModel.viewModelState.removeObserver {  }
        viewModel.requestingWeatherData.removeObserver {  }

        assertEquals(WeatherPeekViewModel.State.FAILED, viewModel.viewModelState.value)
        assertEquals(false, viewModel.requestingWeatherData.value)

        verify { appKeyUtil.isEmpty() }
        verify { networkUtil.hasConnectivity() }
        verify { permissionUtil.hasLocationPermission() }
        verify { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) }
        verify { locationManager.requestLocationUpdates(
            any(),
            1000,
            0.0F,
            locationListenerSlot.captured
        ) }
        verify { locationManager.removeUpdates(any<LocationListener>()) }
        verify { sharedPreferences.getBoolean(forceRefreshSettings, any()) }
        coVerify { repository.databaseRefreshRequired(any(), any()) }
        verify { appKeyUtil.getAppKey() }
        coVerify { oneCallService.getWeatherData(any(), any(), any()) }
        verify { logger.e(
            capture<String>(messageSlot),
            any<Exception>()
        ) }
    }
}