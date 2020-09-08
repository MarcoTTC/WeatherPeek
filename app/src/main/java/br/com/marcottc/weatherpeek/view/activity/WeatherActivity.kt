package br.com.marcottc.weatherpeek.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import br.com.marcottc.weatherpeek.databinding.ActivityWeatherLayoutBinding
import br.com.marcottc.weatherpeek.model.mock.DailyDataForecastMockGenerator
import br.com.marcottc.weatherpeek.model.mock.HourlyDataForecastMockGenerator
import br.com.marcottc.weatherpeek.view.adapter.DailyForecastAdapter
import br.com.marcottc.weatherpeek.view.adapter.HourlyForecastAdapter

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherLayoutBinding

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var dailyForecastAdapter: DailyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        hourlyForecastAdapter = HourlyForecastAdapter()
        binding.hourlyForecastRecyclerView.adapter = hourlyForecastAdapter

        hourlyForecastAdapter.setHourlyForecastDataList(HourlyDataForecastMockGenerator.generate())

        dailyForecastAdapter = DailyForecastAdapter()
        binding.dailyForecastRecyclerView.adapter = dailyForecastAdapter

        dailyForecastAdapter.setDailyForecastDataList(DailyDataForecastMockGenerator.generate())

        binding.closeIcon.setOnClickListener {
            onBackPressed()
        }
    }
}