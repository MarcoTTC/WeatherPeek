package br.com.marcottc.weatherpeek.view.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ActivityMainLayoutBinding
import br.com.marcottc.weatherpeek.model.OneCallWeatherData
import br.com.marcottc.weatherpeek.model.mock.MockGenerator
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

        updatingWeatherData(MockGenerator.generateOneCallWeatherData())
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