package br.com.marcottc.weatherpeek.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.model.ErrorResponse
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.model.room.*
import br.com.marcottc.weatherpeek.network.RetrofitClientInstance
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.util.NetworkUtil
import br.com.marcottc.weatherpeek.util.oneCallAppId
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.stream.Collectors

class WeatherDataViewModel(private val weatherApplication: Application) :
    AndroidViewModel(weatherApplication) {

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

    private val _currentTimezoneType: MutableLiveData<String?> = MutableLiveData()
    val currentTimezoneType: LiveData<String?>
        get() = _currentTimezoneType

    val currentWeatherCache: LiveData<CurrentWeatherCache?> = liveData {
        val currentWeather = currentWeatherCacheDao.get()
        emit(currentWeather)
    }

    val weatherListCache: LiveData<List<WeatherCache>?> = liveData {
        val weather = weatherCacheDao.getAll()
        emit(weather)
    }

    val hourlyWeatherListCache: LiveData<List<HourlyWeatherCache>?> = liveData {
        val hourlyWeatherCache = hourlyWeatherCacheDao.getAll()
        emit(hourlyWeatherCache)
    }

    val dailyWeatherListCache: LiveData<List<DailyWeatherCache>?> = liveData {
        val dailyWeatherCache = dailyWeatherCacheDao.getAll()
        emit(dailyWeatherCache)
    }

    private val _showMessage: MutableLiveData<String> = MutableLiveData()
    val showMessage: LiveData<String>
        get() = _showMessage

    private var retrofitInstance: Retrofit
    private var oneCallService: OneCallService
    private var locationManager: LocationManager
    private var database: WeatherPeekDatabase
    private var weatherCacheDao: WeatherCacheDao
    private var currentWeatherCacheDao: CurrentWeatherDao
    private var hourlyWeatherCacheDao: HourlyWeatherCacheDao
    private var dailyWeatherCacheDao: DailyWeatherCacheDao

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

    init {
        _viewModelState.value = State.LOADING
        _mustRequestPermissionFirst.value = false
        _requestingWeatherData.value = false
        _showMessage.value = ""

        retrofitInstance = RetrofitClientInstance.getRetrofitInstance()
        oneCallService = retrofitInstance.create(OneCallService::class.java)
        locationManager =
            weatherApplication.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        database = RoomDatabaseInstance.getRoomInstance(weatherApplication)
        weatherCacheDao = database.getWeatherCacheDao()
        currentWeatherCacheDao = database.getCurrentWeatherDao()
        hourlyWeatherCacheDao = database.getHourlyWeatherCacheDao()
        dailyWeatherCacheDao = database.getDailyWeatherCacheDao()
    }

    fun getWeatherData() {
        if (_requestingWeatherData.value == true) {
            _showMessage.value = "Please wait a moment..."
        } else {
            if (oneCallAppId.isEmpty()) {
                _showMessage.value = "Please set up an app id before building the project!"
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
                return
            }
            if (!NetworkUtil.hasConnectivity(weatherApplication)) {
                _showMessage.value =
                    weatherApplication.resources.getString(R.string.no_internet_connectivity)
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
                return
            }

            _mustRequestPermissionFirst.value = false
            if (ActivityCompat.checkSelfPermission(
                    weatherApplication,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    weatherApplication,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
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

        viewModelScope.launch {
            try {
                val response = oneCallService.getWeatherData(lat = latitude, lon = longitude)
                if (response.isSuccessful) {
                    val availableWeatherData = response.body()
                    availableWeatherData?.let {
                        _currentTimezoneType.value = availableWeatherData.timezone
                        val currentWeather = CurrentWeatherCache(
                            availableWeatherData.current,
                            availableWeatherData.timezone
                        )
                        currentWeatherCacheDao.clear()
                        currentWeatherCacheDao.insert(currentWeather)

                        val weatherList =
                            availableWeatherData.current.weatherList.stream().map { data ->
                                WeatherCache(data)
                            }.collect(Collectors.toList())
                        weatherCacheDao.clear()
                        weatherCacheDao.insertAll(*weatherList.toTypedArray())

                        val hourlyWeatherList =
                            availableWeatherData.hourlyDataList.stream().map { data ->
                                HourlyWeatherCache(data)
                            }.collect(Collectors.toList())
                        hourlyWeatherCacheDao.clear()
                        hourlyWeatherCacheDao.insertAll(*hourlyWeatherList.toTypedArray())

                        val dailyWeatherList =
                            availableWeatherData.dailyDataList.stream().map { data ->
                                DailyWeatherCache(data)
                            }.collect(Collectors.toList())
                        dailyWeatherCacheDao.clear()
                        dailyWeatherCacheDao.insertAll(*dailyWeatherList.toTypedArray())

                        _viewModelState.value = State.SUCCESS
                    } ?: run {
                        _viewModelState.value = State.FAILED
                    }
                } else {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(
                        response.errorBody()!!.charStream(),
                        ErrorResponse::class.java
                    )
                    if (errorResponse != null) {
                        _showMessage.value = errorResponse.message
                    }
                    _viewModelState.value = State.FAILED
                }

                _requestingWeatherData.value = false
            } catch (exception: Exception) {
                when (exception) {
                    is JsonSyntaxException,
                    is JsonIOException -> {
                        Log.e(
                            WeatherDataViewModel::class.java.canonicalName,
                            exception.message,
                            exception
                        )
                        _requestingWeatherData.value = false
                        _viewModelState.value = State.FAILED
                    }
                    else -> throw exception
                }
            }
        }
    }
}