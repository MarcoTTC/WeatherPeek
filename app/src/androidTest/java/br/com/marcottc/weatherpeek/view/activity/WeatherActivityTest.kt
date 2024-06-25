package br.com.marcottc.weatherpeek.view.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import br.com.marcottc.weatherpeek.R
import org.junit.After
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

    @Before
    fun registerIdlingResource() {
        mActivityScenarioRule.scenario.onActivity { mainActivity ->
            requestIdlingResource = mainActivity.requestingWeatherDataIdlingResource
        }
        IdlingRegistry.getInstance().register(requestIdlingResource)
    }

    @Test
    fun weatherActivity_isRequestingWeatherData() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)

        onView(withId(R.id.hourly_forecast_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_forecast_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.uvi_forecast_loading_symbol)).check(matches(isDisplayed()))
    }

    @Test
    fun weatherActivity_hasRequestedWeatherData() {
        onView(withId(R.id.hourly_forecast_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.daily_forecast_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.uvi_meter)).check(matches(isDisplayed()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)
    }

}