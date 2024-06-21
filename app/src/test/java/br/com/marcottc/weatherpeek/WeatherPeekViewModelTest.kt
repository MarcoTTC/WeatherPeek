package br.com.marcottc.weatherpeek

import android.content.SharedPreferences
import android.content.res.Resources
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.NetworkUtil
import br.com.marcottc.weatherpeek.util.PermissionUtil
import br.com.marcottc.weatherpeek.util.forceRefreshSettings
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import io.mockk.every
import io.mockk.mockk
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
    private val sharedPreferences: SharedPreferences = mockk<SharedPreferences>()
    private val resources: Resources = mockk<Resources>()

    private lateinit var viewModel: WeatherDataViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = WeatherDataViewModel(
            repository,
            oneCallService,
            locationManager,
            networkUtil,
            permissionUtil,
            sharedPreferences,
            resources
        )
    }

    @Test
    fun getWeatherData_noAppIdError() {
        fail()
    }
}