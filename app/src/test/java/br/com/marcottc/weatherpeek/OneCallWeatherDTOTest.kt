package br.com.marcottc.weatherpeek

import br.com.marcottc.weatherpeek.model.dco.CurrentWeatherCache
import br.com.marcottc.weatherpeek.model.dco.DailyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.HourlyWeatherCache
import br.com.marcottc.weatherpeek.model.dco.WeatherCache
import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallTimelineDTO
import br.com.marcottc.weatherpeek.model.dto.OneCallWeatherDTO
import br.com.marcottc.weatherpeek.model.room.WeatherPeekConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// Parsing tests against One Call 4.0 "onecall/current", "onecall/timeline/1h" and
// "onecall/timeline/1day" payloads, shaped after https://openweathermap.org/api/one-call-4 with
// units=metric.
//
// The payloads deliberately carry fields this app has no view for ("uvi" on current/hourly,
// "snow" on hourly, the daily moon data), so that dropping them stays a tested behaviour.
class OneCallWeatherDTOTest {

    private lateinit var gson: Gson

    private val currentType = object : TypeToken<OneCallTimelineDTO<CurrentWeatherData>>() {}.type
    private val hourlyType = object : TypeToken<OneCallTimelineDTO<HourlyWeatherData>>() {}.type
    private val dailyType = object : TypeToken<OneCallTimelineDTO<DailyWeatherData>>() {}.type

    @Before
    fun setup() {
        gson = Gson()
    }

    private fun parseSnapshot(currentJson: String = currentResponse): OneCallWeatherDTO {
        val current: OneCallTimelineDTO<CurrentWeatherData> = gson.fromJson(currentJson, currentType)
        val hourly: OneCallTimelineDTO<HourlyWeatherData> = gson.fromJson(hourlyResponse, hourlyType)
        val daily: OneCallTimelineDTO<DailyWeatherData> = gson.fromJson(dailyResponse, dailyType)
        return OneCallWeatherDTO.fromResponses(current, hourly, daily)!!
    }

    @Test
    fun parse_timelineEnvelope_mapsLocationAndPagination() {
        val hourly: OneCallTimelineDTO<HourlyWeatherData> = gson.fromJson(hourlyResponse, hourlyType)

        assertEquals(37.422, hourly.lat, 0.0001)
        assertEquals(-122.084, hourly.lon, 0.0001)
        assertEquals("America/Los_Angeles", hourly.timezone)
        assertEquals(-25200, hourly.timezoneOffset)
        assertNotNull(hourly.next)
        assertNull(hourly.prev)
    }

    @Test
    fun parse_fullResponses_mapsTheFieldsBackingAView() {
        val dto = parseSnapshot()

        assertEquals(37.422, dto.lat, 0.0001)
        assertEquals(-122.084, dto.lon, 0.0001)
        assertEquals("America/Los_Angeles", dto.timezone)
        assertEquals(-25200, dto.timezoneOffset)

        val current = dto.current
        assertEquals(1718844339L, current.dt)
        assertEquals(1718801250L, current.sunrise)
        assertEquals(1718854329L, current.sunset)
        assertEquals(20.37, current.temp, 0.0001)
        // 4.0 sends fractional values for fields that were integers in 3.0
        assertEquals(1017.64, current.pressure, 0.0001)
        assertEquals(57.6, current.humidity, 0.0001)
        assertEquals(40.2, current.clouds, 0.0001)
        assertEquals(9999.5, current.visibility, 0.0001)
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
        assertEquals(listOf("alert-heat-1"), current.alertIds)

        assertEquals(2, dto.hourlyDataList.size)
        assertEquals(1718845200L, dto.hourlyDataList[0].dt)
        assertEquals(21.55, dto.hourlyDataList[0].temp, 0.0001)
        assertEquals(listOf("alert-heat-1"), dto.hourlyDataList[0].alertIds)

        assertEquals(2, dto.dailyDataList.size)
        assertEquals(1718827200L, dto.dailyDataList[0].dt)
        assertEquals(12.13, dto.dailyDataList[0].temp.min, 0.0001)
        assertEquals(25.24, dto.dailyDataList[0].temp.max, 0.0001)
        assertEquals(8.29, dto.dailyDataList[0].uvi, 0.0001)
        assertEquals(9800.4, dto.dailyDataList[0].visibility, 0.0001)
        assertEquals(listOf("alert-heat-1", "alert-flood-2"), dto.dailyDataList[0].alertIds)
    }

