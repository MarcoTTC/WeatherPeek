package br.com.marcottc.weatherpeek

import br.com.marcottc.weatherpeek.mock.MockGenerator
import br.com.marcottc.weatherpeek.model.dto.CurrentWeatherData
import br.com.marcottc.weatherpeek.model.dto.DailyWeatherData
import br.com.marcottc.weatherpeek.model.dto.HourlyWeatherData
import br.com.marcottc.weatherpeek.model.dto.OneCallTimelineDTO
import br.com.marcottc.weatherpeek.network.datasource.OneCallRemoteDataSource
import br.com.marcottc.weatherpeek.network.service.OneCallService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class OneCallRemoteDataSourceTest {

    private val service: OneCallService = mockk()
    private val dataSource = OneCallRemoteDataSource(service)

    private fun <T> envelope(data: List<T>, next: String? = null) = OneCallTimelineDTO(
        37.422, -122.084, "America/Los_Angeles", -25200, data, null, next
    )

    private fun hours(from: Long, count: Int) =
        (0 until count).map { HourlyWeatherData(from + it * 3600L, 20.0) }

    private fun <T> errorBody(code: Int): Response<T> = Response.error(
        code,
        """{"cod":$code,"message":"error $code"}""".toResponseBody("application/json".toMediaType())
    )

    private fun stubCurrentAndDaily(
        current: List<CurrentWeatherData> = listOf(MockGenerator.generateCurrentWeatherData())
    ) {
        coEvery { service.getCurrentWeather(any(), any(), any(), any(), any()) } returns
                Response.success(envelope(current))
        coEvery { service.getDailyTimeline(any(), any(), any(), any(), any(), any()) } returns
                Response.success(envelope<DailyWeatherData>(MockGenerator.generateDailyWeatherData()))
    }

    @Test
    fun getWeatherData_assemblesTheSnapshotAndPagesTheHourlyTimelineUpTo48Hours() = runTest {
        stubCurrentAndDaily()
        val start = 1718845200L
        coEvery { service.getHourlyTimeline(any(), any(), null, any(), any(), any()) } returns
                Response.success(envelope(hours(start, 20), next = "next"))
        coEvery { service.getHourlyTimeline(any(), any(), start + 20 * 3600L, any(), any(), any()) } returns
                Response.success(envelope(hours(start + 20 * 3600L, 20), next = "next"))
        coEvery { service.getHourlyTimeline(any(), any(), start + 40 * 3600L, any(), any(), any()) } returns
                Response.success(envelope(hours(start + 40 * 3600L, 20), next = "next"))

        val response = dataSource.getWeatherData(37.422, -122.084, "key")

        assertTrue(response.isSuccessful)
        val body = response.body()!!
        assertEquals(37.422, body.lat, 0.0001)
        assertEquals(-25200, body.timezoneOffset)
        assertEquals(MockGenerator.generateCurrentWeatherData(), body.current)
        assertEquals(48, body.hourlyDataList.size)
        assertEquals(start, body.hourlyDataList.first().dt)
        assertEquals(start + 47 * 3600L, body.hourlyDataList.last().dt)
        assertEquals((0 until 48).map { start + it * 3600L }, body.hourlyDataList.map { it.dt })
        assertEquals(MockGenerator.generateDailyWeatherData().size, body.dailyDataList.size)
        coVerify(exactly = 3) { service.getHourlyTimeline(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun getWeatherData_stopsPagingWhenThereIsNoNextPage() = runTest {
        stubCurrentAndDaily()
        coEvery { service.getHourlyTimeline(any(), any(), null, any(), any(), any()) } returns
                Response.success(envelope(hours(1718845200L, 12)))

        val response = dataSource.getWeatherData(37.422, -122.084, "key")

        assertEquals(12, response.body()!!.hourlyDataList.size)
        coVerify(exactly = 1) { service.getHourlyTimeline(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun getWeatherData_keepsTheReceivedHoursWhenAFollowUpPageFails() = runTest {
        stubCurrentAndDaily()
        coEvery { service.getHourlyTimeline(any(), any(), null, any(), any(), any()) } returns
                Response.success(envelope(hours(1718845200L, 20), next = "next"))
        coEvery { service.getHourlyTimeline(any(), any(), match { it != null }, any(), any(), any()) } returns
                errorBody(429)

        val response = dataSource.getWeatherData(37.422, -122.084, "key")

        assertTrue(response.isSuccessful)
        assertEquals(20, response.body()!!.hourlyDataList.size)
    }

    @Test
    fun getWeatherData_returnsTheErrorOfAFailingRequiredCall() = runTest {
        stubCurrentAndDaily()
        coEvery { service.getCurrentWeather(any(), any(), any(), any(), any()) } returns errorBody(401)
        coEvery { service.getHourlyTimeline(any(), any(), any(), any(), any(), any()) } returns
                Response.success(envelope(hours(1718845200L, 5)))

        val response = dataSource.getWeatherData(37.422, -122.084, "key")

        assertFalse(response.isSuccessful)
        assertEquals(401, response.code())
        assertTrue(response.errorBody()!!.string().contains("error 401"))
    }

    @Test
    fun getWeatherData_withoutACurrentRecord_succeedsWithoutBody() = runTest {
        stubCurrentAndDaily(current = emptyList())
        coEvery { service.getHourlyTimeline(any(), any(), any(), any(), any(), any()) } returns
                Response.success(envelope(hours(1718845200L, 5)))

        val response = dataSource.getWeatherData(37.422, -122.084, "key")

        assertTrue(response.isSuccessful)
        assertNull(response.body())
    }
}
