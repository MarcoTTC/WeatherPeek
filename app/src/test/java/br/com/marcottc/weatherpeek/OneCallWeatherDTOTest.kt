package br.com.marcottc.weatherpeek

import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Parsing tests against a One Call 3.0 "current and forecasts weather data" payload, shaped after
// https://openweathermap.org/api/one-call-3 with units=metric.
//
// The payload deliberately carries every section and field that this app has no view for
// ("minutely", "alerts", "daily.summary", "daily.moon_phase", "daily.moonrise", "daily.moonset",
// "daily.snow", "current.uvi", "hourly.uvi", "hourly.snow"), so that dropping them stays a tested
// behaviour instead of an accident.
class OneCallWeatherDTOTest {

    private lateinit var gson: Gson

    @Before
    fun setup() {
        gson = Gson()
    }

    @Test
    fun parse_fullResponse_mapsTheFieldsBackingAView() {
        val dto = gson.fromJson(oneCallThreeResponse, OneCallWeatherDTO::class.java)

        assertEquals(37.422, dto.lat, 0.0001)
        assertEquals(-122.084, dto.lon, 0.0001)
        assertEquals("America/Los_Angeles", dto.timezone)

        val current = dto.current
        assertEquals(1718844339L, current.dt)
        assertEquals(1718801250L, current.sunrise)
        assertEquals(1718854329L, current.sunset)
        assertEquals(20.37, current.temp, 0.0001)
        assertEquals(58, current.humidity)
        assertEquals(40, current.clouds)
        assertEquals(8.75, current.windSpeed, 0.0001)
        assertEquals(350.0, current.windDeg, 0.0001)
        assertEquals(12.35, current.windGust, 0.0001)
        assertEquals(2.93, current.rain!!.oneHour, 0.0001)
        assertEquals(1.17, current.snow!!.oneHour, 0.0001)
        assertEquals(1, current.weatherList.size)
        assertEquals(500, current.weatherList[0].id)
        assertEquals("Rain", current.weatherList[0].main)
        assertEquals("chuva leve", current.weatherList[0].description)
        assertEquals("10d", current.weatherList[0].icon)

        assertEquals(2, dto.hourlyDataList.size)
        assertEquals(1718845200L, dto.hourlyDataList[0].dt)
        assertEquals(21.55, dto.hourlyDataList[0].temp, 0.0001)

        assertEquals(2, dto.dailyDataList.size)
        assertEquals(1718827200L, dto.dailyDataList[0].dt)
        assertEquals(12.13, dto.dailyDataList[0].temp.min, 0.0001)
        assertEquals(25.24, dto.dailyDataList[0].temp.max, 0.0001)
        assertEquals(8.29, dto.dailyDataList[0].uvi, 0.0001)
    }

    @Test
    fun parse_fullResponse_feedsTheCacheObjectsBehindEachView() {
        val dto = gson.fromJson(oneCallThreeResponse, OneCallWeatherDTO::class.java)

        // What MainActivity renders on its current weather, wind and precipitation cards
        val currentCache = CurrentWeatherCache(dto)
        assertEquals(1718844339L, currentCache.dt)
        assertEquals(37.422, currentCache.latitude, 0.0001)
        assertEquals(-122.084, currentCache.longitude, 0.0001)
        assertEquals(20.37, currentCache.temp, 0.0001)
        assertEquals(8.75, currentCache.windSpeed, 0.0001)
        assertEquals(12.35, currentCache.windGust, 0.0001)
        assertEquals(2.93, currentCache.rainAmount, 0.0001)
        assertEquals(1.17, currentCache.snowAmount, 0.0001)
        assertEquals(58, currentCache.humidity)
        assertEquals(40, currentCache.clouds)

        val weatherCache = WeatherCache(dto.current.weatherList[0])
        assertEquals("Rain", weatherCache.main)
        assertEquals("http://openweathermap.org/img/wn/10d@2x.png", weatherCache.getIconUrl())

        // What the hourly forecast RecyclerView renders
        val hourlyCache = HourlyWeatherCache(dto.hourlyDataList[0])
        assertEquals(1718845200L, hourlyCache.dt)
        assertEquals(21.55, hourlyCache.temp, 0.0001)

        // What the daily forecast RecyclerView and the UviMeter render
        val dailyCache = DailyWeatherCache(dto.dailyDataList[0])
        assertEquals(1718827200L, dailyCache.dt)
        assertEquals(12.13, dailyCache.minTemp, 0.0001)
        assertEquals(25.24, dailyCache.maxTemp, 0.0001)
        assertEquals("http://openweathermap.org/img/wn/10d@2x.png", dailyCache.iconUrl)
        assertEquals(8.29, dailyCache.uvi, 0.0001)
    }

    @Test
    fun parse_responseWithoutTheWhereAvailableFields_fallsBackToZero() {
        val dto = gson.fromJson(oneCallThreeResponse, OneCallWeatherDTO::class.java)

        // "wind_gust", "rain" and "snow" are only sent by One Call 3.0 where available. The second
        // entry of each forecast list omits all of them, the way a dry and gustless hour does.
        assertNull(dto.hourlyDataList[1].rain)
        assertEquals(0.0, dto.hourlyDataList[1].windGust, 0.0001)
        assertEquals(0.0, dto.dailyDataList[1].rain, 0.0001)
        assertEquals(0.0, dto.dailyDataList[1].windGust, 0.0001)

        val currentWithoutPrecipitation = gson.fromJson(
            oneCallThreeResponseWithoutPrecipitation,
            OneCallWeatherDTO::class.java
        )
        assertNull(currentWithoutPrecipitation.current.rain)
        assertNull(currentWithoutPrecipitation.current.snow)
        assertEquals(0.0, currentWithoutPrecipitation.current.windGust, 0.0001)

        val currentCache = CurrentWeatherCache(currentWithoutPrecipitation)
        assertEquals(0.0, currentCache.rainAmount, 0.0001)
        assertEquals(0.0, currentCache.snowAmount, 0.0001)
        assertEquals(0.0, currentCache.windGust, 0.0001)
    }

