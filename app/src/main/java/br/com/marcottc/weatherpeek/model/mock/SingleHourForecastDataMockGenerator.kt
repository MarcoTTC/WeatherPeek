package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.SingleHourForecastData

class SingleHourForecastDataMockGenerator {
    companion object {
        fun generate(): List<SingleHourForecastData> {
            return listOf(
                SingleHourForecastData("29°", "12:00AM"),
                SingleHourForecastData("34°", "NOW"),
                SingleHourForecastData("30°", "14:00AM"),
                SingleHourForecastData("31°", "15:00AM"),
                SingleHourForecastData("29°", "16:00AM")
            )
        }
    }
}