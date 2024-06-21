package br.com.marcottc.weatherpeek

import android.content.SharedPreferences
import android.content.res.Resources
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.NetworkUtil
import br.com.marcottc.weatherpeek.util.PermissionUtil
import br.com.marcottc.weatherpeek.viewmodel.WeatherPeekViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
    private val sharedPreferences: SharedPreferences = mockk<SharedPreferences>()
    private val resources: Resources = mockk<Resources>()

    private lateinit var viewModel: WeatherPeekViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = WeatherPeekViewModel(
            repository,
            oneCallService,
            locationManager,
            networkUtil,
            permissionUtil,
            appKeyUtil,
            sharedPreferences,
            resources
        )
    }

    @Test
    fun weatherPeekViewModel_isInitialized() {
        assertEquals(viewModel.viewModelState.value, WeatherPeekViewModel.State.LOADING)
        assertEquals(viewModel.mustRequestPermissionFirst.value, false)
        assertEquals(viewModel.requestingWeatherData.value, false)
        assertEquals(viewModel.showMessage.value, "")
    }

    @Test
    fun getWeatherData_noAppId_showsErrorMessage() {
        runBlocking {
            coEvery { appKeyUtil.isEmpty() } returns true
            viewModel.getWeatherData()
        }
        assertEquals(viewModel.showMessage.value, "Please set up an app id before building the project!")
        assertEquals(viewModel.requestingWeatherData.value, false)
        assertEquals(viewModel.viewModelState.value, WeatherPeekViewModel.State.FAILED)
        coVerify { appKeyUtil.isEmpty() }
    }

    @Test
    fun getWeatherData_noInternetConnectivity_showsErrorMessage() {
        runBlocking {
            coEvery { appKeyUtil.isEmpty() } returns false
            coEvery { networkUtil.hasConnectivity() } returns false
            coEvery { resources.getString(R.string.no_internet_connectivity) } returns "No internet connectivity!"
            viewModel.getWeatherData()
        }
        assertEquals(viewModel.showMessage.value, "No internet connectivity!")
        assertEquals(viewModel.requestingWeatherData.value, false)
        assertEquals(viewModel.viewModelState.value, WeatherPeekViewModel.State.FAILED)
        coVerify { appKeyUtil.isEmpty() }
        coVerify { networkUtil.hasConnectivity() }
        coVerify { resources.getString(R.string.no_internet_connectivity) }
    }

    @Test
    fun getWeatherData_noLocationPermission_showsErrorMessage() {
        runBlocking {
            coEvery { appKeyUtil.isEmpty() } returns false
            coEvery { networkUtil.hasConnectivity() } returns true
            coEvery { permissionUtil.hasLocationPermission() } returns false
            viewModel.getWeatherData()
        }
        assertEquals(viewModel.mustRequestPermissionFirst.value, true)
        assertEquals(viewModel.requestingWeatherData.value, false)
        assertEquals(viewModel.viewModelState.value, WeatherPeekViewModel.State.FAILED)
        coVerify { appKeyUtil.isEmpty() }
        coVerify { networkUtil.hasConnectivity() }
        coVerify { permissionUtil.hasLocationPermission() }
    }
}