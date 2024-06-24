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
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

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
    fun mainActivity_isRequestingWeatherData() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)

        onView(withId(R.id.current_weather_card_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_card_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.precipitation_card_loading_symbol)).check(matches(isDisplayed()))
        onView(withId(R.id.forecast_loading_symbol)).check(matches(isDisplayed()))
    }

    @Test
    fun mainActivity_hasRequestedWeatherData() {
        onView(withId(R.id.temperature_value)).check(matches(isDisplayed()))
        onView(withId(R.id.wind_speed_amount)).check(matches(isDisplayed()))
        onView(withId(R.id.precipitation_amount)).check(matches(isDisplayed()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(requestIdlingResource)
    }

}