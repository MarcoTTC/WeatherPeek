package br.com.marcottc.weatherpeek.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.marcottc.weatherpeek.model.OneCallWeatherData
import br.com.marcottc.weatherpeek.network.RetrofitClientInstance
import br.com.marcottc.weatherpeek.network.service.OneCallService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WeatherDataViewModel : ViewModel() {

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

    init {
        _requestingWeatherData.value = false
        _availableWeatherData.value = null
        _showMessage.value = ""

        retrofitInstance = RetrofitClientInstance.getRetrofitInstance()
        oneCallService = retrofitInstance.create(OneCallService::class.java)
    }

    fun getWeatherData() {
        if (_requestingWeatherData.value == true) {
            _showMessage.value = "Please wait a moment..."
        } else {
            _requestingWeatherData.value = true

            val requestWeatherDataCall = oneCallService.getWeatherData(lat = 0.0, lon = 0.0)
            requestWeatherDataCall.enqueue(object : Callback<OneCallWeatherData>{
                override fun onResponse(
                    call: Call<OneCallWeatherData>,
                    response: Response<OneCallWeatherData>
                ) {
                    val body = response.body()
                    val gson = Gson()

                    if (response.isSuccessful) {
                        val oneWeatherDataResponse = gson.fromJson(body.toString(), OneCallWeatherData::class.java)
                        _availableWeatherData.value = oneWeatherDataResponse
                    } else {
                        _showMessage.value = "Failed to retrieve weather data"
                    }

                    _requestingWeatherData.value = false
                }

                override fun onFailure(call: Call<OneCallWeatherData>, t: Throwable) {
                    _requestingWeatherData.value = false
                }
            })
        }
    }
}