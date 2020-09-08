package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.DailyWeatherData

class DailyDataForecastMockGenerator {
    companion object {
        fun generate(): List<DailyWeatherData> {
            return listOf(
                DailyWeatherData(1599490800000, 34.0, 29.0),
                DailyWeatherData(1599577200000, 34.0, 27.0),
                DailyWeatherData(1599663600000, 32.0, 28.0),
                DailyWeatherData(1599750000000, 35.0, 30.0),
                DailyWeatherData(1599836400000, 34.0, 28.0)
            )
        }
    }
}