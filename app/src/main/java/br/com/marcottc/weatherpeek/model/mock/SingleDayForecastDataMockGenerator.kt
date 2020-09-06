package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.SingleDayForecastData

class SingleDayForecastDataMockGenerator {
    companion object {
        fun generate(): List<SingleDayForecastData> {
            return listOf(
                SingleDayForecastData("MON", "34°", "29°"),
                SingleDayForecastData("TUE", "34°", "27°"),
                SingleDayForecastData("WED", "32°", "28°"),
                SingleDayForecastData("THU", "35°", "30°"),
                SingleDayForecastData("FRI", "34°", "28°")
            )
        }
    }
}