package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.DailyWeatherData
import br.com.marcottc.weatherpeek.model.HourlyWeatherData

class MockGenerator {
    companion object {
        fun generateDailyWeatherData(): List<DailyWeatherData> {
            return listOf(
                DailyWeatherData(1599490800000, 34.0, 29.0),
                DailyWeatherData(1599577200000, 34.0, 27.0),
                DailyWeatherData(1599663600000, 32.0, 28.0),
                DailyWeatherData(1599750000000, 35.0, 30.0),
                DailyWeatherData(1599836400000, 34.0, 28.0)
            )
        }

        fun generateHourlyWeatherData(): List<HourlyWeatherData> {
            return listOf(
                HourlyWeatherData(1599487200000, 29.0),
                HourlyWeatherData(1599490800000, 34.0),
                HourlyWeatherData(1599494400000, 30.0),
                HourlyWeatherData(1599498000000, 31.0),
                HourlyWeatherData(1599501600000, 29.0)
            )
        }
    }
}