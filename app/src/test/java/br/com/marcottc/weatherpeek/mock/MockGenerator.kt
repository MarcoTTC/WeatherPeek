package br.com.marcottc.weatherpeek.mock

import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import br.com.marcottc.weatherpeek.model.dto.FeelsLikeData
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.model.dto.TempData
import br.com.marcottc.weatherpeek.model.dto.WeatherData

class MockGenerator {
    companion object {
        // Data related methods
        fun generateOneCallWeatherData(): OneCallWeatherDTO {
            return OneCallWeatherDTO(
                37.422,
                -122.084,
                "America/Los_Angeles",
                "-25200",
                generateCurrentWeatherData(),
                generateHourlyWeatherData(),
                generateDailyWeatherData()
            )
        }

        fun generateCurrentWeatherData(): CurrentWeatherData {
            return CurrentWeatherData(dt=1718844339, sunrise=1718801250, sunset=1718854329, temp=20.37, feelsLike=19.98, pressure=1011, humidity=58, dewPoint=11.84, clouds=0, visibility=10000, windSpeed=8.75, windDeg=350.0, windGust=0.0, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), rain=null, snow=null)
        }

        fun generateDailyWeatherData(): List<DailyWeatherData> {
            return listOf(
                DailyWeatherData(dt=1718827200, sunrise=1718801250, sunset=1718854329, temp= TempData(day=25.24, min=12.13, max=25.24, night=13.64, eve=20.19, morn=13.57), feelsLike= FeelsLikeData(day=24.39, night=12.99, eve=19.7, morn=12.65), pressure=1012, humidity=22, dewPoint=2.12, visibility=0, windSpeed=4.51, windDeg=296.0, windGust=5.09, weatherList=listOf(WeatherData(id=802, main="Clouds", description="nuvens dispersas", icon="03d")), clouds=49, pop=0.0, rain=0.0, uvi=8.29),
                DailyWeatherData(dt=1718913600, sunrise=1718887660, sunset=1718940742, temp=TempData(day=26.23, min=11.94, max=26.38, night=13.87, eve=19.91, morn=12.73), feelsLike=FeelsLikeData(day=26.23, night=13.27, eve=19.29, morn=12.12), pressure=1010, humidity=30, dewPoint=7.13, visibility=0, windSpeed=4.1, windDeg=300.0, windGust=3.74, weatherList=listOf(WeatherData(id=801, main="Clouds", description="algumas nuvens", icon="02d")), clouds=22, pop=0.0, rain=0.0, uvi=8.1),
                DailyWeatherData(dt=1719000000, sunrise=1718974073, sunset=1719027154, temp=TempData(day=29.28, min=11.91, max=30.6, night=17.23, eve=24.58, morn=13.25), feelsLike=FeelsLikeData(day=27.85, night=16.58, eve=24.06, morn=12.64), pressure=1008, humidity=25, dewPoint=7.05, visibility=0, windSpeed=3.67, windDeg=317.0, windGust=3.72, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), clouds=0, pop=0.0, rain=0.0, uvi=8.79),
                DailyWeatherData(dt=1719086400, sunrise=1719060487, sunset=1719113565, temp=TempData(day=32.63, min=14.99, max=32.7, night=17.16, eve=22.01, morn=16.87), feelsLike=FeelsLikeData(day=30.64, night=16.55, eve=21.49, morn=16.15), pressure=1008, humidity=21, dewPoint=7.96, visibility=0, windSpeed=3.32, windDeg=300.0, windGust=4.07, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), clouds=0, pop=0.0, rain=0.0, uvi=9.36),
                DailyWeatherData(dt=1719172800, sunrise=1719146902, sunset=1719199973, temp=TempData(day=29.96, min=14.39, max=29.96, night=15.1, eve=19.47, morn=17.79), feelsLike=FeelsLikeData(day=28.46, night=14.55, eve=18.93, morn=17.14), pressure=1010, humidity=26, dewPoint=8.0, visibility=0, windSpeed=3.64, windDeg=294.0, windGust=4.27, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), clouds=0, pop=0.0, rain=0.0, uvi=10.14),
                DailyWeatherData(dt=1719259200, sunrise=1719233319, sunset=1719286380, temp=TempData(day=28.96, min=13.27, max=28.96, night=15.17, eve=19.61, morn=16.65), feelsLike=FeelsLikeData(day=27.68, night=14.54, eve=19.04, morn=15.96), pressure=1010, humidity=27, dewPoint=7.88, visibility=0, windSpeed=3.56, windDeg=311.0, windGust=3.76, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), clouds=0, pop=0.0, rain=0.0, uvi=11.0),
                DailyWeatherData(dt=1719345600, sunrise=1719319738, sunset=1719372786, temp=TempData(day=30.39, min=13.28, max=30.39, night=16.77, eve=21.13, morn=17.61), feelsLike=FeelsLikeData(day=28.56, night=16.1, eve=20.5, morn=16.81), pressure=1011, humidity=20, dewPoint=5.82, visibility=0, windSpeed=4.07, windDeg=309.0, windGust=4.42, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), clouds=0, pop=0.0, rain=0.0, uvi=11.0),
                DailyWeatherData(dt=1719432000, sunrise=1719406158, sunset=1719459189, temp=TempData(day=28.8, min=14.74, max=28.8, night=16.56, eve=20.54, morn=18.2), feelsLike=FeelsLikeData(day=27.31, night=15.86, eve=19.77, morn=17.51), pressure=1015, humidity=20, dewPoint=4.71, visibility=0, windSpeed=5.01, windDeg=307.0, windGust=7.55, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), clouds=0, pop=0.0, rain=0.0, uvi=11.0)
            )
        }

        fun generateHourlyWeatherData(): List<HourlyWeatherData> {
            return listOf(
                HourlyWeatherData(dt=1718841600, temp=20.88, feelsLike=20.38, pressure=1011, humidity=52, dewPoint=10.67, clouds=9, visibility=10000, windSpeed=4.18, windDeg=297.0, windGust=4.86, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718845200, temp=20.37, feelsLike=19.98, pressure=1011, humidity=58, dewPoint=11.84, clouds=0, visibility=10000, windSpeed=3.61, windDeg=294.0, windGust=4.47, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718848800, temp=20.19, feelsLike=19.7, pressure=1011, humidity=55, dewPoint=10.87, clouds=19, visibility=10000, windSpeed=3.19, windDeg=293.0, windGust=3.89, weatherList=listOf(WeatherData(id=801, main="Clouds", description="algumas nuvens", icon="02d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718852400, temp=19.04, feelsLike=18.49, pressure=1011, humidity=57, dewPoint=10.34, clouds=33, visibility=10000, windSpeed=2.95, windDeg=291.0, windGust=3.7, weatherList=listOf(WeatherData(id=802, main="Clouds", description="nuvens dispersas", icon="03d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718856000, temp=17.04, feelsLike=16.47, pressure=1012, humidity=64, dewPoint=10.19, clouds=48, visibility=10000, windSpeed=2.55, windDeg=281.0, windGust=3.08, weatherList=listOf(WeatherData(id=802, main="Clouds", description="nuvens dispersas", icon="03n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718859600, temp=15.28, feelsLike=14.69, pressure=1013, humidity=70, dewPoint=9.85, clouds=54, visibility=10000, windSpeed=1.99, windDeg=269.0, windGust=2.55, weatherList=listOf(WeatherData(id=803, main="Clouds", description="nublado", icon="04n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718863200, temp=13.64, feelsLike=12.99, pressure=1013, humidity=74, dewPoint=8.27, clouds=59, visibility=10000, windSpeed=1.53, windDeg=244.0, windGust=1.76, weatherList=listOf(WeatherData(id=803, main="Clouds", description="nublado", icon="04n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718866800, temp=13.57, feelsLike=12.94, pressure=1013, humidity=75, dewPoint=8.23, clouds=10, visibility=10000, windSpeed=1.38, windDeg=239.0, windGust=1.46, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718870400, temp=13.35, feelsLike=12.73, pressure=1012, humidity=76, dewPoint=8.32, clouds=7, visibility=10000, windSpeed=1.67, windDeg=256.0, windGust=1.75, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718874000, temp=12.92, feelsLike=12.28, pressure=1012, humidity=77, dewPoint=8.17, clouds=6, visibility=10000, windSpeed=1.52, windDeg=266.0, windGust=1.7, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718877600, temp=12.64, feelsLike=12.02, pressure=1011, humidity=79, dewPoint=8.2, clouds=5, visibility=10000, windSpeed=0.62, windDeg=238.0, windGust=0.83, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718881200, temp=12.47, feelsLike=11.84, pressure=1011, humidity=79, dewPoint=8.28, clouds=5, visibility=10000, windSpeed=2.04, windDeg=268.0, windGust=2.09, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718884800, temp=12.05, feelsLike=11.43, pressure=1011, humidity=81, dewPoint=8.09, clouds=5, visibility=10000, windSpeed=1.28, windDeg=272.0, windGust=2.01, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718888400, temp=11.94, feelsLike=11.3, pressure=1012, humidity=81, dewPoint=7.96, clouds=3, visibility=10000, windSpeed=0.45, windDeg=239.0, windGust=0.78, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718892000, temp=12.73, feelsLike=12.12, pressure=1012, humidity=79, dewPoint=8.53, clouds=1, visibility=10000, windSpeed=1.17, windDeg=254.0, windGust=1.17, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718895600, temp=15.06, feelsLike=14.4, pressure=1012, humidity=68, dewPoint=8.38, clouds=1, visibility=10000, windSpeed=1.23, windDeg=262.0, windGust=1.46, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718899200, temp=18.31, feelsLike=17.58, pressure=1012, humidity=53, dewPoint=8.08, clouds=8, visibility=10000, windSpeed=0.23, windDeg=293.0, windGust=0.71, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718902800, temp=20.81, feelsLike=20.1, pressure=1011, humidity=44, dewPoint=7.6, clouds=27, visibility=10000, windSpeed=1.17, windDeg=309.0, windGust=1.23, weatherList=listOf(WeatherData(id=802, main="Clouds", description="nuvens dispersas", icon="03d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718906400, temp=22.5, feelsLike=21.82, pressure=1011, humidity=39, dewPoint=7.6, clouds=25, visibility=10000, windSpeed=2.21, windDeg=322.0, windGust=1.72, weatherList=listOf(WeatherData(id=802, main="Clouds", description="nuvens dispersas", icon="03d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718910000, temp=24.59, feelsLike=23.99, pressure=1011, humidity=34, dewPoint=7.39, clouds=10, visibility=10000, windSpeed=2.63, windDeg=324.0, windGust=1.91, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718913600, temp=26.23, feelsLike=26.23, pressure=1010, humidity=30, dewPoint=7.13, clouds=22, visibility=10000, windSpeed=2.49, windDeg=318.0, windGust=2.04, weatherList=listOf(WeatherData(id=801, main="Clouds", description="algumas nuvens", icon="02d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718917200, temp=26.38, feelsLike=26.38, pressure=1010, humidity=30, dewPoint=7.45, clouds=9, visibility=10000, windSpeed=4.04, windDeg=316.0, windGust=3.2, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718920800, temp=26.03, feelsLike=26.03, pressure=1009, humidity=31, dewPoint=7.24, clouds=5, visibility=10000, windSpeed=3.84, windDeg=309.0, windGust=3.59, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718924400, temp=25.13, feelsLike=24.56, pressure=1009, humidity=33, dewPoint=7.11, clouds=3, visibility=10000, windSpeed=3.87, windDeg=302.0, windGust=3.58, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718928000, temp=23.51, feelsLike=22.91, pressure=1009, humidity=38, dewPoint=7.71, clouds=1, visibility=10000, windSpeed=4.1, windDeg=300.0, windGust=3.74, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718931600, temp=21.74, feelsLike=21.12, pressure=1009, humidity=44, dewPoint=8.18, clouds=0, visibility=10000, windSpeed=3.69, windDeg=299.0, windGust=3.74, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718935200, temp=19.91, feelsLike=19.29, pressure=1010, humidity=51, dewPoint=8.59, clouds=0, visibility=10000, windSpeed=3.1, windDeg=297.0, windGust=3.19, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718938800, temp=17.39, feelsLike=16.8, pressure=1010, humidity=62, dewPoint=9.01, clouds=0, visibility=10000, windSpeed=3.08, windDeg=292.0, windGust=3.18, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718942400, temp=15.14, feelsLike=14.54, pressure=1011, humidity=70, dewPoint=8.76, clouds=0, visibility=10000, windSpeed=2.92, windDeg=288.0, windGust=3.16, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718946000, temp=14.23, feelsLike=13.64, pressure=1011, humidity=74, dewPoint=8.82, clouds=0, visibility=10000, windSpeed=2.27, windDeg=283.0, windGust=2.24, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718949600, temp=13.87, feelsLike=13.27, pressure=1011, humidity=75, dewPoint=8.64, clouds=0, visibility=10000, windSpeed=2.07, windDeg=275.0, windGust=1.94, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718953200, temp=13.43, feelsLike=12.84, pressure=1011, humidity=77, dewPoint=8.71, clouds=3, visibility=10000, windSpeed=1.9, windDeg=272.0, windGust=1.92, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718956800, temp=13.12, feelsLike=12.55, pressure=1010, humidity=79, dewPoint=8.64, clouds=2, visibility=10000, windSpeed=1.58, windDeg=264.0, windGust=1.76, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718960400, temp=12.69, feelsLike=12.1, pressure=1010, humidity=80, dewPoint=8.65, clouds=3, visibility=10000, windSpeed=1.6, windDeg=263.0, windGust=1.8, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718964000, temp=12.52, feelsLike=11.94, pressure=1009, humidity=81, dewPoint=8.6, clouds=3, visibility=10000, windSpeed=1.18, windDeg=245.0, windGust=1.38, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718967600, temp=12.25, feelsLike=11.67, pressure=1009, humidity=82, dewPoint=8.61, clouds=2, visibility=10000, windSpeed=1.14, windDeg=250.0, windGust=1.41, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718971200, temp=11.98, feelsLike=11.4, pressure=1009, humidity=83, dewPoint=8.39, clouds=1, visibility=10000, windSpeed=0.88, windDeg=227.0, windGust=1.05, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718974800, temp=11.91, feelsLike=11.32, pressure=1009, humidity=83, dewPoint=8.39, clouds=0, visibility=10000, windSpeed=0.86, windDeg=222.0, windGust=1.02, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718978400, temp=13.25, feelsLike=12.64, pressure=1009, humidity=77, dewPoint=8.71, clouds=0, visibility=10000, windSpeed=0.65, windDeg=214.0, windGust=0.95, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718982000, temp=16.01, feelsLike=15.31, pressure=1009, humidity=63, dewPoint=8.63, clouds=0, visibility=10000, windSpeed=0.51, windDeg=255.0, windGust=0.86, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718985600, temp=19.53, feelsLike=18.82, pressure=1009, humidity=49, dewPoint=8.2, clouds=0, visibility=10000, windSpeed=0.44, windDeg=309.0, windGust=0.86, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718989200, temp=22.6, feelsLike=21.93, pressure=1009, humidity=39, dewPoint=7.82, clouds=0, visibility=10000, windSpeed=1.41, windDeg=333.0, windGust=1.0, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718992800, temp=24.89, feelsLike=24.3, pressure=1009, humidity=33, dewPoint=7.42, clouds=0, visibility=10000, windSpeed=2.21, windDeg=341.0, windGust=1.23, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1718996400, temp=27.23, feelsLike=26.53, pressure=1008, humidity=29, dewPoint=7.06, clouds=0, visibility=10000, windSpeed=2.57, windDeg=338.0, windGust=1.52, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1719000000, temp=29.28, feelsLike=27.85, pressure=1008, humidity=25, dewPoint=7.05, clouds=0, visibility=10000, windSpeed=2.88, windDeg=330.0, windGust=1.84, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1719003600, temp=30.39, feelsLike=28.68, pressure=1007, humidity=23, dewPoint=7.44, clouds=0, visibility=10000, windSpeed=3.37, windDeg=323.0, windGust=2.35, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1719007200, temp=30.6, feelsLike=28.91, pressure=1007, humidity=24, dewPoint=8.02, clouds=0, visibility=10000, windSpeed=3.67, windDeg=317.0, windGust=3.03, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null),
                HourlyWeatherData(dt=1719010800, temp=30.0, feelsLike=28.44, pressure=1006, humidity=25, dewPoint=8.5, clouds=0, visibility=10000, windSpeed=3.48, windDeg=312.0, windGust=3.35, weatherList=listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01d")), pop=0.0, rain=null)
            )
        }

        fun generateWeatherData(): List<WeatherData> {
            return listOf(WeatherData(id=800, main="Clear", description="céu limpo", icon="01n"))
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

        fun generateDailyWeatherCacheList(): List<DailyWeatherCache> {
            return listOf(
                DailyWeatherCache(dt=1718827200, minTemp=12.13, maxTemp=24.41, iconUrl="http://openweathermap.org/img/wn/03d@2x.png", uvi=8.29),
                DailyWeatherCache(dt=1718913600, minTemp=12.06, maxTemp=26.59, iconUrl="http://openweathermap.org/img/wn/01d@2x.png", uvi=8.1),
                DailyWeatherCache(dt=1719000000, minTemp=11.98, maxTemp=31.27, iconUrl="http://openweathermap.org/img/wn/01d@2x.png", uvi=8.79),
                DailyWeatherCache(dt=1719086400, minTemp=14.9, maxTemp=32.58, iconUrl="http://openweathermap.org/img/wn/01d@2x.png", uvi=9.36),
                DailyWeatherCache(dt=1719172800, minTemp=13.94, maxTemp=28.43, iconUrl="http://openweathermap.org/img/wn/01d@2x.png", uvi=10.14),
                DailyWeatherCache(dt=1719259200, minTemp=13.25, maxTemp=29.2, iconUrl="http://openweathermap.org/img/wn/01d@2x.png", uvi=11.0),
                DailyWeatherCache(dt=1719345600, minTemp=12.86, maxTemp=28.83, iconUrl="http://openweathermap.org/img/wn/01d@2x.png", uvi=11.0),
                DailyWeatherCache(dt=1719432000, minTemp=13.8, maxTemp=27.63, iconUrl="http://openweathermap.org/img/wn/02d@2x.png", uvi=11.0)
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

        fun generateWeatherCache(): List<WeatherCache> {
            return listOf(WeatherCache(id=800, main="Clear", description="céu limpo", icon="01d"))
        }
    }
}