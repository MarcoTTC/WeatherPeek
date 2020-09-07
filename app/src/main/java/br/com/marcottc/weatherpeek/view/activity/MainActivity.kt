package br.com.marcottc.weatherpeek.view.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import br.com.marcottc.weatherpeek.databinding.ActivityMainLayoutBinding
import br.com.marcottc.weatherpeek.model.mock.HourlyDataForecastMockGenerator
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainLayoutBinding

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        hourlyForecastAdapter = HourlyForecastAdapter()
        binding.forecastRecyclerView.adapter = hourlyForecastAdapter

        hourlyForecastAdapter.setHourlyForecastDataList(HourlyDataForecastMockGenerator.generate())

        binding.fab.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this, binding.forecastCard, "forecast_card")
            startActivity(intent, options.toBundle())
        }
    }
}