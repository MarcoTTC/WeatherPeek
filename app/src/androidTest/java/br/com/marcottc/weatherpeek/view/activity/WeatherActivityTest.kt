package br.com.marcottc.weatherpeek.view.activity

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import br.com.marcottc.weatherpeek.R
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class WeatherActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(WeatherActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )

    private lateinit var requestIdlingResource: IdlingResource
    private lateinit var weatherActivity: WeatherActivity

    @Before
    fun registerIdlingResource() {
        mActivityScenarioRule.scenario.onActivity { weatherActivity ->
            this.weatherActivity = weatherActivity
            requestIdlingResource = weatherActivity.requestingWeatherDataIdlingResource
        }
        IdlingRegistry.getInstance().register(requestIdlingResource)
    }

    @Test
    fun weatherActivity_isRequestingWeatherData() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)

        onView(withId(R.id.hourly_forecast_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_forecast_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.uvi_forecast_loading_symbol)).check(matches(isDisplayed()))

        onView(withId(R.id.hourly_forecast_recycler_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.daily_forecast_recycler_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.uvi_meter)).check(matches(not(isDisplayed())))

        onView(withId(R.id.hourly_forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.hourly_forecast_reload_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.daily_forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.daily_forecast_reload_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.uvi_forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.uvi_forecast_reload_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun weatherActivity_hasRequestedWeatherData() {
        onView(withId(R.id.hourly_forecast_loading_symbol)).check(matches(not(isDisplayed())))
        onView(withId(R.id.daily_forecast_loading_symbol)).check(matches(not(isDisplayed())))
        onView(withId(R.id.uvi_forecast_loading_symbol)).check(matches(not(isDisplayed())))

        onView(withId(R.id.hourly_forecast_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.hourly_forecast_recycler_view)).check(
            matches(hasMinimumChildCount(5))
        )
        onView(withId(R.id.hourly_forecast_recycler_view)).perform(
            scrollToLastPosition<RecyclerView.ViewHolder>()
        )
        onView(withId(R.id.hourly_forecast_recycler_view)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(0)
        )

        onView(withId(R.id.daily_forecast_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_forecast_recycler_view)).check(
            matches(hasMinimumChildCount(5))
        )
        onView(withId(R.id.daily_forecast_recycler_view)).perform(
            scrollToLastPosition<RecyclerView.ViewHolder>()
        )
        onView(withId(R.id.daily_forecast_recycler_view)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(0)
        )

        onView(withId(R.id.uvi_meter)).check(matches(isDisplayed()))

        onView(withId(R.id.hourly_forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.hourly_forecast_reload_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.daily_forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.daily_forecast_reload_text)).check(matches(not(isDisplayed())))
        onView(withId(R.id.uvi_forecast_reload_icon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.uvi_forecast_reload_text)).check(matches(not(isDisplayed())))
    }

    @Test
    fun weatherActivity_finishActivity() {
        onView(withId(R.id.close_icon)).perform(click())

        assertTrue(weatherActivity.isFinishing)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)
    }

}