    @Test
    fun parse_fullResponses_feedsTheCacheObjectsBehindEachView() {
        val dto = parseSnapshot()

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
        // ...which the cache rounds to the whole numbers the views display
        assertEquals(1018, currentCache.pressure)
        assertEquals(58, currentCache.humidity)
        assertEquals(40, currentCache.clouds)
        assertEquals(listOf("alert-heat-1"), currentCache.alertIds)

        val weatherCache = WeatherCache(dto.current.weatherList[0])
        assertEquals("Rain", weatherCache.main)
        assertEquals("http://openweathermap.org/img/wn/10d@2x.png", weatherCache.getIconUrl())

        // What the hourly forecast RecyclerView renders
        val hourlyCache = HourlyWeatherCache(dto.hourlyDataList[0])
        assertEquals(1718845200L, hourlyCache.dt)
        assertEquals(21.55, hourlyCache.temp, 0.0001)
        assertEquals(listOf("alert-heat-1"), hourlyCache.alertIds)

        // What the daily forecast RecyclerView and the UviMeter render
        val dailyCache = DailyWeatherCache(dto.dailyDataList[0])
        assertEquals(1718827200L, dailyCache.dt)
        assertEquals(12.13, dailyCache.minTemp, 0.0001)
        assertEquals(25.24, dailyCache.maxTemp, 0.0001)
        assertEquals("http://openweathermap.org/img/wn/10d@2x.png", dailyCache.iconUrl)
        assertEquals(8.29, dailyCache.uvi, 0.0001)
        assertEquals(9800, dailyCache.visibility)
        assertEquals(listOf("alert-heat-1", "alert-flood-2"), dailyCache.alertIds)
    }

    @Test
    fun parse_responsesWithoutTheWhereAvailableFields_fallsBackToDefaults() {
        val dto = parseSnapshot(currentResponseWithoutPrecipitation)

        // "wind_gust", "rain", "snow", "visibility" and "alerts" are only sent where available.
        // The second entry of each timeline omits all of them.
        assertNull(dto.hourlyDataList[1].rain)
        assertNull(dto.hourlyDataList[1].alertIds)
        assertEquals(0.0, dto.hourlyDataList[1].windGust, 0.0001)
        assertEquals(0.0, dto.dailyDataList[1].rain, 0.0001)
        assertEquals(0.0, dto.dailyDataList[1].windGust, 0.0001)
        assertEquals(0.0, dto.dailyDataList[1].visibility, 0.0001)

        assertNull(dto.current.rain)
        assertNull(dto.current.snow)
        assertEquals(0.0, dto.current.windGust, 0.0001)

        val currentCache = CurrentWeatherCache(dto)
        assertEquals(0.0, currentCache.rainAmount, 0.0001)
        assertEquals(0.0, currentCache.snowAmount, 0.0001)
        assertEquals(0.0, currentCache.windGust, 0.0001)
        assertEquals(emptyList<String>(), currentCache.alertIds)
        assertEquals(emptyList<String>(), HourlyWeatherCache(dto.hourlyDataList[1]).alertIds)
        assertEquals(emptyList<String>(), DailyWeatherCache(dto.dailyDataList[1]).alertIds)
    }

    @Test
    fun parse_dailyPrecipitation_acceptsBothDocumentedShapes() {
        // The migration guide documents a plain total ("rain": 12.57), the 4.0 field list an
        // object ("rain": {"1h": ...}). Both must parse.
        val daily: OneCallTimelineDTO<DailyWeatherData> = gson.fromJson(dailyResponse, dailyType)
        assertEquals(12.57, daily.data[0].rain, 0.0001)
        assertEquals(3.41, daily.data[0].snow, 0.0001)

        val objectShaped: OneCallTimelineDTO<DailyWeatherData> =
            gson.fromJson(dailyResponseWithObjectPrecipitation, dailyType)
        assertEquals(7.5, objectShaped.data[0].rain, 0.0001)
        assertEquals(0.8, objectShaped.data[0].snow, 0.0001)
    }

