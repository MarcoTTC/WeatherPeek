package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO

class MockGenerator {
    companion object {
        fun generateOneCallWeatherData(): OneCallWeatherDTO {
            return OneCallWeatherDTO(0.0, 0.0, "America/Chicago", "-18000",
                generateCurrentWeatherData(),
                generateHourlyWeatherData(),
                generateDailyWeatherData()
            )
        }

        private fun generateCurrentWeatherData(): CurrentWeatherData {
            return CurrentWeatherData(1600434690000, 1600422677000, 1600463008000,
            34.0, 102, 42, 23)
        }

        private fun generateDailyWeatherData(): List<DailyWeatherData> {
            return listOf(
                DailyWeatherData(1599490800000, 34.0, 29.0),
                DailyWeatherData(1599577200000, 34.0, 27.0),
                DailyWeatherData(1599663600000, 32.0, 28.0),
                DailyWeatherData(1599750000000, 35.0, 30.0),
                DailyWeatherData(1599836400000, 34.0, 28.0)
            )
        }

        private fun generateHourlyWeatherData(): List<HourlyWeatherData> {
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