package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.HourlyWeatherData

class HourlyDataForecastMockGenerator {
    companion object {
        fun generate(): List<HourlyWeatherData> {
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