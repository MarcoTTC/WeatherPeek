package br.com.marcottc.weatherpeek.view.activity

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import br.com.marcottc.weatherpeek.R
import br.com.marcottc.weatherpeek.databinding.ActivityWeatherLayoutBinding
import br.com.marcottc.weatherpeek.espresso.BooleanIdlingResource
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.util.PermissionUtil
import br.com.marcottc.weatherpeek.view.adapter.DailyForecastAdapter
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter
import br.com.marcottc.weatherpeek.viewmodel.WeatherPeekViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.annotations.TestOnly
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherLayoutBinding
    private val weatherPeekViewModel: WeatherPeekViewModel by viewModel()
    private val permissionUtil: PermissionUtil by inject()

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var dailyForecastAdapter: DailyForecastAdapter

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    @TestOnly
    val requestingWeatherDataIdlingResource = BooleanIdlingResource("requestingWeatherData")

    override fun onCreate(savedInstanceState: Bundle?) {
        // Draws the activity behind the status and navigation bars, which become transparent.
        // The status bar sits on the light orange background, so it keeps dark icons (the app
        // theme is always light, so this does not follow the system dark mode).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        applySystemBarInsets()

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

        weatherPeekViewModel.viewModelState.observe(this) { currentState ->
            when (currentState) {
                WeatherPeekViewModel.State.LOADING -> {
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
                WeatherPeekViewModel.State.SUCCESS -> {
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

        weatherPeekViewModel.hourlyWeatherListCache.observe(this) { data ->
            if (data != null && data.isNotEmpty()) {
                updatingHourlyWeatherList(data)
            }
        }

        weatherPeekViewModel.dailyWeatherListCache.observe(this) { data ->
            if (data != null && data.isNotEmpty()) {
                updatingDailyWeatherList(data)
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

    // Keeps the forecast card clear of the system bars and the display cutout while edge-to-edge.
    // The background still fills the whole screen behind the bars; only the content is padded.
    // The insets are consumed here, so the views below do not apply them a second time.
    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.coordinatorLayout) { content, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            content.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun requestingLocationPermission() {
        val permissionArray = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        when {
            permissionUtil.hasLocationPermission() -> {
                weatherPeekViewModel.getWeatherData()
                requestingWeatherDataIdlingResource.setIdle(false)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && ActivityCompat.shouldShowRequestPermissionRationale(
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

    private fun updatingHourlyWeatherList(hourlyWeatherList: List<HourlyWeatherCache>) {
        hourlyForecastAdapter.setHourlyForecastDataList(hourlyWeatherList)
    }

    private fun updatingDailyWeatherList(dailyWeatherList: List<DailyWeatherCache>) {
        dailyForecastAdapter.setDailyForecastDataList(dailyWeatherList)
        binding.uviMeter.setCurrentUvi(dailyWeatherList[0].uvi)
    }
}