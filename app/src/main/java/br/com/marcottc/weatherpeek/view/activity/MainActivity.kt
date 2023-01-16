package br.com.marcottc.weatherpeek.view.activity

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ActivityMainLayoutBinding
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import br.com.marcottc.weatherpeek.viewmodel.factory.ViewModelWithApplicationContextFactory
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import android.util.Pair as AndroidPair

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
            requestingLocationPermission()
        }

        hourlyForecastAdapter = HourlyForecastAdapter()
        binding.forecastRecyclerView.adapter = hourlyForecastAdapter

        binding.fab.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                AndroidPair.create(binding.forecastCard, "forecast_card"),
                AndroidPair.create(binding.forecastIcon, "forecast_icon"),
                AndroidPair.create(binding.forecastTitle, "forecast_title")
            )
            startActivity(intent, options.toBundle())
        }

        val viewModelWithApplicationContextFactory = ViewModelWithApplicationContextFactory(applicationContext)
        weatherDataViewModel = ViewModelProvider(this, viewModelWithApplicationContextFactory).get(WeatherDataViewModel::class.java)

        weatherDataViewModel.viewModelState.observe(this, { currentState ->
            when (currentState) {
                WeatherDataViewModel.State.LOADING -> {
                    binding.loadingSymbol.visibility = View.VISIBLE
                    binding.timezoneType.visibility = View.GONE
                    binding.currentTimeValue.visibility = View.GONE
                    binding.dayNightIcon.visibility = View.GONE
                    binding.temperatureValue.visibility = View.GONE
                    binding.weatherType.visibility = View.GONE
                    binding.weatherIcon.visibility = View.GONE
                    binding.sunriseIcon.visibility = View.GONE
                    binding.sunriseTime.visibility = View.GONE
                    binding.sunsetIcon.visibility = View.GONE
                    binding.sunsetTime.visibility = View.GONE
                    binding.pressureIcon.visibility = View.GONE
                    binding.pressureValue.visibility = View.GONE
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
                    binding.fab.visibility = View.GONE
                }
                WeatherDataViewModel.State.SUCCESS -> {
                    binding.loadingSymbol.visibility = View.GONE
                    binding.timezoneType.visibility = View.VISIBLE
                    binding.currentTimeValue.visibility = View.VISIBLE
                    binding.dayNightIcon.visibility = View.VISIBLE
                    binding.temperatureValue.visibility = View.VISIBLE
                    binding.weatherType.visibility = View.VISIBLE
                    binding.weatherIcon.visibility = View.VISIBLE
                    binding.sunriseIcon.visibility = View.VISIBLE
                    binding.sunriseTime.visibility = View.VISIBLE
                    binding.sunsetIcon.visibility = View.VISIBLE
                    binding.sunsetTime.visibility = View.VISIBLE
                    binding.pressureIcon.visibility = View.VISIBLE
                    binding.pressureValue.visibility = View.VISIBLE
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
                    binding.fab.visibility = View.VISIBLE
                }
                else -> {
                    binding.loadingSymbol.visibility = View.GONE
                    binding.timezoneType.visibility = View.GONE
                    binding.currentTimeValue.visibility = View.GONE
                    binding.dayNightIcon.visibility = View.GONE
                    binding.temperatureValue.visibility = View.GONE
                    binding.weatherType.visibility = View.GONE
                    binding.weatherIcon.visibility = View.GONE
                    binding.sunriseIcon.visibility = View.GONE
                    binding.sunriseTime.visibility = View.GONE
                    binding.sunsetIcon.visibility = View.GONE
                    binding.sunsetTime.visibility = View.GONE
                    binding.pressureIcon.visibility = View.GONE
                    binding.pressureValue.visibility = View.GONE
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
                    binding.fab.visibility = View.VISIBLE
                }
            }
        })

        weatherDataViewModel.mustRequestPermissionFirst.observe(this, { mustRequestPermission ->
            if (mustRequestPermission) {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.permission_needed_title)
                builder.setMessage(R.string.location_perm_denied)
                builder.create().show()
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

        requestingLocationPermission()
    }

    private fun requestingLocationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                weatherDataViewModel.getWeatherData()
            }
        val permissionArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED-> {
                weatherDataViewModel.getWeatherData()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) &&
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )-> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.permission_needed_title)
                builder.setMessage(R.string.need_perm_location)
                builder.setPositiveButton(R.string.btn_label_i_accept) { dialog, _ ->
                    requestPermissionLauncher.launch(permissionArray)
                    dialog.dismiss()
                }
                builder.setOnCancelListener {
                    weatherDataViewModel.getWeatherData()
                }
                builder.create().show()
            }
            else -> {
                requestPermissionLauncher.launch(permissionArray)
            }
        }
    }

    private fun updatingWeatherData(oneCallWeatherDTO: OneCallWeatherDTO) {
        binding.timezoneType.text = oneCallWeatherDTO.timezone

        val timeFormatter = SimpleDateFormat("HH:mm")

        val currentWeatherData = oneCallWeatherDTO.current
        binding.currentTimeValue.text = timeFormatter.format(Date(currentWeatherData.dt*1000))

        if (currentWeatherData.dt >= currentWeatherData.sunrise && currentWeatherData.dt < currentWeatherData.sunset) {
            binding.dayNightIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_sunny))
            binding.dayNightIcon.contentDescription = resources.getString(R.string.icon_description_day)
        } else {
            binding.dayNightIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_starry_moon))
            binding.dayNightIcon.contentDescription = resources.getString(R.string.icon_description_night)
        }

        binding.temperatureValue.text = String.format("%.0f°c", currentWeatherData.temp)
        binding.weatherType.text = currentWeatherData.weatherList[0].main
        val iconRatio = (resources.displayMetrics.density * 48).toInt()
        Glide.with(this)
            .load(currentWeatherData.weatherList[0].getIconUrl())
            .override(iconRatio, iconRatio)
            .centerInside()
            .into(binding.weatherIcon)
        binding.weatherIcon.contentDescription = currentWeatherData.weatherList[0].description
        binding.sunriseTime.text = timeFormatter.format(Date(currentWeatherData.sunrise*1000))
        binding.sunsetTime.text = timeFormatter.format(Date(currentWeatherData.sunset*1000))
        binding.pressureValue.text = String.format("%d hPa", currentWeatherData.pressure)
        binding.humidityValue.text = String.format("%d %%", currentWeatherData.humidity)
        binding.cloudinessValue.text = String.format("%d %%", currentWeatherData.clouds)

        hourlyForecastAdapter.setHourlyForecastDataList(oneCallWeatherDTO.hourlyDataList)
    }
}