    @Test
    fun fromResponses_withoutCurrentRecord_returnsNull() {
        val current: OneCallTimelineDTO<CurrentWeatherData> = gson.fromJson(emptyCurrentResponse, currentType)
        val hourly: OneCallTimelineDTO<HourlyWeatherData> = gson.fromJson(hourlyResponse, hourlyType)
        val daily: OneCallTimelineDTO<DailyWeatherData> = gson.fromJson(dailyResponse, dailyType)

        assertNull(OneCallWeatherDTO.fromResponses(current, hourly, daily))
    }

    @Test
    fun converters_roundTripAlertIds() {
        val converters = WeatherPeekConverters()
        val ids = listOf("alert-heat-1", "id,with,commas")

        assertEquals(ids, converters.toStringList(converters.fromStringList(ids)))
        assertEquals("[]", converters.fromStringList(emptyList()))
        assertEquals(emptyList<String>(), converters.toStringList("[]"))
        assertEquals(emptyList<String>(), converters.toStringList(null))
    }

    companion object {
        private const val currentResponse = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "data": [
    {
      "dt": 1718844339,
      "sunrise": 1718801250,
      "sunset": 1718854329,
      "temp": 20.37,
      "feels_like": 19.98,
      "pressure": 1017.64,
      "humidity": 57.6,
      "dew_point": 11.84,
      "uvi": 6.12,
      "clouds": 40.2,
      "visibility": 9999.5,
      "wind_speed": 8.75,
      "wind_deg": 350,
      "wind_gust": 12.35,
      "rain": { "1h": 2.93 },
      "snow": { "1h": 1.17 },
      "weather": [
        { "id": 500, "main": "Rain", "description": "chuva leve", "icon": "10d" }
      ],
      "alerts": ["alert-heat-1"]
    }
  ]
}
"""

        private const val currentResponseWithoutPrecipitation = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "data": [
    {
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
    }
  ]
}
"""

        private const val emptyCurrentResponse = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "data": []
}
"""

        private const val hourlyResponse = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "data": [
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
      ],
      "alerts": ["alert-heat-1"]
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
  "next": "https://api.openweathermap.org/data/4.0/onecall/timeline/1h?lat=37.422&lon=-122.084&start=1718852400"
}
"""

        private const val dailyResponse = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "data": [
    {
      "dt": 1718827200,
      "sunrise": 1718801250,
      "sunset": 1718854329,
      "moonrise": 1718845080,
      "moonset": 1718800680,
      "moon_phase": 0.47,
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
      "visibility": 9800.4,
      "pop": 0.85,
      "rain": 12.57,
      "snow": 3.41,
      "uvi": 8.29,
      "weather": [
        { "id": 500, "main": "Rain", "description": "chuva leve", "icon": "10d" }
      ],
      "alerts": ["alert-heat-1", "alert-flood-2"]
    },
    {
      "dt": 1718913600,
      "sunrise": 1718887660,
      "sunset": 1718940742,
      "moonrise": 1718934600,
      "moonset": 1718888400,
      "moon_phase": 0.5,
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
  ]
}
"""

        private const val dailyResponseWithObjectPrecipitation = """
{
  "lat": 37.422,
  "lon": -122.084,
  "timezone": "America/Los_Angeles",
  "timezone_offset": -25200,
  "data": [
    {
      "dt": 1718827200,
      "sunrise": 1718801250,
      "sunset": 1718854329,
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
      "clouds": 49,
      "pop": 0.85,
      "rain": { "1h": 7.5 },
      "snow": { "1h": 0.8 },
      "uvi": 8.29,
      "weather": [
        { "id": 500, "main": "Rain", "description": "chuva leve", "icon": "10d" }
      ]
    }
  ]
}
"""
    }
}
