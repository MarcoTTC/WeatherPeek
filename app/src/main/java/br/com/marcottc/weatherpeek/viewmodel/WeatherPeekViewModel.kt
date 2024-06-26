package br.com.marcottc.weatherpeek.viewmodel

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.*
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.model.ErrorResponse
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.repository.WeatherPeekRepository
import br.com.marcottc.weatherpeek.util.AppKeyUtil
import br.com.marcottc.weatherpeek.util.LoggerUtil
import br.com.marcottc.weatherpeek.util.NetworkUtil
import br.com.marcottc.weatherpeek.util.PermissionUtil
import br.com.marcottc.weatherpeek.util.forceRefreshSettings
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly

class WeatherPeekViewModel(
    private val weatherPeekRepository: WeatherPeekRepository,
    private val oneCallService: OneCallService,
    private val locationManager: LocationManager,
    private val networkUtil: NetworkUtil,
    private val permissionUtil: PermissionUtil,
    private val appKeyUtil: AppKeyUtil,
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val logger: LoggerUtil,
) : ViewModel() {

    enum class State {
        LOADING,
        SUCCESS,
        FAILED
    }

    private val _viewModelState: MutableLiveData<State> = MutableLiveData()
    val viewModelState: LiveData<State>
        get() = _viewModelState

    private val _mustRequestPermissionFirst: MutableLiveData<Boolean> = MutableLiveData()
    val mustRequestPermissionFirst: LiveData<Boolean>
        get() = _mustRequestPermissionFirst

    private val _requestingWeatherData: MutableLiveData<Boolean> = MutableLiveData()
    val requestingWeatherData: LiveData<Boolean>
        get() = _requestingWeatherData

    val currentWeatherCache: LiveData<CurrentWeatherCache> = liveData {
        emitSource(weatherPeekRepository.currentWeatherCache)
    }

    val weatherListCache: LiveData<List<WeatherCache>> = liveData {
        emitSource(weatherPeekRepository.weatherListCache)
    }

    val hourlyWeatherListCache: LiveData<List<HourlyWeatherCache>> = liveData {
        emitSource(weatherPeekRepository.hourlyWeatherListCache)
    }

    val dailyWeatherListCache: LiveData<List<DailyWeatherCache>> = liveData {
        emitSource(weatherPeekRepository.dailyWeatherListCache)
    }

    private val _showMessage: MutableLiveData<String> = MutableLiveData()
    val showMessage: LiveData<String>
        get() = _showMessage

    private val mLocationListener = object : LocationListener {
        private var previousAccuracy: Float = 1000000.0F
        private var countdown: Int = 3

        override fun onLocationChanged(location: Location) {
            val currentAccuracy = location.accuracy
            if (currentAccuracy < previousAccuracy && countdown > 0) {
                previousAccuracy = currentAccuracy
                countdown--
            } else {
                previousAccuracy = 1000000.0F
                countdown = 3
                recoveringWeatherData(location.latitude, location.longitude)
            }
        }
    }

    private var retrievingWeatherDataJob: Job? = null

    init {
        _viewModelState.value = State.LOADING
        _mustRequestPermissionFirst.value = false
        _requestingWeatherData.value = false
        _showMessage.value = ""
    }

    fun setForceRefreshOption(value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(forceRefreshSettings, value)
        editor.apply()
    }

    @SuppressLint("MissingPermission")
    fun getWeatherData() {
        if (_requestingWeatherData.value == true) {
            _showMessage.value = resources.getString(R.string.please_wait)
        } else {
            if (appKeyUtil.isEmpty()) {
                _showMessage.value = resources.getString(R.string.no_valid_app_id)
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
                return
            }

            if (!networkUtil.hasConnectivity()) {
                _showMessage.value =
                    resources.getString(R.string.no_internet_connectivity)
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
                return
            }

            _mustRequestPermissionFirst.value = false
            if (permissionUtil.hasLocationPermission()) {
                _requestingWeatherData.value = true
                _viewModelState.value = State.LOADING

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        0.0F,
                        mLocationListener
                    )
                } else {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000,
                        0.0F,
                        mLocationListener
                    )
                }
            } else {
                _mustRequestPermissionFirst.value = true
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
            }
        }
    }

    private fun recoveringWeatherData(latitude: Double, longitude: Double) {
        locationManager.removeUpdates(mLocationListener)

        retrievingWeatherDataJob = viewModelScope.launch(Dispatchers.IO) {
            val isForceRefresh = sharedPreferences.getBoolean(forceRefreshSettings, false)

            if (isForceRefresh ||
                weatherPeekRepository.databaseRefreshRequired(latitude, longitude)
            ) {
                try {
                    val response =
                        oneCallService.getWeatherData(
                            lat = latitude,
                            lon = longitude,
                            appid = appKeyUtil.getAppKey()
                        )
                    var newState: State
                    var errorMessage: String? = null
                    if (response.isSuccessful) {
                        val availableWeatherData = response.body()
                        if (availableWeatherData != null) {
                            weatherPeekRepository.updateRepository(availableWeatherData)

                            newState = State.SUCCESS
                        } else {
                            newState = State.FAILED
                        }
                    } else {
                        val gson = Gson()
                        val errorResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        if (errorResponse != null) {
                            errorMessage = errorResponse.message
                        }
                        newState = State.FAILED
                    }

                    withContext(mainDispatcher) {
                        _viewModelState.value = newState
                        _requestingWeatherData.value = false
                        if (errorMessage != null) {
                            _showMessage.value = errorMessage!!
                        }
                    }
                } catch (exception: Exception) {
                    when (exception) {
                        is JsonSyntaxException,
                        is JsonIOException -> {
                            logger.e(
                                WeatherPeekViewModel::class.java.simpleName,
                                exception.message ?: "",
                                exception
                            )
                            withContext(mainDispatcher) {
                                _requestingWeatherData.value = false
                                _viewModelState.value = State.FAILED
                            }
                        }

                        else -> throw exception
                    }
                }
            } else {
                withContext(mainDispatcher) {
                    _showMessage.value = resources.getString(R.string.retrieved_cache)
                    _requestingWeatherData.value = false
                    _viewModelState.value = State.SUCCESS
                }
            }
        }
    }

    @TestOnly
    suspend fun suspendUntilWeatherDataIsRetrieved() {
        retrievingWeatherDataJob?.join()
    }

    override fun onCleared() {
        super.onCleared()

        retrievingWeatherDataJob?.cancel()
        locationManager.removeUpdates(mLocationListener)
    }
}