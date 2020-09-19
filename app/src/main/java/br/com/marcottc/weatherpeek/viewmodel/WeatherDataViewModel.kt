package br.com.marcottc.weatherpeek.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.marcottc.weatherpeek.model.ErrorResponse
import br.com.marcottc.weatherpeek.model.OneCallWeatherData
import br.com.marcottc.weatherpeek.network.RetrofitClientInstance
import br.com.marcottc.weatherpeek.network.service.OneCallService
import br.com.marcottc.weatherpeek.util.OneCallAppId
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WeatherDataViewModel(private val context: Context) : ViewModel() {

    enum class State {
        LOADING,
        SUCCESS,
        FAILED
    }

    private val _viewModelState: MutableLiveData<State> = MutableLiveData()
    val viewModelState: LiveData<State>
        get() = _viewModelState

    private val _requestingWeatherData: MutableLiveData<Boolean> = MutableLiveData()
    val requestingWeatherData: LiveData<Boolean>
        get() = _requestingWeatherData

    private val _availableWeatherData: MutableLiveData<OneCallWeatherData?> = MutableLiveData()
    val availableWeatherData: LiveData<OneCallWeatherData?>
        get() = _availableWeatherData

    private val _showMessage: MutableLiveData<String> = MutableLiveData()
    val showMessage: LiveData<String>
        get() = _showMessage

    private var retrofitInstance: Retrofit
    private var oneCallService: OneCallService
    private var locationManager: LocationManager

    private lateinit var requestWeatherDataCall: Call<OneCallWeatherData>

    private val mLocationListener = object : LocationListener {
        private var previousAccuracy: Float = 1000000.0F
        private var countdown: Int = 5

        override fun onLocationChanged(location: Location) {
            val currentAccuracy = location.accuracy
            if (currentAccuracy < previousAccuracy && countdown > 0) {
                previousAccuracy = currentAccuracy
                countdown--
            } else {
                previousAccuracy = 1000000.0F
                countdown = 5
                recoveringWeatherData(location.latitude, location.longitude)
            }
        }
    }

    init {
        _viewModelState.value = State.LOADING
        _requestingWeatherData.value = false
        _availableWeatherData.value = null
        _showMessage.value = ""

        retrofitInstance = RetrofitClientInstance.getRetrofitInstance()
        oneCallService = retrofitInstance.create(OneCallService::class.java)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun getWeatherData() {
        if (_requestingWeatherData.value == true) {
            _showMessage.value = "Please wait a moment..."
        } else {
            if (OneCallAppId.appId.isEmpty()) {
                _showMessage.value = "Please set up an app id before building the project!"
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
                return
            }

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                _requestingWeatherData.value = true
                _viewModelState.value = State.LOADING

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0.0F,
                    mLocationListener
                )
            } else {
                _showMessage.value = "No valid permission to recover device location"
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
            }
        }
    }

    private fun recoveringWeatherData(latitude: Double, longitude: Double) {
        locationManager.removeUpdates(mLocationListener)

        requestWeatherDataCall = oneCallService.getWeatherData(lat = latitude, lon = longitude)
        requestWeatherDataCall.enqueue(object : Callback<OneCallWeatherData> {
            override fun onResponse(
                call: Call<OneCallWeatherData>,
                response: Response<OneCallWeatherData>
            ) {
                if (response.isSuccessful) {
                    _availableWeatherData.value = response.body()
                    _viewModelState.value = State.SUCCESS
                } else {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(
                        response.errorBody()!!.charStream(),
                        ErrorResponse::class.java
                    )
                    _showMessage.value = errorResponse.message
                    _viewModelState.value = State.FAILED
                }

                _requestingWeatherData.value = false
            }

            override fun onFailure(call: Call<OneCallWeatherData>, t: Throwable) {
                Log.e(WeatherDataViewModel::class.java.canonicalName, t.message, t)
                _requestingWeatherData.value = false
                _viewModelState.value = State.FAILED
            }
        })
    }

    override fun onCleared() {
        if (::requestWeatherDataCall.isInitialized && requestWeatherDataCall.isExecuted) {
            requestWeatherDataCall.cancel()
        }
    }
}