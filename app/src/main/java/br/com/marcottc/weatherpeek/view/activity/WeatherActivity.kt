package br.com.marcottc.weatherpeek.view.activity

import android.Manifest
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
import br.com.marcottc.weatherpeek.databinding.ActivityWeatherLayoutBinding
import br.com.marcottc.weatherpeek.model.OneCallWeatherData
import br.com.marcottc.weatherpeek.view.adapter.DailyForecastAdapter
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter
import br.com.marcottc.weatherpeek.viewmodel.WeatherDataViewModel
import br.com.marcottc.weatherpeek.viewmodel.factory.ViewModelWithApplicationContextFactory
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherLayoutBinding
    private lateinit var weatherDataViewModel: WeatherDataViewModel

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var dailyForecastAdapter: DailyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherLayoutBinding.inflate(LayoutInflater.from(this))
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
        binding.hourlyForecastRecyclerView.adapter = hourlyForecastAdapter

        dailyForecastAdapter = DailyForecastAdapter()
        binding.dailyForecastRecyclerView.adapter = dailyForecastAdapter

        binding.closeIcon.setOnClickListener {
            onBackPressed()
        }

        val viewModelWithApplicationContextFactory =
            ViewModelWithApplicationContextFactory(applicationContext)
        weatherDataViewModel = ViewModelProvider(this, viewModelWithApplicationContextFactory).get(
            WeatherDataViewModel::class.java
        )

        weatherDataViewModel.viewModelState.observe(this, { currentState ->
            when (currentState) {
                WeatherDataViewModel.State.LOADING -> {
                    binding.hourlyForecastLoadingSymbol.visibility = View.VISIBLE
                    binding.hourlyForecastReloadIcon.visibility = View.GONE
                    binding.hourlyForecastReloadText.visibility = View.GONE
                    binding.hourlyForecastRecyclerView.visibility = View.GONE
                    binding.dailyForecastLoadingSymbol.visibility = View.VISIBLE
                    binding.dailyForecastReloadIcon.visibility = View.GONE
                    binding.dailyForecastReloadText.visibility = View.GONE
                    binding.dailyForecastRecyclerView.visibility = View.GONE
                    binding.uviForecastLoadingSymbol.visibility = View.VISIBLE
                    binding.uviForecastReloadIcon.visibility = View.GONE
                    binding.uviForecastReloadText.visibility = View.GONE
                    binding.uviMeter.visibility = View.GONE
                }
                WeatherDataViewModel.State.SUCCESS -> {
                    binding.hourlyForecastLoadingSymbol.visibility = View.GONE
                    binding.hourlyForecastReloadIcon.visibility = View.GONE
                    binding.hourlyForecastReloadText.visibility = View.GONE
                    binding.hourlyForecastRecyclerView.visibility = View.VISIBLE
                    binding.dailyForecastLoadingSymbol.visibility = View.GONE
                    binding.dailyForecastReloadIcon.visibility = View.GONE
                    binding.dailyForecastReloadText.visibility = View.GONE
                    binding.dailyForecastRecyclerView.visibility = View.VISIBLE
                    binding.uviForecastLoadingSymbol.visibility = View.GONE
                    binding.uviForecastReloadIcon.visibility = View.GONE
                    binding.uviForecastReloadText.visibility = View.GONE
                    binding.uviMeter.visibility = View.VISIBLE
                }
                else -> {
                    binding.hourlyForecastLoadingSymbol.visibility = View.GONE
                    binding.hourlyForecastReloadIcon.visibility = View.VISIBLE
                    binding.hourlyForecastReloadText.visibility = View.VISIBLE
                    binding.hourlyForecastRecyclerView.visibility = View.GONE
                    binding.dailyForecastLoadingSymbol.visibility = View.GONE
                    binding.dailyForecastReloadIcon.visibility = View.VISIBLE
                    binding.dailyForecastReloadText.visibility = View.VISIBLE
                    binding.dailyForecastRecyclerView.visibility = View.GONE
                    binding.uviForecastLoadingSymbol.visibility = View.GONE
                    binding.uviForecastReloadIcon.visibility = View.VISIBLE
                    binding.uviForecastReloadText.visibility = View.VISIBLE
                    binding.uviMeter.visibility = View.GONE
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

    private fun updatingWeatherData(oneCallWeatherData: OneCallWeatherData) {
        hourlyForecastAdapter.setHourlyForecastDataList(oneCallWeatherData.hourlyDataList)
        dailyForecastAdapter.setDailyForecastDataList(oneCallWeatherData.dailyDataList)
        binding.uviMeter.setCurrentUvi(oneCallWeatherData.dailyDataList[0].uvi)
    }
}