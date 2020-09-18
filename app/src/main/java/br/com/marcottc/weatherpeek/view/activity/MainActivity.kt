package br.com.marcottc.weatherpeek.view.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ActivityMainLayoutBinding
import br.com.marcottc.weatherpeek.model.OneCallWeatherData
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLayoutBinding
    private lateinit var weatherDataViewModel: WeatherDataViewModel

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.primaryColor
            )
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            weatherDataViewModel.getWeatherData()
        }

        hourlyForecastAdapter = HourlyForecastAdapter()
        binding.forecastRecyclerView.adapter = hourlyForecastAdapter

        binding.fab.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.forecastCard,
                "forecast_card"
            )
            startActivity(intent, options.toBundle())
        }

        weatherDataViewModel = ViewModelProvider(this).get(WeatherDataViewModel::class.java)

        weatherDataViewModel.viewModelState.observe(this, { currentState ->
            when (currentState) {
                WeatherDataViewModel.State.LOADING -> {
                    binding.loadingSymbol.visibility = View.VISIBLE
                    binding.timezoneType.visibility = View.GONE
                    binding.currentTimeValue.visibility = View.GONE
                    binding.temperatureValue.visibility = View.GONE
                    binding.weatherType.visibility = View.GONE
                    binding.weatherIcon.visibility = View.GONE
                    binding.sunriseIcon.visibility = View.GONE
                    binding.sunriseTime.visibility = View.GONE
                    binding.sunsetIcon.visibility = View.GONE
                    binding.sunsetTime.visibility = View.GONE
                    binding.pressureIcon.visibility = View.GONE
                    binding.pressureIcon.visibility = View.GONE
                    binding.humidityIcon.visibility = View.GONE
                    binding.humidityValue.visibility = View.GONE
                    binding.cloudinessIcon.visibility = View.GONE
                    binding.cloudinessValue.visibility = View.GONE
                    binding.reloadIcon.visibility = View.GONE
                    binding.reloadText.visibility = View.GONE
                    binding.forecastLoadingSymbol.visibility = View.VISIBLE
                    binding.forecastReloadIcon.visibility = View.GONE
                    binding.forecastReloadText.visibility = View.GONE
                    binding.forecastRecyclerView.visibility = View.GONE
                }
                WeatherDataViewModel.State.SUCCESS -> {
                    binding.loadingSymbol.visibility = View.GONE
                    binding.timezoneType.visibility = View.VISIBLE
                    binding.currentTimeValue.visibility = View.VISIBLE
                    binding.temperatureValue.visibility = View.VISIBLE
                    binding.weatherType.visibility = View.VISIBLE
                    binding.weatherIcon.visibility = View.VISIBLE
                    binding.sunriseIcon.visibility = View.VISIBLE
                    binding.sunriseTime.visibility = View.VISIBLE
                    binding.sunsetIcon.visibility = View.VISIBLE
                    binding.sunsetTime.visibility = View.VISIBLE
                    binding.pressureIcon.visibility = View.VISIBLE
                    binding.pressureIcon.visibility = View.VISIBLE
                    binding.humidityIcon.visibility = View.VISIBLE
                    binding.humidityValue.visibility = View.VISIBLE
                    binding.cloudinessIcon.visibility = View.VISIBLE
                    binding.cloudinessValue.visibility = View.VISIBLE
                    binding.reloadIcon.visibility = View.GONE
                    binding.reloadText.visibility = View.GONE
                    binding.forecastLoadingSymbol.visibility = View.GONE
                    binding.forecastReloadIcon.visibility = View.GONE
                    binding.forecastReloadText.visibility = View.GONE
                    binding.forecastRecyclerView.visibility = View.VISIBLE
                }
                else -> {
                    binding.loadingSymbol.visibility = View.GONE
                    binding.timezoneType.visibility = View.GONE
                    binding.currentTimeValue.visibility = View.GONE
                    binding.temperatureValue.visibility = View.GONE
                    binding.weatherType.visibility = View.GONE
                    binding.weatherIcon.visibility = View.GONE
                    binding.sunriseIcon.visibility = View.GONE
                    binding.sunriseTime.visibility = View.GONE
                    binding.sunsetIcon.visibility = View.GONE
                    binding.sunsetTime.visibility = View.GONE
                    binding.pressureIcon.visibility = View.GONE
                    binding.pressureIcon.visibility = View.GONE
                    binding.humidityIcon.visibility = View.GONE
                    binding.humidityValue.visibility = View.GONE
                    binding.cloudinessIcon.visibility = View.GONE
                    binding.cloudinessValue.visibility = View.GONE
                    binding.reloadIcon.visibility = View.VISIBLE
                    binding.reloadText.visibility = View.VISIBLE
                    binding.forecastLoadingSymbol.visibility = View.GONE
                    binding.forecastReloadIcon.visibility = View.VISIBLE
                    binding.forecastReloadText.visibility = View.VISIBLE
                    binding.forecastRecyclerView.visibility = View.GONE
                }
            }
        })

        weatherDataViewModel.availableWeatherData.observe(this, { data ->
            if (data != null) {
                updatingWeatherData(data)
            }
        })

        weatherDataViewModel.requestingWeatherData.observe(this, { isRequesting ->
            if (!isRequesting) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })

        weatherDataViewModel.showMessage.observe(this, { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .show()
            }
        })

        weatherDataViewModel.getWeatherData()
    }

    private fun updatingWeatherData(oneCallWeatherData: OneCallWeatherData) {
        binding.timezoneType.text = oneCallWeatherData.timezone

        val timeFormatter = SimpleDateFormat("HH:mm")

        val currentWeatherData = oneCallWeatherData.current
        binding.currentTimeValue.text = timeFormatter.format(Date(currentWeatherData.dt))
        binding.temperatureValue.text = String.format("%.0f°c", currentWeatherData.temp)
        binding.weatherType.text = currentWeatherData.weatherList[0].main
        binding.weatherIcon.contentDescription = currentWeatherData.weatherList[0].description
        binding.sunriseTime.text = timeFormatter.format(Date(currentWeatherData.sunrise))
        binding.sunsetTime.text = timeFormatter.format(Date(currentWeatherData.sunset))
        binding.pressureValue.text = String.format("%d hPa", currentWeatherData.pressure)
        binding.humidityValue.text = String.format("%d %%", currentWeatherData.humidity)
        binding.cloudinessValue.text = String.format("%d %%", currentWeatherData.clouds)

        hourlyForecastAdapter.setHourlyForecastDataList(oneCallWeatherData.hourlyDataList)
    }
}