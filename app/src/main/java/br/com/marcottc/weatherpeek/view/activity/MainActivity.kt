package br.com.marcottc.weatherpeek.view.activity

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ActivityMainLayoutBinding
import br.com.marcottc.weatherpeek.espresso.BooleanIdlingResource
import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.util.forceRefreshSettings
import br.com.marcottc.weatherpeek.util.sharedPreferencesDb
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter
import br.com.marcottc.weatherpeek.viewmodel.WeatherPeekViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.annotations.TestOnly
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong
import android.util.Pair as AndroidPair

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLayoutBinding
    private val weatherPeekViewModel: WeatherPeekViewModel by viewModel()

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    @TestOnly
    val requestingWeatherDataIdlingResource = BooleanIdlingResource("requestingWeatherData")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(sharedPreferencesDb, MODE_PRIVATE)
        val isForceRefresh = sharedPreferences.getBoolean(forceRefreshSettings, false)
        binding.forceRefreshSwitch.isChecked = isForceRefresh

        binding.forceRefreshSwitch.setOnCheckedChangeListener { _, isChecked ->
            weatherPeekViewModel.setForceRefreshOption(isChecked)
        }

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

        weatherPeekViewModel.viewModelState.observe(this) { currentState ->
            when (currentState) {
                WeatherPeekViewModel.State.LOADING -> {
                    binding.currentWeatherCardLoadingSymbol.visibility = View.VISIBLE
                    binding.currentWeatherReloadIcon.visibility = View.GONE
                    binding.currentWeatherReloadText.visibility = View.GONE
                    binding.currentTimeValue.visibility = View.GONE
                    binding.temperatureValue.visibility = View.GONE
                    binding.weatherType.visibility = View.GONE
                    binding.weatherIcon.visibility = View.GONE
                    binding.sunriseIcon.visibility = View.GONE
                    binding.sunriseTime.visibility = View.GONE
                    binding.sunsetIcon.visibility = View.GONE
                    binding.sunsetTime.visibility = View.GONE
                    binding.windCardLoadingSymbol.visibility = View.VISIBLE
                    binding.windCardReloadIcon.visibility = View.GONE
                    binding.windSpeedTypeIcon.visibility = View.GONE
                    binding.windSpeedAmount.visibility = View.GONE
                    binding.windDirectionTypeIcon.visibility = View.GONE
                    binding.windDirectionAmount.visibility = View.GONE
                    binding.windGustTypeIcon.visibility = View.GONE
                    binding.windGustAmount.visibility = View.GONE
                    binding.precipitationCardLoadingSymbol.visibility = View.VISIBLE
                    binding.precipitationCardReloadIcon.visibility = View.GONE
                    binding.precipitationTypeIcon.visibility = View.GONE
                    binding.precipitationAmount.visibility = View.GONE
                    binding.humidityIcon.visibility = View.GONE
                    binding.humidityValue.visibility = View.GONE
                    binding.cloudinessIcon.visibility = View.GONE
                    binding.cloudinessValue.visibility = View.GONE
                    binding.forecastLoadingSymbol.visibility = View.VISIBLE
                    binding.forecastReloadIcon.visibility = View.GONE
                    binding.forecastReloadText.visibility = View.GONE
                    binding.forecastRecyclerView.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                }
                WeatherPeekViewModel.State.SUCCESS -> {
                    binding.currentWeatherCardLoadingSymbol.visibility = View.GONE
                    binding.currentWeatherReloadIcon.visibility = View.GONE
                    binding.currentWeatherReloadText.visibility = View.GONE
                    binding.currentTimeValue.visibility = View.VISIBLE
                    binding.temperatureValue.visibility = View.VISIBLE
                    binding.weatherType.visibility = View.VISIBLE
                    binding.weatherIcon.visibility = View.VISIBLE
                    binding.sunriseIcon.visibility = View.VISIBLE
                    binding.sunriseTime.visibility = View.VISIBLE
                    binding.sunsetIcon.visibility = View.VISIBLE
                    binding.sunsetTime.visibility = View.VISIBLE
                    binding.windCardLoadingSymbol.visibility = View.GONE
                    binding.windCardReloadIcon.visibility = View.GONE
                    binding.windSpeedTypeIcon.visibility = View.VISIBLE
                    binding.windSpeedAmount.visibility = View.VISIBLE
                    binding.windDirectionTypeIcon.visibility = View.VISIBLE
                    binding.windDirectionAmount.visibility = View.VISIBLE
                    binding.windGustTypeIcon.visibility = View.VISIBLE
                    binding.windGustAmount.visibility = View.VISIBLE
                    binding.precipitationCardLoadingSymbol.visibility = View.GONE
                    binding.precipitationCardReloadIcon.visibility = View.GONE
                    binding.precipitationTypeIcon.visibility = View.VISIBLE
                    binding.precipitationAmount.visibility = View.VISIBLE
                    binding.humidityIcon.visibility = View.VISIBLE
                    binding.humidityValue.visibility = View.VISIBLE
                    binding.cloudinessIcon.visibility = View.VISIBLE
                    binding.cloudinessValue.visibility = View.VISIBLE
                    binding.forecastLoadingSymbol.visibility = View.GONE
                    binding.forecastReloadIcon.visibility = View.GONE
                    binding.forecastReloadText.visibility = View.GONE
                    binding.forecastRecyclerView.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                }
                else -> {
                    binding.currentWeatherCardLoadingSymbol.visibility = View.GONE
                    binding.currentWeatherReloadIcon.visibility = View.VISIBLE
                    binding.currentWeatherReloadText.visibility = View.VISIBLE
                    binding.currentTimeValue.visibility = View.GONE
                    binding.temperatureValue.visibility = View.GONE
                    binding.weatherType.visibility = View.GONE
                    binding.weatherIcon.visibility = View.GONE
                    binding.sunriseIcon.visibility = View.GONE
                    binding.sunriseTime.visibility = View.GONE
                    binding.sunsetIcon.visibility = View.GONE
                    binding.sunsetTime.visibility = View.GONE
                    binding.windCardLoadingSymbol.visibility = View.GONE
                    binding.windCardReloadIcon.visibility = View.VISIBLE
                    binding.windSpeedTypeIcon.visibility = View.GONE
                    binding.windSpeedAmount.visibility = View.GONE
                    binding.windDirectionTypeIcon.visibility = View.GONE
                    binding.windDirectionAmount.visibility = View.GONE
                    binding.windGustTypeIcon.visibility = View.GONE
                    binding.windGustAmount.visibility = View.GONE
                    binding.precipitationCardLoadingSymbol.visibility = View.GONE
                    binding.precipitationCardReloadIcon.visibility = View.VISIBLE
                    binding.precipitationTypeIcon.visibility = View.GONE
                    binding.precipitationAmount.visibility = View.GONE
                    binding.humidityIcon.visibility = View.GONE
                    binding.humidityValue.visibility = View.GONE
                    binding.cloudinessIcon.visibility = View.GONE
                    binding.cloudinessValue.visibility = View.GONE
                    binding.forecastLoadingSymbol.visibility = View.GONE
                    binding.forecastReloadIcon.visibility = View.VISIBLE
                    binding.forecastReloadText.visibility = View.VISIBLE
                    binding.forecastRecyclerView.visibility = View.GONE
                    binding.fab.visibility = View.VISIBLE
                }
            }
        }

        weatherPeekViewModel.mustRequestPermissionFirst.observe(this) { mustRequestPermission ->
            if (mustRequestPermission) {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.permission_needed_title)
                builder.setMessage(R.string.location_perm_denied)
                builder.create().show()
                requestingWeatherDataIdlingResource.setIdle(true)
            }
        }

        weatherPeekViewModel.currentWeatherCache.observe(this) { data ->
            if (data != null) {
                updatingWeatherCards(data)
            }
        }

        weatherPeekViewModel.weatherListCache.observe(this) { data ->
            if (data != null && data.isNotEmpty()) {
                updatingWeatherList(data)
            }
        }

        weatherPeekViewModel.hourlyWeatherListCache.observe(this) { data ->
            if (data != null && data.isNotEmpty()) {
                updatingHourlyWeatherList(data)
            }
        }

        weatherPeekViewModel.requestingWeatherData.observe(this) { isRequesting ->
            if (!isRequesting) {
                binding.swipeRefreshLayout.isRefreshing = false
                requestingWeatherDataIdlingResource.setIdle(true)
            }
        }

        weatherPeekViewModel.showMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .show()
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                weatherPeekViewModel.getWeatherData()
                requestingWeatherDataIdlingResource.setIdle(false)
            }

        requestingLocationPermission()
    }

    private fun requestingLocationPermission() {
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
                    ) == PackageManager.PERMISSION_GRANTED -> {
                weatherPeekViewModel.getWeatherData()
                requestingWeatherDataIdlingResource.setIdle(false)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(R.string.permission_needed_title)
                builder.setMessage(R.string.need_perm_location)
                builder.setPositiveButton(R.string.btn_label_i_accept) { dialog, _ ->
                    requestPermissionLauncher.launch(permissionArray)
                    dialog.dismiss()
                }
                // TODO - Check if I should remove the cancel listener on this dialog
                builder.setOnCancelListener {
                    weatherPeekViewModel.getWeatherData()
                    requestingWeatherDataIdlingResource.setIdle(false)
                }
                builder.create().show()
            }
            else -> {
                requestPermissionLauncher.launch(permissionArray)
            }
        }
    }

    private fun updatingWeatherCards(currentWeatherCache: CurrentWeatherCache) {
        val currentLocale = resources.configuration.locales[0]
        val timeFormatter = SimpleDateFormat("HH:mm", currentLocale)
        binding.currentTimeValue.text = timeFormatter.format(Date(currentWeatherCache.dt * 1000))

        binding.temperatureValue.text = String.format("%.0f°c", currentWeatherCache.temp)

        binding.sunriseTime.text = timeFormatter.format(Date(currentWeatherCache.sunrise * 1000))
        binding.sunsetTime.text = timeFormatter.format(Date(currentWeatherCache.sunset * 1000))
        binding.windSpeedAmount.text = String.format("%.2f mph", currentWeatherCache.windSpeed)
        binding.windDirectionAmount.text = String.format("%.0fº", currentWeatherCache.windDeg)
        binding.windGustAmount.text = String.format("%.2f mph", currentWeatherCache.windGust)

        val rainValue = currentWeatherCache.rainAmount
        val snowValue = currentWeatherCache.snowAmount
        if (rainValue == 0.0 && snowValue == 0.0) {
            val noPrecipitationMessage: String = if (currentWeatherCache.temp > 0) {
                getString(R.string.no_rain)
            } else {
                getString(R.string.no_snow)
            }
            binding.precipitationAmount.text = noPrecipitationMessage
            binding.precipitationTypeIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_storm
                )
            )
        } else {
            if (rainValue >= snowValue) {
                binding.precipitationAmount.text = String.format("%d mm/h", rainValue.roundToLong())
                binding.precipitationTypeIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_storm))
            } else {
                binding.precipitationAmount.text = String.format("%d mm/h", snowValue.roundToLong())
                binding.precipitationTypeIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_snowflake))
            }
        }
        binding.humidityValue.text = String.format("%d %%", currentWeatherCache.humidity)
        binding.cloudinessValue.text = String.format("%d %%", currentWeatherCache.clouds)
    }

    private fun updatingWeatherList(weatherList: List<WeatherCache>) {
        binding.weatherType.text = weatherList[0].main

        val iconRatio = (resources.displayMetrics.density * 48).toInt()
        Glide.with(this)
            .load(weatherList[0].getIconUrl())
            .override(iconRatio, iconRatio)
            .centerInside()
            .into(binding.weatherIcon)

        binding.weatherIcon.contentDescription = weatherList[0].description
    }

    private fun updatingHourlyWeatherList(hourlyWeatherList: List<HourlyWeatherCache>) {
        hourlyForecastAdapter.setHourlyForecastDataList(hourlyWeatherList)
    }
}