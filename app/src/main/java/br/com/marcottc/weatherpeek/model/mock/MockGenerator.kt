package br.com.marcottc.weatherpeek.model.mock

import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO

class MockGenerator {
    companion object {
        // Data related methods
        fun generateOneCallWeatherData(): OneCallWeatherDTO {
            return OneCallWeatherDTO(0.0, 0.0, "America/Chicago", "-18000",
                generateCurrentWeatherData(),
                generateHourlyWeatherData(),
                generateDailyWeatherData()
            )
        }

        fun generateCurrentWeatherData(): CurrentWeatherData {
            return CurrentWeatherData(1600434690000, 1600422677000, 1600463008000,
            34.0, 102, 42, 23)
        }

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

        // Cache related mock methods
        fun generateCurrentWeatherCache(): CurrentWeatherCache {
            return CurrentWeatherCache(
                dt=1718756791,
                latitude=37.422,
                longitude=-122.084,
                timezone="America/Los_Angeles",
                sunrise=1718714841,
                sunset=1718767913,
                temp=25.86,
                pressure=1009,
                windSpeed=8.75,
                windDeg=340.0,
                windGust=0.0,
                rainAmount=0.0,
                snowAmount=0.0,
                humidity=38,
                clouds=0
            )
        }

        fun generateHourlyWeatherCacheList(): List<HourlyWeatherCache> {
            return listOf(
                HourlyWeatherCache(dt=1718924400, temp=26.95),
                HourlyWeatherCache(dt=1718920800, temp=26.64),
                HourlyWeatherCache(dt=1718917200, temp=27.25),
                HourlyWeatherCache(dt=1718913600, temp=27.07),
                HourlyWeatherCache(dt=1718910000, temp=25.69),
                HourlyWeatherCache(dt=1718906400, temp=23.37),
                HourlyWeatherCache(dt=1718902800, temp=21.14),
                HourlyWeatherCache(dt=1718899200, temp=18.32),
                HourlyWeatherCache(dt=1718895600, temp=15.46),
                HourlyWeatherCache(dt=1718892000, temp=13.04),
                HourlyWeatherCache(dt=1718888400, temp=11.92),
                HourlyWeatherCache(dt=1718884800, temp=12.1),
                HourlyWeatherCache(dt=1718881200, temp=12.4),
                HourlyWeatherCache(dt=1718877600, temp=12.57),
                HourlyWeatherCache(dt=1718874000, temp=12.96),
                HourlyWeatherCache(dt=1718870400, temp=13.52),
                HourlyWeatherCache(dt=1718866800, temp=13.7),
                HourlyWeatherCache(dt=1718863200, temp=14.09),
                HourlyWeatherCache(dt=1718859600, temp=14.65),
                HourlyWeatherCache(dt=1718856000, temp=15.48),
                HourlyWeatherCache(dt=1718852400, temp=17.66),
                HourlyWeatherCache(dt=1718848800, temp=20.11),
                HourlyWeatherCache(dt=1718845200, temp=21.55),
                HourlyWeatherCache(dt=1718841600, temp=22.52),
                HourlyWeatherCache(dt=1718838000, temp=23.12),
                HourlyWeatherCache(dt=1718834400, temp=23.71),
                HourlyWeatherCache(dt=1718830800, temp=24.59),
                HourlyWeatherCache(dt=1718827200, temp=24.35),
                HourlyWeatherCache(dt=1718823600, temp=23.36),
                HourlyWeatherCache(dt=1718820000, temp=21.93),
                HourlyWeatherCache(dt=1718816400, temp=19.73),
                HourlyWeatherCache(dt=1718812800, temp=17.11),
                HourlyWeatherCache(dt=1718809200, temp=14.99),
                HourlyWeatherCache(dt=1718805600, temp=13.08),
                HourlyWeatherCache(dt=1718802000, temp=11.8),
                HourlyWeatherCache(dt=1718798400, temp=11.96),
                HourlyWeatherCache(dt=1718794800, temp=12.15),
                HourlyWeatherCache(dt=1718791200, temp=12.46),
                HourlyWeatherCache(dt=1718787600, temp=12.78),
                HourlyWeatherCache(dt=1718784000, temp=13.08),
                HourlyWeatherCache(dt=1718780400, temp=13.38),
                HourlyWeatherCache(dt=1718776800, temp=13.88),
                HourlyWeatherCache(dt=1718773200, temp=14.8),
                HourlyWeatherCache(dt=1718769600, temp=17.56),
                HourlyWeatherCache(dt=1718766000, temp=20.79),
                HourlyWeatherCache(dt=1718762400, temp=23.56),
                HourlyWeatherCache(dt=1718758800, temp=25.27),
                HourlyWeatherCache(dt=1718755200, temp=25.86)
            )
        }
    }
}