    @Test
    fun parse_responseCarryingTheSectionsWithoutAView_doesNotFail() {
        // "minutely" and "alerts" are excluded by OneCallService, but a response carrying them
        // anyway must still be parsed, and silently dropped, rather than blow the request up.
        val dto = gson.fromJson(oneCallThreeResponse, OneCallWeatherDTO::class.java)

        assertNotNull(dto.current)
        assertEquals(2, dto.hourlyDataList.size)
        assertEquals(2, dto.dailyDataList.size)
    }

    companion object {
        private const val oneCallThreeResponse = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "current": {
    "dt": 1718844339,
    "sunrise": 1718801250,
    "sunset": 1718854329,
    "temp": 20.37,
    "feels_like": 19.98,
    "pressure": 1011,
    "humidity": 58,
    "dew_point": 11.84,
    "uvi": 6.12,
    "clouds": 40,
    "visibility": 10000,
    "wind_speed": 8.75,
    "wind_deg": 350,
    "wind_gust": 12.35,
    "rain": { "1h": 2.93 },
    "snow": { "1h": 1.17 },
    "weather": [
      { "id": 500, "main": "Rain", "description": "chuva leve", "icon": "10d" }
    ]
  },
  "minutely": [
    { "dt": 1718844360, "precipitation": 2.928 },
    { "dt": 1718844420, "precipitation": 1.484 }
  ],
  "hourly": [
    {
      "dt": 1718845200,
      "temp": 21.55,
      "feels_like": 21.1,
      "pressure": 1011,
      "humidity": 55,
      "dew_point": 11.9,
      "uvi": 6.12,
      "clouds": 40,
      "visibility": 10000,
      "wind_speed": 3.14,
      "wind_deg": 302,
      "wind_gust": 4.02,
      "pop": 0.42,
      "rain": { "1h": 0.46 },
      "snow": { "1h": 0.11 },
      "weather": [
        { "id": 500, "main": "Rain", "description": "chuva leve", "icon": "10d" }
      ]
    },
    {
      "dt": 1718848800,
      "temp": 20.11,
      "feels_like": 19.72,
      "pressure": 1012,
      "humidity": 60,
      "dew_point": 12.02,
      "uvi": 4.31,
      "clouds": 22,
      "visibility": 10000,
      "wind_speed": 2.55,
      "wind_deg": 281,
      "pop": 0,
      "weather": [
        { "id": 801, "main": "Clouds", "description": "algumas nuvens", "icon": "02d" }
      ]
    }
  ],
  "daily": [
    {
      "dt": 1718827200,
      "sunrise": 1718801250,
      "sunset": 1718854329,
      "moonrise": 1718845080,
      "moonset": 1718800680,
      "moon_phase": 0.47,
      "summary": "Espere um dia de chuva leve com temperatura amena",
      "temp": {
        "day": 25.24, "min": 12.13, "max": 25.24,
        "night": 13.64, "eve": 20.19, "morn": 13.57
      },
      "feels_like": { "day": 24.39, "night": 12.99, "eve": 19.7, "morn": 12.65 },
      "pressure": 1012,
      "humidity": 22,
      "dew_point": 2.12,
      "wind_speed": 4.51,
      "wind_deg": 296,
      "wind_gust": 5.09,
      "clouds": 49,
      "pop": 0.85,
      "rain": 12.57,
      "snow": 3.41,
      "uvi": 8.29,
      "weather": [
        { "id": 500, "main": "Rain", "description": "chuva leve", "icon": "10d" }
      ]
    },
    {
      "dt": 1718913600,
      "sunrise": 1718887660,
      "sunset": 1718940742,
      "moonrise": 1718934600,
      "moonset": 1718888400,
      "moon_phase": 0.5,
      "summary": "Espere um dia de céu limpo",
      "temp": {
        "day": 26.23, "min": 11.94, "max": 26.38,
        "night": 13.87, "eve": 19.91, "morn": 12.73
      },
      "feels_like": { "day": 26.23, "night": 13.27, "eve": 19.29, "morn": 12.12 },
      "pressure": 1010,
      "humidity": 30,
      "dew_point": 7.13,
      "wind_speed": 4.1,
      "wind_deg": 300,
      "clouds": 0,
      "pop": 0,
      "uvi": 8.1,
      "weather": [
        { "id": 800, "main": "Clear", "description": "céu limpo", "icon": "01d" }
      ]
    }
  ],
  "alerts": [
    {
      "sender_name": "NWS Tulsa",
      "event": "Heat Advisory",
      "start": 1718841600,
      "end": 1718928000,
      "description": "HEAT ADVISORY REMAINS IN EFFECT",
      "tags": ["Extreme temperature value"]
    }
  ]
}
"""

        private const val oneCallThreeResponseWithoutPrecipitation = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "current": {
    "dt": 1718844339,
    "sunrise": 1718801250,
    "sunset": 1718854329,
    "temp": 20.37,
    "feels_like": 19.98,
    "pressure": 1011,
    "humidity": 58,
    "dew_point": 11.84,
    "uvi": 6.12,
    "clouds": 0,
    "visibility": 10000,
    "wind_speed": 8.75,
    "wind_deg": 350,
    "weather": [
      { "id": 800, "main": "Clear", "description": "céu limpo", "icon": "01d" }
    ]
  },
  "hourly": [],
  "daily": []
}
"""
    }
}
